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
import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.domain.usecase.order.CartUsecase
import com.example.khoitriso.domain.usecase.order.OrderUsecase
import com.example.khoitriso.test.MockData.mockCart
import com.example.khoitriso.test.MockData.mockCartItems
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.ItemType
import com.example.khoitriso.utils.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val courseUsecase: CourseUsecase,
    private val cartUsecase: CartUsecase,
    private val orderUsecase: OrderUsecase,
) : BaseViewModel() {
    private val _course: MutableStateFlow<UiState<CourseDetail>> = MutableStateFlow(UiState.Loading)
    val course: StateFlow<UiState<CourseDetail>> = _course
    val courseId = savedStateHandle.get<Int>("courseId")!!
    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState: StateFlow<ExoPlayer?> = _playerState
    private val _selectedLesson = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = _selectedLesson
    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    init {
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
        initializePlayerInParent(_playerState, context)
        changeVideoSource(_playerState, videoUrl)
    }

    fun releasePlayer() {
        releasePlayerInParent(_playerState)
    }

    fun selectLesson(lesson: Lesson, context: Context) {
        if (lesson.isFree) {
            _selectedLesson.value = lesson
            if (_playerState.value == null) {
                initializePlayer(context, lesson.videoUrl)
            } else {
                changeVideoSource(_playerState, lesson.videoUrl)
            }
        } else {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("Bài học này cần mua khóa học để xem"))
            }
        }
    }

    fun addToCart(itemId: Int) {
        viewModelScope.launch {
            val currentCourse = (_course.value as? UiState.Success)?.data
            val result = if (_isTestMode) {
                Result.success(true)
            } else {
                cartUsecase.addToCart(itemId = itemId, itemType = ItemType.Course)
            }
            result.fold(
                onSuccess = {
                    // Chỉ cập nhật mock data khi ở chế độ test
                    if (_isTestMode && currentCourse != null) {
                        val newCartItem = CartItem(
                            id = mockCartItems.size + 1,
                            itemId = itemId,
                            itemType = ItemType.Course,
                            price = currentCourse.price,
                            coverImage = currentCourse.thumbnail,
                            title = currentCourse.title
                        )
                        mockCartItems.add(newCartItem)
                    }
                    // Gửi sự kiện thành công lên UI
                    _events.send(UiEvent.ShowSnackbar("Đã thêm vào giỏ hàng thành công!"))
                },
                onFailure = { error ->
                    // Gửi sự kiện thất bại lên UI
                    _events.send(UiEvent.ShowSnackbar("Lỗi: ${error.message}"))
                }
            )
        }
    }

    fun buyNow() {

    }
}
