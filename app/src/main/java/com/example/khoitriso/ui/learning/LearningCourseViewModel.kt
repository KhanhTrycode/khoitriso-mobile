package com.example.khoitriso.ui.learning

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.AssignmentPreview
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.models.LessonDiscussion
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.domain.usecase.discussion.LessonDiscussionUsecase
import com.example.khoitriso.domain.usecase.lesson.LessonUsecase
import com.example.khoitriso.domain.request.CreateLessonDiscussionRequest
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiEvent
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearningCourseViewModel @Inject constructor(
    private val courseUsecase: CourseUsecase,
    private val lessonUsecase: LessonUsecase,
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

    private val _assignments = MutableStateFlow<UiState<List<com.example.khoitriso.domain.models.AssignmentPreview>>>(UiState.Loading)
    val assignments: StateFlow<UiState<List<com.example.khoitriso.domain.models.AssignmentPreview>>> = _assignments

    private val _fullAssignments = MutableStateFlow<UiState<List<com.example.khoitriso.domain.models.Assignment>>>(UiState.Loading)
    val fullAssignments: StateFlow<UiState<List<com.example.khoitriso.domain.models.Assignment>>> = _fullAssignments

    private val _discussions = MutableStateFlow<UiState<MyResponese<LessonDiscussion>>>(UiState.Loading)
    val discussions: StateFlow<UiState<MyResponese<LessonDiscussion>>> = _discussions
    
    private val _lessonCompleteState = MutableStateFlow<UiState<Unit>?>(null)
    val lessonCompleteState: StateFlow<UiState<Unit>?> = _lessonCompleteState

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
            apiCall = {
                courseUsecase.getCourseById(courseId)
            },
            onSuccess = {
                debug("getCourseDetail: ${it.data.lessons[0]}","LearningCourseViewModel")
                val firstLesson = it.data.lessons[0]
                _currentLesson.value = UiState.Success(firstLesson)
                // Load assignments cho lesson đầu tiên
                loadAssignments(firstLesson.id)
            })
    }

    fun onScreenAppeared(context: Context) {
        initializePlayerInParent(_playerState, context)
    }

    fun onLessonClicked(lesson: Lesson, context: Context) {
        // Set lesson hiện tại trước để UI có thể hiển thị ngay
        _currentLesson.value = UiState.Success(lesson)
        
        if (_playerState.value == null) {
            initializePlayerInParent(_playerState, context)
        }
        changeVideoSource(_playerState, lesson.videoUrl)
        debug("onLessonClicked: ${lesson.videoUrl}","LearningCourseViewModel")
        
        // Load full lesson data từ API để có materials và assignments mới nhất
        loadLessonDetails(lesson.id)
    }
    
    private fun loadLessonDetails(lessonId: Int) {
        viewModelScope.launch {
            lessonUsecase.getLessonById(lessonId).fold(
                onSuccess = { fullLesson ->
                    debug("Lesson details loaded: ${fullLesson.title}, materials: ${fullLesson.materials.size}, assignments: ${fullLesson.assignments.size}", "LearningCourseVM")
                    // Update current lesson với full data (bao gồm materials)
                    _currentLesson.value = UiState.Success(fullLesson)
                    // Update assignments
                    _assignments.value = UiState.Success(fullLesson.assignments)
                },
                onFailure = { exception ->
                    debug("Failed to load lesson details: ${exception.message}", "LearningCourseVM")
                    // Giữ nguyên lesson cũ, chỉ load assignments
                    loadAssignments(lessonId)
                }
            )
        }
    }

    override fun onCleared() {
        releasePlayerInParent(_playerState)
        super.onCleared()
    }

    fun releasePlayer(){
        releasePlayerInParent(_playerState)

    }

    fun loadAssignments(lessonId: Int) {
        viewModelScope.launch {
            _assignments.value = UiState.Loading
            
            debug("loadAssignments: lessonId = $lessonId", "LearningCourseVM")
            
            // Chỉ lấy lesson để có danh sách assignments (chỉ có Id, Title, Description)
            // Khi click vào assignment sẽ gọi getAssignmentById để lấy đầy đủ thông tin
            courseUsecase.getLessonById(lessonId).fold(
                onSuccess = { lesson ->
                    debug("Lesson loaded: ${lesson.title}, assignments: ${lesson.assignments.size}", "LearningCourseVM")
                    _assignments.value = UiState.Success(lesson.assignments)
                },
                onFailure = { exception ->
                    debug("Failed to load lesson: ${exception.message}", "LearningCourseVM")
                    _assignments.value = UiState.Error(exception.message ?: "Không thể tải danh sách bài tập")
                }
            )
        }
    }

    fun loadFullAssignments(assignmentPreviews: List<AssignmentPreview>) {
        viewModelScope.launch {
            _fullAssignments.value = UiState.Loading
            
            debug("loadFullAssignments: previews size = ${assignmentPreviews.size}", "LearningCourseVM")
            
            if (assignmentPreviews.isEmpty()) {
                _fullAssignments.value = UiState.Success(emptyList())
                return@launch
            }
            
            // Load tất cả assignments song song bằng cách gọi getAssignmentById cho mỗi assignment
            val assignmentResults = assignmentPreviews.map { preview ->
                async {
                    debug("Loading assignment ID: ${preview.id}", "LearningCourseVM")
                    courseUsecase.getAssignmentById(preview.id)
                }
            }.map { it.await() }
            
            val loadedAssignments = mutableListOf<com.example.khoitriso.domain.models.Assignment>()
            var hasError = false
            var errorMessage = ""
            
            assignmentResults.forEach { result ->
                result.fold(
                    onSuccess = { assignment ->
                        debug("Loaded assignment: ${assignment.title}", "LearningCourseVM")
                        loadedAssignments.add(assignment)
                    },
                    onFailure = { exception ->
                        hasError = true
                        errorMessage = exception.message ?: "Không thể tải một số bài tập"
                        debug("Failed to load assignment: $errorMessage", "LearningCourseVM")
                    }
                )
            }
            
            debug("Total loaded: ${loadedAssignments.size}, hasError: $hasError", "LearningCourseVM")
            
            if (hasError && loadedAssignments.isEmpty()) {
                _fullAssignments.value = UiState.Error(errorMessage)
            } else {
                _fullAssignments.value = UiState.Success(loadedAssignments)
            }
        }
    }

    fun loadDiscussions(lessonId: Int, page: Int = 1) {
        viewModelScope.launch {
            _discussions.value = UiState.Loading
            
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

    fun markLessonComplete(lessonId: Int) {
        viewModelScope.launch {
            _lessonCompleteState.value = UiState.Loading
            courseUsecase.markLessonComplete(lessonId).fold(
                onSuccess = {
                    _lessonCompleteState.value = UiState.Success(Unit)
                    _events.send(UiEvent.ShowSnackbar("Đã đánh dấu hoàn thành bài học"))
                    // Reload course detail to update lesson status and progress
                    getCourseDetail()
                },
                onFailure = { exception ->
                    _lessonCompleteState.value = UiState.Error(exception.message ?: "Không thể hoàn thành bài học")
                    _events.send(UiEvent.ShowSnackbar(exception.message ?: "Không thể hoàn thành bài học"))
                }
            )
        }
    }

}