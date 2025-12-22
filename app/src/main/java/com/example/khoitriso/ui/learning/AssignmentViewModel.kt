package com.example.khoitriso.ui.learning

import androidx.lifecycle.SavedStateHandle
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.AssignmentSubmission
import com.example.khoitriso.domain.request.AssignmentSubmissionRequest
import com.example.khoitriso.domain.request.QuestionAnswerRequest
import com.example.khoitriso.domain.usecase.assignment.AssignmentUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val courseUsecase: CourseUsecase,
    private val assignmentUsecase: AssignmentUsecase,
    private val userManager: UserManager,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _assignment = MutableStateFlow<UiState<Assignment>>(UiState.Loading)
    val assignment: StateFlow<UiState<Assignment>> = _assignment

    private val _submissionResult = MutableStateFlow<UiState<AssignmentSubmission>?>(null)
    val submissionResult: StateFlow<UiState<AssignmentSubmission>?> = _submissionResult

    private val _isDone = MutableStateFlow<Boolean>(false)
    val isDone: StateFlow<Boolean> = _isDone
    private val _isStarted = MutableStateFlow<Boolean>(false)
    val isStarted: StateFlow<Boolean> = _isStarted
    
    private val _attemptCount = MutableStateFlow<Int>(0)
    val attemptCount: StateFlow<Int> = _attemptCount
    
    private val _showSubmitDialog = MutableStateFlow<Boolean>(false)
    val showSubmitDialog: StateFlow<Boolean> = _showSubmitDialog

    // Map questionId -> selectedOptionId or answerText
    private val _userAnswers = MutableStateFlow<Map<Int, Pair<Int?, String?>>>(emptyMap())
    val userAnswers: StateFlow<Map<Int, Pair<Int?, String?>>> = _userAnswers

    private val _isAnswers = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val isAnswers: StateFlow<Map<Int, Boolean>> = _isAnswers

    init{
        getAssignment()
    }

    private fun getAssignment() {
        val assignmentId = savedStateHandle.get<Int>("assignmentId")
        
        if (assignmentId != null) {
            // Chỉ load assignment info (không có questions)
            loadData(
                stateFlow = _assignment,
                apiCall = {
                    courseUsecase.getAssignmentById(assignmentId)
                },
                onSuccess = { assignmentResult ->
                    // Đảm bảo questions là empty list khi mới vào
                    val assignmentWithoutQuestions = assignmentResult.data.copy(questions = emptyList())
                    _assignment.value = UiState.Success(assignmentWithoutQuestions)
                    // Cập nhật attemptCount từ assignment
                    _attemptCount.value = assignmentResult.data.attemptCount
                }
            )
        } else {
            _assignment.value = UiState.Error("Assignment ID is required")
        }
    }

    private fun loadQuestions(assignmentId: Int, assignment: Assignment) {
        viewModelScope.launch {
            debug("Loading questions for assignment $assignmentId", "AssignmentViewModel")
            courseUsecase.getAssignmentQuestions(assignmentId).fold(
                onSuccess = { questions ->
                    debug("Questions loaded successfully: ${questions.size} questions", "AssignmentViewModel")
                    // Không convert MathML - để MathJax render trực tiếp
                    val updatedAssignment = assignment.copy(questions = questions)
                    _assignment.value = UiState.Success(updatedAssignment)
                    debug("Assignment updated with ${updatedAssignment.questions.size} questions", "AssignmentViewModel")
                    _isAnswers.value = questions.associate { question ->
                        question.id to false
                    }
                },
                onFailure = { exception ->
                    // Nếu không load được questions, vẫn hiển thị assignment nhưng không có questions
                    debug("Failed to load questions: ${exception.message}", "AssignmentViewModel")
                    _assignment.value = UiState.Success(assignment.copy(questions = emptyList()))
                }
            )
        }
    }

    fun startAssignment(){
        val assignmentId = savedStateHandle.get<Int>("assignmentId")
        val currentAssignment = (_assignment.value as? UiState.Success<Assignment>)?.data
        
        if (assignmentId != null && currentAssignment != null) {
            // Kiểm tra số lần đã nộp
            if (currentAssignment.maxAttempts > 0 && _attemptCount.value >= currentAssignment.maxAttempts) {
                // Đã vượt quá số lần cho phép
                return
            }
            
            // Bắt đầu assignment: load questions và set isStarted
            _isStarted.value = true
            loadQuestions(assignmentId, currentAssignment)
        }
    }
    
    fun canStartAssignment(): Boolean {
        val currentAssignment = (_assignment.value as? UiState.Success<Assignment>)?.data ?: return false
        return currentAssignment.maxAttempts <= 0 || _attemptCount.value < currentAssignment.maxAttempts
    }

    fun timesUp(){
        _isDone.value = true
        _isStarted.value = false
    }

    fun onOptionSelected(questionId: Int, optionId: Int) {
        _isAnswers.value = _isAnswers.value.toMutableMap().apply {
            this[questionId] = true
        }
        _userAnswers.value = _userAnswers.value.toMutableMap().apply {
            this[questionId] = Pair(optionId, null)
        }
    }

    fun onTextAnswerChanged(questionId: Int, answerText: String) {
        _isAnswers.value = _isAnswers.value.toMutableMap().apply {
            this[questionId] = answerText.isNotBlank()
        }
        _userAnswers.value = _userAnswers.value.toMutableMap().apply {
            this[questionId] = Pair(null, answerText)
        }
    }
    
    fun showSubmitDialog() {
        _showSubmitDialog.value = true
    }
    
    fun hideSubmitDialog() {
        _showSubmitDialog.value = false
    }

    fun submitAssignment() {
        val assignmentId = savedStateHandle.get<Int>("assignmentId") ?: return
        val currentAnswers = _userAnswers.value

        viewModelScope.launch {
            _submissionResult.value = UiState.Loading
            _showSubmitDialog.value = false
            
            val request = AssignmentSubmissionRequest(
                answers = currentAnswers.map { (questionId, answerPair) ->
                    QuestionAnswerRequest(
                        questionId = questionId,
                        optionId = answerPair.first,
                        answerText = answerPair.second
                    )
                }
            )

            assignmentUsecase.submitAssignment(assignmentId, request).fold(
                onSuccess = { submission ->
                    _submissionResult.value = UiState.Success(submission)
                    _isDone.value = true
                    _isStarted.value = false
                    // Reload assignment để cập nhật attemptCount
                    getAssignment()
                },
                onFailure = { exception ->
                    _submissionResult.value = UiState.Error(exception.message ?: "Failed to submit assignment")
                }
            )
        }
    }
}