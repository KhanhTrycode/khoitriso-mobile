package com.example.khoitriso.ui.learning

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.media3.exoplayer.ExoPlayer
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LearningCourseViewModel @Inject constructor(
    private val courseUsecase: CourseUsecase,
    private val userManager: UserManager,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val _courseDetail = MutableStateFlow<UiState<CourseDetail>>(UiState.Loading)
    val courseDetail: StateFlow<UiState<CourseDetail>> = _courseDetail

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState: StateFlow<ExoPlayer?> = _playerState

    private val _currentLesson = MutableStateFlow<UiState<Lesson>>(UiState.Loading)
    val currentLesson: StateFlow<UiState<Lesson>> = _currentLesson

    init {
        getCourseDetail()
    }

    fun getCourseDetail() {

        val courseId = savedStateHandle.get<Int>("courseId") ?: -1
        debug("getCourseDetail:$courseId","LearningCourseViewModel")

        loadData(
            _courseDetail,
            MockData.mockCourseDetail1,
            apiCall = {
                courseUsecase.getCourseById(courseId)
            },
            onSuccess = {
                debug("getCourseDetail: ${it.data.lessons[0]}","LearningCourseViewModel")
                _currentLesson.value = UiState.Success(it.data.lessons[0])
            })
    }

    fun onScreenAppeared(context: Context) {
        initializePlayerInParent(_playerState, context)
    }

    fun onLessonClicked(lesson: Lesson, context: Context) {
        _currentLesson.value = UiState.Success(lesson)
        if (_playerState.value == null) {
            initializePlayerInParent(_playerState, context)
        }
        changeVideoSource(_playerState, lesson.videoUrl)
        debug("onLessonClicked: ${lesson.videoUrl}","LearningCourseViewModel")
    }

    override fun onCleared() {
        releasePlayerInParent(_playerState)
        super.onCleared()
    }

    fun releasePlayer(){
        releasePlayerInParent(_playerState)

    }

}