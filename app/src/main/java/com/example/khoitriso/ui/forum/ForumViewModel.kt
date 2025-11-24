package com.example.khoitriso.ui.forum

import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.repository.*
import com.example.khoitriso.domain.request.CreateAnswerRequest
import com.example.khoitriso.domain.request.CreateCommentRequest
import com.example.khoitriso.domain.request.CreateQuestionRequest
import com.example.khoitriso.domain.request.ForumBookmarksResult
import com.example.khoitriso.domain.request.ForumQuestions
import com.example.khoitriso.domain.request.ForumVoteRequest
import com.example.khoitriso.domain.usecase.forum.ForumUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val forumUsecase: ForumUsecase,
    private val tokenManager: TokenManager,
    private val userManager: UserManager,
    private val authRepository: AuthRepository
) : BaseViewModel() {
    
    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId.asStateFlow()
    
    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> = _currentUserName.asStateFlow()
    
    private val _currentUserAvatar = MutableStateFlow<String?>(null)
    val currentUserAvatar: StateFlow<String?> = _currentUserAvatar.asStateFlow()
    
    init {
        viewModelScope.launch {
            loadCurrentUserInfo()
        }
    }
    
    private suspend fun loadCurrentUserInfo() {
        // Load user ID from token
        val token = tokenManager.getAccessToken()
        token?.let {
            try {
                val jwt = JWT.decode(it)
                _currentUserId.value = jwt.getClaim("UserId").asInt()
            } catch (e: Exception) {
                _currentUserId.value = null
            }
        }
        
        // Try to get user info from UserManager first (local cache)
        val savedUser = userManager.getCurrentUser()
        if (savedUser != null) {
            // Đã có user info trong local, dùng luôn
            _currentUserId.value = savedUser.id
            _currentUserName.value = savedUser.fullName
            _currentUserAvatar.value = savedUser.avatar
        } else {
            // Chưa có user info trong local, gọi API /auth/me để lấy
            authRepository.getMe().fold(
                onSuccess = { user ->
                    user?.let {
                        _currentUserId.value = it.id
                        _currentUserName.value = it.fullName
                        _currentUserAvatar.value = it.avatar
                        // Lưu vào UserManager để dùng lần sau
                        userManager.saveUser(it)
                    }
                },
                onFailure = { 
                    // Nếu API fail, fallback về JWT token
                    val userNameFromToken = userManager.getUserNameFromToken(tokenManager)
                    _currentUserName.value = userNameFromToken ?: "User"
                }
            )
        }
    }

    // Questions list
    private val _questions = MutableStateFlow<UiState<MyResponese<ForumQuestion>>>(UiState.Loading)
    val questions: StateFlow<UiState<MyResponese<ForumQuestion>>> = _questions
    // Question detail
    private val _question = MutableStateFlow<UiState<ForumQuestion>>(UiState.Loading)
    val question: StateFlow<UiState<ForumQuestion>> = _question.asStateFlow()

    // Answers
    private val _answers = MutableStateFlow<UiState<List<ForumAnswer>>>(UiState.Loading)
    val answers: StateFlow<UiState<List<ForumAnswer>>> = _answers.asStateFlow()

    // Comments (key = parentId, value = comments)
    private val _comments = MutableStateFlow<Map<String, List<ForumComment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<ForumComment>>> = _comments.asStateFlow()

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
            
//            val result = forumUsecase.getQuestions(
//                search = _searchQuery.value.ifEmpty { null },
//                categoryId = _selectedCategory.value,
//                tag = _selectedTag.value,
//                isSolved = isSolvedFilterValue,
//                isPinned = _isPinnedFilter.value,
//                page = page,
//                pageSize = 20,
//                sortBy = apiSortBy,
//                desc = _desc.value
//            )
//            _questions.value = result.fold(
//                onSuccess = { 
//                    // Load user votes and bookmarks after questions loaded
//                    _currentUserId.value?.let { userId ->
//                        loadUserVotesForQuestions(it.items, userId)
//                        loadBookmarksForQuestions(it.items, userId)
//                    }
//                    UiState.Success(it)
//                },
//                onFailure = { UiState.Error(it.message ?: "Failed to load questions") }
//            )

            loadDataWithPage(
                stateFlow = _questions,
                mockData = MockData.mockForumQuestions,
                apiCall = {
                    forumUsecase.getQuestions()
                }
            )
        }
    }

    private suspend fun loadUserVotesForQuestions(questions: List<ForumQuestion>, userId: Int) {
        questions.forEach { question ->
            forumUsecase.getUserVote(1, question.id, userId).onSuccess { voteType ->
                val key = "1-${question.id}"
                _userVotes.value = _userVotes.value.toMutableMap().apply {
                    if (voteType != null && voteType != 0) {
                        put(key, voteType)
                    }
                }
            }
        }
    }

    private suspend fun loadBookmarksForQuestions(questions: List<ForumQuestion>, userId: Int) {
        questions.forEach { question ->
            forumUsecase.isBookmarked(question.id, userId).onSuccess { isBookmarked ->
                _bookmarks.value = _bookmarks.value.toMutableSet().apply {
                    if (isBookmarked) {
                        add(question.id)
                    }
                }
            }
        }
    }

    fun loadQuestionById(id: String) {
        _question.value = UiState.Loading
        viewModelScope.launch {
            val result = forumUsecase.getQuestionById(id)
            _question.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load question") }
            )
        }
    }

    fun loadAnswers(questionId: String) {
        _answers.value = UiState.Loading
        viewModelScope.launch {
            val result = forumUsecase.getAnswers(questionId)
            _answers.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load answers") }
            )
        }
    }

    fun loadComments(parentType: Int, parentId: String) {
        viewModelScope.launch {
            val result = forumUsecase.getComments(parentType, parentId)
            result.onSuccess { commentsList ->
                _comments.value = _comments.value.toMutableMap().apply {
                    put(parentId, commentsList)
                }
            }
        }
    }

    fun createQuestion(request: CreateQuestionRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = forumUsecase.createQuestion(request)
            result.fold(
                onSuccess = {
                    onSuccess()
                    loadQuestions() // Refresh list
                },
                onFailure = { onError(it.message ?: "Failed to create question") }
            )
        }
    }

    fun createAnswer(questionId: String, request: CreateAnswerRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = forumUsecase.createAnswer(questionId, request)
            result.fold(
                onSuccess = {
                    onSuccess()
                    loadAnswers(questionId) // Refresh answers
                    loadQuestionById(questionId) // Refresh question
                },
                onFailure = { onError(it.message ?: "Failed to create answer") }
            )
        }
    }

    fun createComment(request: CreateCommentRequest) {
        viewModelScope.launch {
            val result = forumUsecase.createComment(request)
            result.fold(
                onSuccess = {
                    loadComments(request.parentType, request.parentId) // Refresh comments
                },
                onFailure = {  }
            )
        }
    }

    fun vote(targetType: Int, targetId: String, userId: Int, voteType: Int) {
        viewModelScope.launch {
            val request = ForumVoteRequest(
                targetId = targetId,
                targetType = targetType,
                userId = userId,
                voteType = voteType
            )
            val result = forumUsecase.vote(request)
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
                    // Refresh question/answer
                    if (targetType == 1) { // Question
                        _question.value.let { state ->
                            if (state is UiState.Success) {
                                loadQuestionById(targetId)
                            }
                        }
                    } else if (targetType == 2) { // Answer
                        loadAnswers(_question.value.let { if (it is UiState.Success) it.data.id else "" })
                    }
                },
                onFailure = {

                }
            )
        }
    }

    fun loadUserVote(targetType: Int, targetId: String, userId: Int) {
        viewModelScope.launch {
            val result = forumUsecase.getUserVote(targetType, targetId, userId)
            result.onSuccess { voteType ->
                val key = "$targetType-$targetId"
                _userVotes.value = _userVotes.value.toMutableMap().apply {
                    if (voteType != null && voteType != 0) {
                        put(key, voteType)
                    } else {
                        remove(key)
                    }
                }
            }
        }
    }

    fun toggleBookmark(questionId: String, userId: Int) {
        viewModelScope.launch {
            val isBookmarked = _bookmarks.value.contains(questionId)
            val result = if (isBookmarked) {
                forumUsecase.removeBookmark(questionId, userId)
            } else {
                forumUsecase.addBookmark(questionId, userId)
            }
            result.fold(
                onSuccess = {
                    _bookmarks.value = _bookmarks.value.toMutableSet().apply {
                        if (isBookmarked) {
                            remove(questionId)
                        } else {
                            add(questionId)
                        }
                    }

                },
                onFailure = {  }
            )
        }
    }

    fun loadBookmarkStatus(questionId: String, userId: Int) {
        viewModelScope.launch {
            val result = forumUsecase.isBookmarked(questionId, userId)
            result.onSuccess { isBookmarked ->
                _bookmarks.value = _bookmarks.value.toMutableSet().apply {
                    if (isBookmarked) {
                        add(questionId)
                    } else {
                        remove(questionId)
                    }
                }
            }
        }
    }

    fun acceptAnswer(answerId: String) {
        viewModelScope.launch {
            val result = forumUsecase.acceptAnswer(answerId)
            result.fold(
                onSuccess = {
                    // Refresh question and answers
                    _question.value.let { state ->
                        if (state is UiState.Success) {
                            loadQuestionById(state.data.id)
                            loadAnswers(state.data.id)
                        }
                    }
                },
                onFailure = {  }
            )
        }
    }

    fun unacceptAnswer(answerId: String) {
        viewModelScope.launch {
            val result = forumUsecase.unacceptAnswer(answerId)
            result.fold(
                onSuccess = {
                    // Refresh question and answers
                    _question.value.let { state ->
                        if (state is UiState.Success) {
                            loadQuestionById(state.data.id)
                            loadAnswers(state.data.id)
                        }
                    }
                },
                onFailure = {

                }
            )
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            val result = forumUsecase.getCategories()
            _categories.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load categories") }
            )
        }
    }

    fun loadTags(limit: Int = 30) {
        viewModelScope.launch {
            val result = forumUsecase.getTags(limit)
            _tags.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load tags") }
            )
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            val result = forumUsecase.getStats()
            _stats.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load stats") }
            )
        }
    }

    suspend fun getBookmarks(userId: Int, page: Int = 1, pageSize: Int = 20): Result<ForumBookmarksResult> {
        return forumUsecase.getBookmarks(userId, page, pageSize)
    }

    fun loadInitialData() {
        loadQuestions()
        loadCategories()
        loadTags()
        loadStats()
        
        // Load bookmarks for all questions when user is available
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                // This will be called after questions are loaded
            }
        }
    }
}
