package com.example.khoitriso.ui.learning

import androidx.lifecycle.SavedStateHandle
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.Assignment
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
class AssignmentViewModel @Inject constructor(
    private val courseUsecase: CourseUsecase,
    private val userManager: UserManager,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _assignment = MutableStateFlow<UiState<Assignment>>(UiState.Loading)
    val assignment: StateFlow<UiState<Assignment>> = _assignment

    private val _isDone = MutableStateFlow<Boolean>(false)
    val isDone: StateFlow<Boolean> = _isDone
    private val _isStarted = MutableStateFlow<Boolean>(false)
    val isStarted: StateFlow<Boolean> = _isStarted

    private val _isAnswers = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val isAnswers: StateFlow<Map<Int, Boolean>> = _isAnswers

    init{
        getAssignment()
    }

    private fun getAssignment() {
        val assignmentId = savedStateHandle.get<Int>("assignmentId") ?: -1
        loadData(
            stateFlow = _assignment,
            mockData = MockData.mockAssignment1,
            apiCall = {
                courseUsecase.getAssignmentById(assignmentId)
            },
            onSuccess = {
                _isAnswers.value = it.data.questions.associate { question ->
                    question.id to false
                }
            }
        )
    }

    fun startAssignment(){
        _isStarted.value = true
    }

    fun timesUp(){
        _isDone.value = true
        _isStarted.value = false
    }

    fun onOptionSelected(questionId: Int,optionId:Int){
        _isAnswers.value = _isAnswers.value.toMutableMap().apply {
            this[questionId] = true
        }
    }
}