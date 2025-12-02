package com.example.khoitriso.ui.forum

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.ForumAnswer
import com.example.khoitriso.domain.models.ForumCategory
import com.example.khoitriso.domain.models.ForumComment
import com.example.khoitriso.domain.models.ForumQuestion
import com.example.khoitriso.domain.models.ForumStats
import com.example.khoitriso.domain.models.ForumTag
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.request.CreateAnswerRequest
import com.example.khoitriso.domain.request.CreateCommentRequest
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.forum.ForumUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.ForumViewModel
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumDetalQuestionViewModel @Inject constructor(
    private val forumUsecase: ForumUsecase,
    authUsecase: AuthUsecase,
    userManager: UserManager,
    private val savedStateHandle: SavedStateHandle,
) : ForumViewModel(
    authUsecase = authUsecase,
    forumUsecase = forumUsecase,
    userManager = userManager,
) {

    // Question detail
    private val _question = MutableStateFlow<UiState<ForumQuestion>>(UiState.Loading)
    val question: StateFlow<UiState<ForumQuestion>> = _question

    // Answers
    private val _answers = MutableStateFlow<UiState<List<ForumAnswer>>>(UiState.Loading)
    val answers: StateFlow<UiState<List<ForumAnswer>>> = _answers

    // Comments (key = parentId, value = comments)
    private val _comments = MutableStateFlow<Map<String, List<ForumComment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<ForumComment>>> = _comments

    // Categories
    private val _categories = MutableStateFlow<UiState<List<ForumCategory>>>(UiState.Loading)
    val categories: StateFlow<UiState<List<ForumCategory>>> = _categories

    // Tags
    private val _tags = MutableStateFlow<UiState<List<ForumTag>>>(UiState.Loading)
    val tags: StateFlow<UiState<List<ForumTag>>> = _tags

    private val _currentUser = MutableStateFlow<UiState<User>>(UiState.Loading)
    val currentUser: StateFlow<UiState<User>> = _currentUser

    // Stats
    private val _stats = MutableStateFlow<UiState<ForumStats>>(UiState.Loading)
    val stats: StateFlow<UiState<ForumStats>> = _stats

    private val _isVoted = MutableStateFlow<Int>(0)
    val isVoted: StateFlow<Int> = _isVoted

    // User votes (key = "targetType-targetId", value = voteType)
    private val _userVoteAnswers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val userVoteAnswers: StateFlow<Map<String, Int>> = _userVoteAnswers

    // Bookmarks
    private val _isBookmarked = MutableStateFlow<Boolean>(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked

    init {
        loadCurrentUser(
            _currentUser
        )
        getQuestionById()
    }

    fun getQuestionById() {
        loadQuestionById()

        when (val currentState = _question.value) {
            is UiState.Success<ForumQuestion> -> {
                loadUserVoteForQuestion(currentState.data.id, currentState.data.userId)
                loadBookmarksForQuestion(currentState.data.id, currentState.data.userId)
                loadComments(1, currentState.data.id)
                loadAnswers(currentState.data.id)
            }

            else -> {

            }

        }
    }

    private fun loadQuestionById() {
        val questionId = savedStateHandle.get<String>("questionId") ?: ""
        loadData(
            stateFlow = _question,
            mockData = MockData.mockForumQuestions[0],
            apiCall = {
                forumUsecase.getQuestionById(questionId)
            }
        )
    }

    private fun loadUserVoteForQuestion(questionId: String, userId: Int) {
        viewModelScope.launch {
            if (_isTestMode) {
                _isVoted.value = 1 // Mock: user has upvoted
            } else {
                forumUsecase.getUserVote(1, questionId, userId).onSuccess { voteType ->
                    _isVoted.value = voteType
                }
            }
        }
    }

    private fun loadBookmarksForQuestion(questionId: String, userId: Int) {
        loadDataWithNoResult(
            stateFlow = _isBookmarked,
            mockData = false,
            apiCall = {
                forumUsecase.isBookmarked(questionId, userId)
            }
        )
    }

    fun vote(targetType: Int, targetId: String, userId: Int, voteType: Int) {
        voteInParent(targetType, targetId, userId, voteType, onSuccess = {
            _isVoted.value = it
        })
    }

    fun toggleBookmark(questionId: String, userId: Int) {
        toggleBookmarkInParent(
            questionId = questionId,
            userId = userId,
            isCurrentlyBookmarked = _isBookmarked.value,
            onSuccess = {
                _isBookmarked.value = !_isBookmarked.value

            }
        )
    }

    fun createAnswer(
        questionId: String,
        request: CreateAnswerRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = if (_isTestMode) {
                Result.success({
                    MockData.mockForumAnswers.add(
                        ForumAnswer(
                            id = "99",
                            questionId = "99",
                            content = request.content,
                            userId = request.userId,
                            userName = request.userName,
                            createdAt = "2024-10-01T08:00:00Z",
                            updatedAt = "2024-10-01T08:00:00Z"
                        )
                    )
                })
            } else {
                forumUsecase.createAnswer(questionId, request)
            }
            result.fold(
                onSuccess = {
                    onSuccess()
                    loadAnswers(questionId) // Refresh answers
                    getQuestionById() // Refresh question
                },
                onFailure = { onError(it.message ?: "Failed to create answer") }
            )
        }
    }

    fun createComment(
        request: CreateCommentRequest, onSuccess: () -> Unit,
        onFailure: () ->
        Unit,
    ) {
        viewModelScope.launch {
            val result = if (_isTestMode) {
                Result.success({
                    MockData.mockForumComments.add(
                        ForumComment(
                            parentId = request.parentId,
                            parentType = request.parentType,
                            content = request.content,
                            userId = request.userId,
                            id = "99",
                            userName = request.userName,
                            createdAt = "2024-10-01T08:00:00Z",
                            updatedAt = "2024-10-01T08:00:00Z",
                            userAvatar = ""
                        )
                    )
                })
            } else {
                forumUsecase.createComment(request)
            }
            result.fold(
                onSuccess = {
                    loadComments(request.parentType, request.parentId) // Refresh comments
                    onSuccess()
                },
                onFailure = {
                    onFailure()
                }
            )
        }
    }

    fun loadAnswers(questionId: String) {
        loadData(
            stateFlow = _answers,
            mockData = MockData.mockForumAnswers,
            apiCall = {
                forumUsecase.getAnswers(questionId)
            },
            onSuccess = {
                it.data.forEach { answer ->
                    loadComments(2, answer.id)
                    // Load user vote for each answer
                    (_currentUser.value as? UiState.Success<User>)?.data?.id?.let { userId ->
                        loadUserVoteForAnswer(answer.id, userId)
                    }
                }
            }
        )
    }

    private fun loadUserVoteForAnswer(answerId: String, userId: Int) {
        viewModelScope.launch {
            val key = "2-$answerId"
            if (_isTestMode) {
                // Mock: Set some votes for testing
                if (answerId == "fa1") {
                    _userVoteAnswers.value = _userVoteAnswers.value.toMutableMap().apply {
                        put(key, 1) // First answer is upvoted
                    }
                }
            } else {
                forumUsecase.getUserVote(2, answerId, userId).onSuccess { voteType ->
                    _userVoteAnswers.value = _userVoteAnswers.value.toMutableMap().apply {
                        if (voteType != 0) {
                            put(key, voteType)
                        } else {
                            remove(key) // Remove if no vote
                        }
                    }
                }
            }
        }
    }

    fun loadComments(parentType: Int, parentId: String) {
        viewModelScope.launch {
            val result = if (_isTestMode) {
                Result.success(MockData.mockForumComments)
            } else {
                forumUsecase.getComments(parentType, parentId)
            }
            result.onSuccess { commentsList ->
                _comments.value = _comments.value.toMutableMap().apply {
                    put(parentId, commentsList)
                }
            }
        }
    }

    fun acceptAnswer(answerId: String) {
        viewModelScope.launch {
            val result = if (_isTestMode) {
                debug("Accepting answer in TestMode", "ForumDetailViewModel")
                Result.success(Unit)
            } else {
                forumUsecase.acceptAnswer(answerId)
            }

            result.fold(
                onSuccess = {
                    // Refresh question and answers
                    val currentState = _question.value
                    if (currentState is UiState.Success) {
                        getQuestionById()
                        loadAnswers(currentState.data.id)
                    }
                    debug(
                        "Accept answer successful for answerId: $answerId",
                        "ForumDetailViewModel"
                    )
                },
                onFailure = { exception ->
                    debug(
                        "Accept answer failed for answerId: $answerId: ${exception.message}",
                        "ForumDetailViewModel"
                    )
                }
            )
        }
    }

    fun unacceptAnswer(answerId: String) {
//        viewModelScope.launch {
//            val result = if (_isTestMode) {
//                debug("Unaccepting answer in TestMode", "ForumDetailViewModel")
//                Result.success(Unit)
//            } else {
//                forumUsecase.unacceptAnswer(answerId)
//            }
//
//            result.fold(
//                onSuccess = {
//                    // Refresh question and answers
//                    val currentState = _question.value
//                    if (currentState is UiState.Success) {
//                        getQuestionById()
//                        loadAnswers(currentState.data.id)
//                    }
//                    debug(
//                        "Unaccept answer successful for answerId: $answerId",
//                        "ForumDetailViewModel"
//                    )
//                },
//                onFailure = { exception ->
//                    debug(
//                        "Unaccept answer failed for answerId: $answerId: ${exception.message}",
//                        "ForumDetailViewModel"
//                    )
//                }
//            )
//        }
    }
}