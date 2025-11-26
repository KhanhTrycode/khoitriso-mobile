package com.example.khoitriso.ui.behavior

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.request.ForumVoteRequest
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.forum.ForumUsecase
import com.example.khoitriso.domain.usecase.forum.IsBookmarked
import com.example.khoitriso.test.MockData
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class ForumViewModel(
    private val authUsecase: AuthUsecase,
    private val forumUsecase: ForumUsecase,
    private val userManager: UserManager,
) : BaseViewModel() {

    protected fun loadCurrentUser(currentUser: MutableStateFlow<UiState<User>>) {
        viewModelScope.launch {
            loadData(
                stateFlow = currentUser,
                mockData = MockData.mockUser1,
                apiCall = {
                    authUsecase.loadCurrentUserInfo(userManager)
                }
            )
        }
    }

    fun loginAgain(): User {
        return MockData.mockUser1
    }


    protected fun voteInParent(
        targetType: Int,
        targetId: String,
        userId: Int,
        voteType: Int,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val result = if (_isTestMode) {
                // Logic cho test mode
                debug("Voting in TestMode", "ForumViewModel")
                Result.success(voteType)
            } else {
                val request = ForumVoteRequest(
                    targetId = targetId,
                    targetType = targetType,
                    userId = userId,
                    voteType = voteType
                )
                forumUsecase.vote(request)
            }

            result.fold(
                onSuccess = { total ->
                    onSuccess(total)
                    debug("Vote successful for target $targetType-$targetId", "ForumViewModel")
                },
                onFailure = { exception ->
                    debug("Vote failed for target $targetType-$targetId: ${exception.message}", "ForumViewModel")
                }
            )
        }
    }


    protected fun toggleBookmarkInParent(
        questionId: String,
        userId: Int,
        isCurrentlyBookmarked: Boolean,
        onSuccess: (Unit) -> Unit
    ){
        viewModelScope.launch {
            val result = if (_isTestMode) {
                // Logic cho test mode
                debug(if (isCurrentlyBookmarked) "removeBookmark" else "addBookmark", "TestMode")
                Result.success(Unit)
            } else {
                // Logic gọi API thật
                if (isCurrentlyBookmarked) {
                    forumUsecase.removeBookmark(questionId, userId)
                } else {
                    forumUsecase.addBookmark(questionId, userId)
                }
            }
            result.fold(
                onSuccess = { total ->
                    onSuccess(total)
                    debug("Bookmark successful for target", "ForumViewModel")
                },
                onFailure = { exception ->
                    debug("unBookmark failed for target: ${exception.message}",
                        "ForumViewModel")
                }
            )
        }
    }
}