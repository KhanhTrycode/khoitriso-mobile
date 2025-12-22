package com.example.khoitriso.ui.forum

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.request.ForumBookmarkItem
import com.example.khoitriso.domain.request.ForumBookmarksResult
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.forum.ForumUsecase
import com.example.khoitriso.ui.behavior.ForumViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumBookMarksViewModel @Inject constructor(
    authUsecase: AuthUsecase,
    private val forumUsecase: ForumUsecase,
    userManager: UserManager
): ForumViewModel(
    authUsecase,
    forumUsecase,
    userManager
) {

    private val _currentUser = MutableStateFlow<UiState<User>>(UiState.Loading)
    val currentUser: StateFlow<UiState<User>> = _currentUser

    private val _bookmarks = MutableStateFlow<UiState<List<ForumBookmarkItem>>>(UiState.Loading)
    val bookmarks: StateFlow<UiState<List<ForumBookmarkItem>>> = _bookmarks

    init {
        loadCurrentUser(_currentUser)
        viewModelScope.launch {
            _currentUser.collect { userState ->
                if (userState is UiState.Success) {
                    loadBookmarks(userState.data.id, 1, 20)
                }
            }
        }
    }

    fun loadBookmarks(userId: Int, page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            _bookmarks.value = UiState.Loading
            val result = forumUsecase.getBookmarks(userId, page, pageSize)
            
            result.fold(
                onSuccess = { bookmarksResult ->
                    _bookmarks.value = UiState.Success(bookmarksResult.items)
                },
                onFailure = { exception ->
                    _bookmarks.value = UiState.Error(exception.message ?: "Failed to load bookmarks")
                }
            )
        }
    }

    fun toggleBookmark(questionId: String, userId: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        val currentBookmarks = _bookmarks.value
        val isBookmarked = if (currentBookmarks is UiState.Success) {
            currentBookmarks.data.any { it.questionId == questionId }
        } else {
            false
        }
        
        toggleBookmarkInParent(
            questionId = questionId,
            userId = userId,
            isCurrentlyBookmarked = isBookmarked,
            onSuccess = {
                // Reload bookmarks after toggle
                loadBookmarks(userId, 1, 20)
                onSuccess()
            }
        )
    }
}