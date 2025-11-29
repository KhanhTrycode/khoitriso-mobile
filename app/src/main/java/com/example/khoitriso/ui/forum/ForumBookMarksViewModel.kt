package com.example.khoitriso.ui.forum

import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.request.ForumBookmarkItem
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.forum.ForumUsecase
import com.example.khoitriso.ui.behavior.ForumViewModel
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ForumBookMarksViewModel @Inject constructor(
    authUsecase: AuthUsecase,
    forumUsecase: ForumUsecase,
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


    fun toggleBookmark(string: String, state: UiState<User>, onSuccess: () -> Unit, onError: () -> Unit){


    }
}