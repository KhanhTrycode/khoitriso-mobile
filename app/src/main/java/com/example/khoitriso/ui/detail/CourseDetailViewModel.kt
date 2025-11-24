package com.example.khoitriso.ui.detail

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.khoitriso.test.MockData
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.media3.common.PlaybackException
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.ui.behavior.BaseViewModel

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val courseUsecase: CourseUsecase
) : BaseViewModel() {
    private val _course: MutableStateFlow<UiState<CourseDetail>> = MutableStateFlow(UiState.Loading)
    val course: StateFlow<UiState<CourseDetail>> = _course
    val courseId = savedStateHandle.get<Int>("courseId")!!
    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState: StateFlow<ExoPlayer?> = _playerState
    init{
        getCourse()
    }

    private fun getCourse() {
        loadData(
            stateFlow = _course,
            mockData = MockData.mockCourseDetail1,
            apiCall = { courseUsecase.getCourseById(courseId) }
        )
    }

    fun initializePlayer(
        context: Context,
        videoUrl: String,
    ) {
        if (_playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = ExoPlayer.Builder(context).build().also {
                    val mediaItem = MediaItem.fromUri(videoUrl.toUri())
                    it.setMediaItem(mediaItem)
                    it.prepare()
                    it.playWhenReady = false
                    it.addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            handleError(error)
                        }
                    })
                }
                _playerState.value = exoPlayer
            }
        }
    }

    fun releasePlayer() {
        _playerState.value?.release()
        _playerState.value = null
    }

    private fun handleError(error: PlaybackException) {
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

    fun addToCart(){

    }

    fun buyNow(){

    }
}