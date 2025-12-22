package com.example.khoitriso.ui.behavior

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import com.example.khoitriso.utils.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    // Channel for navigation events (e.g., token expired, navigate to login)
    protected val _navigationEvents = Channel<NavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()

    protected fun loadUser(
        user: MutableStateFlow<User?>,
        userManager: UserManager,
        onUserLoadFailed: suspend () -> Unit = {}
    ) {
        viewModelScope.launch {
            val loadedUser = userManager.getCurrentUser()
            if (loadedUser != null) {
                user.value = loadedUser
            } else {
                // User not found in local storage, trigger callback
                onUserLoadFailed()
            }
        }
    }

    protected fun <T> loadData(
        stateFlow: MutableStateFlow<UiState<T>>,
        apiCall: suspend () -> Result<T>,
        onSuccess: (UiState.Success<T>) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        viewModelScope.launch {
            stateFlow.value = UiState.Loading
            val result = apiCall()
            result.fold(
                onSuccess = {
                    stateFlow.value = UiState.Success(it)
                    onSuccess(UiState.Success(it))
                },
                onFailure = {
                    stateFlow.value = UiState.Error(it.message ?: "Unknown error")
                    onFailure()
                }
            )
        }
    }

    protected fun <T> loadDataWithNoResult(
        stateFlow: MutableStateFlow<T>,
        apiCall: suspend () -> Result<T>,
    ) {
        viewModelScope.launch {
            try {
                val result = apiCall()
                result.fold(
                    onSuccess = { item ->
                        stateFlow.value = item
                    },
                    onFailure = { exception ->
                        // Nếu thất bại, cập nhật state với thông báo lỗi
                        stateFlow.value = 0 as T
                    }
                )
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
                e.printStackTrace()
            }
        }
    }

    protected fun <T> loadDataWithPage(
        stateFlow: MutableStateFlow<UiState<MyResponese<T>>>,
        apiCall: suspend () -> Result<MyResponese<T>>,
        onFailure: () -> Unit = {},
        onSuccess: (UiState.Success<MyResponese<T>>) -> Unit = {},
    ) {
        viewModelScope.launch {
            stateFlow.value = UiState.Loading
            try {
                val result = apiCall()
                result.fold(
                    onSuccess = { item ->
                        stateFlow.value = UiState.Success(item)
                        onSuccess(UiState.Success(item))
                    },
                    onFailure = { exception ->
                        stateFlow.value = UiState.Error(
                            exception.message ?: ("An unknown error " +
                                    "occurred")
                        )
                        onFailure()
                    }
                )
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
                e.printStackTrace()
                stateFlow.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    protected fun initializePlayerInParent(
        playerState: MutableStateFlow<ExoPlayer?>,
        context: Context,
    ) {
        // Chỉ tạo player nếu nó chưa tồn tại
        if (playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = ExoPlayer.Builder(context).build().also { player ->
                    // Set audio attributes để có âm thanh
                    val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
                        .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MOVIE)
                        .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                        .build()
                    player.setAudioAttributes(audioAttributes, true)
                    
                    // Không set media item ở đây nữa, sẽ set ở hàm changeVideoSource
                    player.playWhenReady = false
                    player.volume = 1f // Đảm bảo volume = 1 (100%)
                    player.addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            handleErrorInparent(error)
                        }
                    })
                }
                playerState.value = exoPlayer
            }
        }
    }

    protected fun changeVideoSource(
        playerState: MutableStateFlow<ExoPlayer?>,
        videoUrl: String,
    ) {
        // Lấy player hiện tại
        val currentPlayer = playerState.value ?: return
        
        // Check if it's YouTube URL - ExoPlayer doesn't support YouTube directly
        // YouTube videos will be handled by WebView in the UI
        if (com.example.khoitriso.utils.YouTubeUtils.isYouTubeUrl(videoUrl)) {
            debug("YouTube URL detected, will use WebView: $videoUrl", "BaseViewModel")
            return
        }
        
        viewModelScope.launch {
            // Tạo media item mới từ URL
            val mediaItem = MediaItem.fromUri(videoUrl.toUri())

            // Dừng video hiện tại (nếu có)
            currentPlayer.stop()

            // Đặt media item mới và chuẩn bị player
            currentPlayer.setMediaItem(mediaItem)
            currentPlayer.prepare()

            // Bạn có thể quyết định có tự động phát hay không ở đây
            // currentPlayer.play()
        }
    }


    protected fun releasePlayerInParent(playerState: MutableStateFlow<ExoPlayer?>) {
        playerState.value?.release()
        playerState.value = null
    }

    protected fun handleErrorInparent(error: PlaybackException) {
        when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                // Handle network connection error
                println("Network connection error")
            }

            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
                // Handle file not found error
                println("File not found")
            }

            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> {
                // Handle decoder initialization error
                println("Decoder initialization error")
            }

            else -> {
                // Handle other types of errors
                println("Other error: ${error.message}")
            }
        }
    }


    fun downloadMaterial(context: Context, url: String, fileName: String,fileType: String) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = url.toUri()

            // 1. Xử lý tên file để tránh lỗi ký tự đặc biệt
            val cleanFileName = fileName.replace("[^a-zA-Z0-9.\\-]".toRegex(), "_") + ".$fileType"

            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url)
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
                ?: "*/*" // Nếu không đoán được thì để mặc định

            val request = DownloadManager.Request(uri)
                .setTitle(fileName) // Tên hiện trên thanh thông báo
                .setDescription("Đang tải xuống...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setMimeType(mimeType)
                // 3. QUAN TRỌNG: Đặt đường dẫn vào thư mục Downloads công khai
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, cleanFileName)
                // 4. Cho phép hiện trong ứng dụng "Downloads" của hệ thống
                .setVisibleInDownloadsUi(true)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            downloadManager.enqueue(request)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// Navigation events for ViewModels
sealed class NavigationEvent {
    object NavigateToLogin : NavigationEvent()
}
