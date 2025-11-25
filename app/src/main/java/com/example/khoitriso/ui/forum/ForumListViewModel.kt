package com.example.khoitriso.ui.forum

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.ForumCategory
import com.example.khoitriso.domain.models.ForumQuestion
import com.example.khoitriso.domain.models.ForumStats
import com.example.khoitriso.domain.models.ForumTag
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.repository.ForumRepository
import com.example.khoitriso.domain.request.ForumVoteRequest
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.forum.ForumUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumListViewModel @Inject constructor(
    private val forumUsecase: ForumUsecase,
    private val tokenManager: TokenManager,
    private val userManager: UserManager,
    private val authUsecase: AuthUsecase,
) : BaseViewModel() {
    // Questions list
    private val _questions = MutableStateFlow<UiState<MyResponese<ForumQuestion>>>(UiState.Loading)
    val questions: StateFlow<UiState<MyResponese<ForumQuestion>>> = _questions

    // Categories
    private val _categories = MutableStateFlow<UiState<List<ForumCategory>>>(UiState.Loading)
    val categories: StateFlow<UiState<List<ForumCategory>>> = _categories.asStateFlow()

    // Tags
    private val _tags = MutableStateFlow<UiState<List<ForumTag>>>(UiState.Loading)
    val tags: StateFlow<UiState<List<ForumTag>>> = _tags.asStateFlow()

    // Stats
    private val _stats = MutableStateFlow<UiState<ForumStats>>(UiState.Loading)
    val stats: StateFlow<UiState<ForumStats>> = _stats.asStateFlow()

    // User votes (key = "targetType-targetId", value = voteType)
    private val _userVotes = MutableStateFlow<Map<String, Int>>(emptyMap())
    val userVotes: StateFlow<Map<String, Int>> = _userVotes.asStateFlow()

    // Bookmarks
    private val _bookmarks = MutableStateFlow<Set<String>>(emptySet())
    val bookmarks: StateFlow<Set<String>> = _bookmarks.asStateFlow()

    // Filters
    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _isSolvedFilter = MutableStateFlow<Boolean?>(null)
    val isSolvedFilter: StateFlow<Boolean?> = _isSolvedFilter.asStateFlow()

    private val _isPinnedFilter = MutableStateFlow<Boolean?>(null)
    val isPinnedFilter: StateFlow<Boolean?> = _isPinnedFilter.asStateFlow()

    private val _sortBy = MutableStateFlow<String?>("activity")
    val sortBy: StateFlow<String?> = _sortBy.asStateFlow()

    private val _desc = MutableStateFlow<Boolean?>(true)
    val desc: StateFlow<Boolean?> = _desc.asStateFlow()

    private val _currentPage = MutableStateFlow<Int>(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()


    private val _currentUser = MutableStateFlow<UiState<User>>(UiState.Loading)
    val currentUser: StateFlow<UiState<User>> = _currentUser.asStateFlow()


    init {
        loadCurrentUser()
        loadQuestions(1)
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            loadData(
                stateFlow = _currentUser,
                mockData = MockData.mockUser1,
                apiCall = {
                    authUsecase.loadCurrentUserInfo(userManager)
                }
            )
        }
    }


    fun loadQuestions(page: Int = 1) {
        _currentPage.value = page
        _questions.value = UiState.Loading
        viewModelScope.launch {
            // Map sortBy to API format
            val apiSortBy = when (_sortBy.value) {
                "newest" -> "createdAt"
                "oldest" -> "createdAt"
                "votes" -> "voteCount"
                "activity" -> "lastActivityAt"
                "unanswered" -> "createdAt" // Sort by createdAt, filter by isSolved = false
                else -> _sortBy.value ?: "createdAt"
            }

            // When sorting by "unanswered", automatically filter isSolved = false
            val isSolvedFilterValue = if (_sortBy.value == "unanswered") {
                false
            } else {
                _isSolvedFilter.value
            }


            loadDataWithPage(
                stateFlow = _questions,
                mockData = MockData.mockForumQuestions,
                apiCall = {
                    forumUsecase.getQuestions(
                        search = _searchQuery.value.ifEmpty { null },
                        categoryId = _selectedCategory.value,
                        tag = _selectedTag.value,
                        isSolved = isSolvedFilterValue,
                        isPinned = _isPinnedFilter.value,
                        page = page,
                        pageSize = 20,
                        sortBy = apiSortBy,
                        desc = _desc.value
                    )
                }
            )
            viewModelScope.launch {
                _questions.collectLatest { questionsState ->
                    if (questionsState is UiState.Success<MyResponese<ForumQuestion>>) {
                        if (_currentUser.value is UiState.Success<User>) {
                            loadUserVotesForQuestions(
                                questionsState.data.items,
                                (_currentUser.value as UiState.Success<User>).data.id
                            )
                            loadBookmarksForQuestions(
                                questionsState.data.items,
                                (_currentUser.value as UiState.Success<User>).data.id
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadUserVotesForQuestions(questions: List<ForumQuestion>, userId: Int) {
        questions.forEach { question ->
//            forumUsecase.getUserVote(1, question.id, userId).onSuccess { voteType ->
//                val key = "1-${question.id}"
//                _userVotes.value = _userVotes.value.toMutableMap().apply {
//                    if (voteType != null && voteType != 0) {
//                        put(key, voteType)
//                    }
//                }
//            }
            val key = "1-${question.id}"
            _userVotes.value = _userVotes.value.toMutableMap().apply {
                put(key, 1)
            }
        }
    }

    private suspend fun loadBookmarksForQuestions(questions: List<ForumQuestion>, userId: Int) {
        questions.forEach { question ->
//            forumUsecase.isBookmarked(question.id, userId).onSuccess { isBookmarked ->
//                _bookmarks.value = _bookmarks.value.toMutableSet().apply {
//                    if (isBookmarked) {
//                        add(question.id)
//                    }
//                }
//            }
            _bookmarks.value = _bookmarks.value.toMutableSet().apply {
                add(question.id)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadQuestions()
    }

    fun setSelectedCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
        _currentPage.value = 1
        loadQuestions()
    }

    fun setSelectedTag(tag: String?) {
        _selectedTag.value = tag
        _currentPage.value = 1
        loadQuestions()
    }

    fun setIsSolvedFilter(isSolved: Boolean?) {
        _isSolvedFilter.value = isSolved
        _currentPage.value = 1
        loadQuestions()
    }

    fun setIsPinnedFilter(isPinned: Boolean?) {
        _isPinnedFilter.value = isPinned
        _currentPage.value = 1
        loadQuestions()
    }

    fun setSortBy(sortBy: String?, desc: Boolean? = null) {
        _sortBy.value = sortBy
        if (desc != null) {
            _desc.value = desc
        } else {
            // Auto set desc based on sortBy
            _desc.value = when (sortBy) {
                "oldest" -> false
                "unanswered" -> true // unanswered questions first
                else -> true // newest, votes, activity default to desc
            }
        }
        _currentPage.value = 1
        loadQuestions()
    }

    fun vote(targetType: Int, targetId: String, userId: Int, voteType: Int) {
        viewModelScope.launch {
            val request = ForumVoteRequest(
                targetId = targetId,
                targetType = targetType,
                userId = userId,
                voteType = voteType
            )
//            val result = forumUsecase.vote(request)
            debug("vote in ForumListViewModel", "TestMode")
            val result = Result.success(1)
            result.fold(
                onSuccess = { total ->
                    // Update user vote state
                    val key = "$targetType-$targetId"
                    _userVotes.value = _userVotes.value.toMutableMap().apply {
                        // Toggle vote: if same voteType, remove (set to 0), otherwise set to voteType
                        val currentVote = get(key) ?: 0
                        if (currentVote == voteType) {
                            remove(key) // Toggle off
                        } else {
                            put(key, voteType)
                        }
                    }
                },
                onFailure = {

                }
            )

        }
    }

    fun toggleBookmark(questionId: String, userId: Int) {
        viewModelScope.launch {
            val isBookmarked = _bookmarks.value.contains(questionId)
            val result = if (isBookmarked) {
//                forumUsecase.removeBookmark(questionId, userId)
                Result.success(Unit)
                debug("removeBookmark in ForumListViewModel", "TestMode")
            } else {
//                forumUsecase.addBookmark(questionId, userId)
                Result.success(Unit)
                debug("addBookmark in ForumListViewModel", "TestMode")
            }
            _bookmarks.value = _bookmarks.value.toMutableSet().apply {
                if (isBookmarked) {
                    remove(questionId)
                } else {
                    add(questionId)
                }
            }
//            result.fold(
//                onSuccess = {
//                    _bookmarks.value = _bookmarks.value.toMutableSet().apply {
//                        if (isBookmarked) {
//                            remove(questionId)
//                        } else {
//                            add(questionId)
//                        }
//                    }
//
//                },
//                onFailure = {  }
//            )
        }
    }
}

