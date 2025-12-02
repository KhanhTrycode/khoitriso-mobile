package com.example.khoitriso.ui.learning

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.models.LessonDiscussion
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.domain.usecase.discussion.LessonDiscussionUsecase
import com.example.khoitriso.domain.request.CreateLessonDiscussionRequest
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiEvent
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearningCourseViewModel @Inject constructor(
    private val courseUsecase: CourseUsecase,
    private val lessonDiscussionUsecase: LessonDiscussionUsecase,
    private val userManager: UserManager,
    private val savedStateHandle: SavedStateHandle,

) : BaseViewModel() {

    private val _courseDetail = MutableStateFlow<UiState<CourseDetail>>(UiState.Loading)
    val courseDetail: StateFlow<UiState<CourseDetail>> = _courseDetail

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState: StateFlow<ExoPlayer?> = _playerState

    private val _currentLesson = MutableStateFlow<UiState<Lesson>>(UiState.Loading)
    val currentLesson: StateFlow<UiState<Lesson>> = _currentLesson

    private val _discussions = MutableStateFlow<UiState<MyResponese<LessonDiscussion>>>(UiState.Loading)
    val discussions: StateFlow<UiState<MyResponese<LessonDiscussion>>> = _discussions

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

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

    fun ListAssignment(){

    }

    fun loadDiscussions(lessonId: Int, page: Int = 1) {
        viewModelScope.launch {
            _discussions.value = UiState.Loading
            
            if (_isTestMode) {
                // Mock data
                _discussions.value = UiState.Success(
                    MyResponese(
                        items = emptyList(),
                        page = 1,
                        pageSize = 20,
                        total = 0,
                        totalPages = 0
                    )
                )
            } else {
                lessonDiscussionUsecase.getLessonDiscussions(
                    lessonId = lessonId,
                    page = page,
                    pageSize = 20,
                    sortBy = "createdAt",
                    desc = true
                ).fold(
                    onSuccess = { result ->
                        _discussions.value = UiState.Success(result)
                    },
                    onFailure = { exception ->
                        _discussions.value = UiState.Error(exception.message ?: "Lỗi không xác định")
                    }
                )
            }
        }
    }

    fun createDiscussion(lessonId: Int, content: String, videoTimestamp: Int = 0) {
        viewModelScope.launch {
            lessonDiscussionUsecase.createLessonDiscussion(
                lessonId = lessonId,
                request = CreateLessonDiscussionRequest(
                    content = content,
                    videoTimestamp = videoTimestamp
                )
            ).fold(
                onSuccess = {
                    // Reload discussions after creating
                    loadDiscussions(lessonId)
                },
                onFailure = { exception ->
                    _events.send(UiEvent.ShowSnackbar(exception.message ?: "Không thể tạo câu hỏi"))
                }
            )
        }
    }

}