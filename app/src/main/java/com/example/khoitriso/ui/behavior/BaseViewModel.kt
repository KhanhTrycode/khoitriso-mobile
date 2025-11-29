package com.example.khoitriso.ui.behavior

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    // Nên đặt là isTestMode cho rõ ràng hơn
    protected val _isTestMode = true // Dùng protected để lớp con có thể truy cập

    protected fun <T> loadData(
        stateFlow: MutableStateFlow<UiState<T>>,
        mockData: T,
        isTestMode: Boolean = _isTestMode,
        apiCall: suspend () -> Result<T>,
        onSuccess: (UiState.Success<T>) -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        viewModelScope.launch {
            stateFlow.value = UiState.Loading
            val result = if (isTestMode) {
                Result.success(mockData)
            } else {
                apiCall()
            }
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
        mockData: T,
        isTestMode: Boolean = _isTestMode,
        apiCall: suspend () -> Result<T>,
    ) {
        viewModelScope.launch {
            if (isTestMode) {
                stateFlow.value = mockData
            } else {
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
    }

    protected fun <T> loadDataWithPage(
        stateFlow: MutableStateFlow<UiState<MyResponese<T>>>,
        mockData: List<T>,
        isTestMode: Boolean = _isTestMode,
        apiCall: suspend () -> Result<MyResponese<T>>,
    ) {
        viewModelScope.launch {
            stateFlow.value = UiState.Loading
            if (isTestMode) {
                stateFlow.value = UiState.Success(
                    MyResponese(
                        items = mockData,
                        page = 1,
                        pageSize = mockData.size,
                        total = mockData.size,
                        totalPages = 1
                    )
                )
            } else {
                try {
                    val result = apiCall()
                    result.fold(
                        onSuccess = { item ->
                            stateFlow.value = UiState.Success(item)
                        },
                        onFailure = { exception ->
                            // Nếu thất bại, cập nhật state với thông báo lỗi
                            stateFlow.value = UiState.Error(
                                exception.message ?: ("An unknown error " +
                                        "occurred")
                            )
                        }
                    )
                } catch (e: Exception) {
                    // Xử lý lỗi nếu cần
                    e.printStackTrace()
                    UiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }
    protected fun initializePlayerInParent(
        playerState: MutableStateFlow<ExoPlayer?>,
        context: Context
    ) {
        // Chỉ tạo player nếu nó chưa tồn tại
        if (playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = ExoPlayer.Builder(context).build().also {
                    // Không set media item ở đây nữa, sẽ set ở hàm changeVideoSource
                    it.playWhenReady = false
                    it.addListener(object : Player.Listener {
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
        var url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        viewModelScope.launch {
            // Tạo media item mới từ URL
            val mediaItem = MediaItem.fromUri(url.toUri())

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


}
