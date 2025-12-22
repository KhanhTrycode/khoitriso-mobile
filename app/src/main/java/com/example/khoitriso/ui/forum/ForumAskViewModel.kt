package com.example.khoitriso.ui.forum

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.ForumCategory
import com.example.khoitriso.domain.models.ForumQuestion
import com.example.khoitriso.domain.models.ForumTag
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.request.CreateQuestionRequest
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.forum.ForumUsecase
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.ui.behavior.ForumViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumAskViewModel @Inject constructor(
    private val forumUsecase: ForumUsecase,
    private val userManager: UserManager,
    private val authUsecase: AuthUsecase,
) : ForumViewModel(
    authUsecase = authUsecase,
    forumUsecase = forumUsecase,
    userManager = userManager,
) {
    
    private val _categories = MutableStateFlow<UiState<List<ForumCategory>>>(UiState.Loading)
    val categories: StateFlow<UiState<List<ForumCategory>>> = _categories.asStateFlow()
    
    private val _tags = MutableStateFlow<UiState<List<ForumTag>>>(UiState.Loading)
    val tags: StateFlow<UiState<List<ForumTag>>> = _tags.asStateFlow()
    
    private val _currentUser = MutableStateFlow<UiState<User>>(UiState.Loading)
    val currentUser: StateFlow<UiState<User>> = _currentUser.asStateFlow()
    
    init {
        loadCurrentUser(_currentUser)
        loadCategories()
        loadTags(30)
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            loadData(
                stateFlow = _categories,
                apiCall = {
                    forumUsecase.getCategories()
                }
            )
        }
    }
    
    fun loadTags(limit: Int = 30) {
        viewModelScope.launch {
            loadData(
                stateFlow = _tags,
                apiCall = {
                    forumUsecase.getTags(limit = limit)
                }
            )
        }
    }
    
    fun createQuestion(
        request: CreateQuestionRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = forumUsecase.createQuestion(request)
            
            result.fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = { exception ->
                    onError(exception.message ?: "Failed to create question")
                }
            )
        }
    }
}

