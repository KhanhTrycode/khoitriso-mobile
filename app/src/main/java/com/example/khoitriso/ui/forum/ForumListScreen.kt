package com.example.khoitriso.ui.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.ui.behavior.DetailHeader
import androidx.compose.ui.text.style.TextDecoration
import com.example.khoitriso.ui.behavior.FormatTimeAgo
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumListScreen(
    navController: NavController,
    viewModel: ForumViewModel = hiltViewModel(),
) {
    val questionsState by viewModel.questions.collectAsState()
    val categoriesState by viewModel.categories.collectAsState()
    val tagsState by viewModel.tags.collectAsState()
    val statsState by viewModel.stats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val isSolvedFilter by viewModel.isSolvedFilter.collectAsState()
    val isPinnedFilter by viewModel.isPinnedFilter.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val userVotes by viewModel.userVotes.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diễn đàn học tập") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("forum/bookmarks") }) {
                        Icon(Icons.Default.Warning, "Bookmarks")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("forum/ask") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Đặt câu hỏi")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Khối tìm kiếm và bộ lọc
            FilterSection(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onSearch = { viewModel.setSearchQuery(searchText) },
                searchQuery = searchQuery,
                categoriesState = categoriesState,
                tagsState = tagsState,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setSelectedCategory(it) },
                selectedTag = selectedTag,
                onTagSelected = { viewModel.setSelectedTag(it) },
                sortBy = sortBy,
                onSortByChanged = { viewModel.setSortBy(it) },
                isPinnedFilter = isPinnedFilter,
                onPinnedFilterChanged = { viewModel.setIsPinnedFilter(it) },
                isSolvedFilter = isSolvedFilter,
                onSolvedFilterChanged = { viewModel.setIsSolvedFilter(it) }
            )

            // Danh sách câu hỏi
            when (val state = questionsState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    val result = state.data
                    if (result.items.isEmpty()) {
                        EmptyQuestionsView()
                    } else {
                        QuestionsList(
                            questions = result.items,
                            userVotes = userVotes,
                            bookmarks = bookmarks,
                            currentUserId = currentUserId,
                            selectedTag = selectedTag,
                            onQuestionClick = { id -> navController.navigate(com.example.khoitriso.utils.NavRoute.NavForumDetail(id)) },
                            onVote = { questionId, voteType ->
                                currentUserId?.let { userId ->
                                    viewModel.vote(1, questionId, userId, voteType)
                                }
                            },
                            onBookmarkClick = { questionId ->
                                currentUserId?.let { userId ->
                                    viewModel.toggleBookmark(questionId, userId)
                                }
                            },
                            onTagClick = { tag ->
                                viewModel.setSelectedTag(if (selectedTag == tag) null else tag)
                            }
                        )
                    }

                    // Phân trang
                    if (result.totalPages > 1) {
                        PaginationControls(
                            currentPage = result.page,
                            totalPages = result.totalPages,
                            onPageChange = { newPage -> viewModel.loadQuestions(newPage) }
                        )
                    }
                }
                is UiState.Error -> {
                   Text("Lỗi: ${state.message}")
                }
            }
        }
    }
}

// --- TÁCH COMPOSABLE CON RA ĐÂY ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    searchQuery: String,
    categoriesState: UiState<List<ForumCategory>>,
    tagsState: UiState<List<ForumTag>>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    selectedTag: String?,
    onTagSelected: (String?) -> Unit,
    sortBy: String?,
    onSortByChanged: (String) -> Unit,
    isPinnedFilter: Boolean?,
    onPinnedFilterChanged: (Boolean?) -> Unit,
    isSolvedFilter: Boolean?,
    onSolvedFilterChanged: (Boolean?) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tìm kiếm câu hỏi...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            onSearchTextChange("")
                            onSearch() // Immediately search for empty string
                        }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Hàng Sắp xếp và Ghim
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SortDropdown(sortBy = sortBy, onSortByChanged = onSortByChanged)
                FilterChip(
                    selected = isPinnedFilter == true,
                    onClick = { onPinnedFilterChanged(if (isPinnedFilter == true) null else true) },
                    label = { Text("Ghim") }
                )
            }

            // Hàng các bộ lọc khác
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategorySelected(null) },
                        label = { Text("Tất cả") }
                    )
                }
                if (categoriesState is UiState.Success) {
                    items(categoriesState.data) { category ->
                        FilterChip(
                            selected = selectedCategory == category.id,
                            onClick = { onCategorySelected(category.id) },
                            label = { Text(category.name) }
                        )
                    }
                }
                item { Spacer(Modifier.width(8.dp)) } // Separator
                item {
                    FilterChip(
                        selected = isSolvedFilter == null,
                        onClick = { onSolvedFilterChanged(null) },
                        label = { Text("Tất cả") }
                    )
                }
                item {
                    FilterChip(
                        selected = isSolvedFilter == true,
                        onClick = { onSolvedFilterChanged(true) },
                        label = { Text("Đã giải quyết") }
                    )
                }
                item {
                    FilterChip(
                        selected = isSolvedFilter == false,
                        onClick = { onSolvedFilterChanged(false) },
                        label = { Text("Chưa giải quyết") }
                    )
                }
                if (tagsState is UiState.Success) {
                    items(tagsState.data.take(10)) { tag ->
                        FilterChip(
                            selected = selectedTag == tag.name,
                            onClick = { onTagSelected(if (selectedTag == tag.name) null else tag.name) },
                            label = { Text(tag.name) }
                        )
                    }
                }
            }

            Button(
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth(),
                enabled = searchText != searchQuery
            ) {
                Icon(Icons.Default.Warning, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Lọc")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortDropdown(sortBy: String?, onSortByChanged: (String) -> Unit) {
    var showSortMenu by remember { mutableStateOf(false) }
    val sortOptions = mapOf(
        "activity" to "Hoạt động",
        "newest" to "Mới nhất",
        "oldest" to "Cũ nhất",
        "votes" to "Nhiều vote",
        "unanswered" to "Chưa trả lời"
    )

    Box {
        FilterChip(
            selected = sortBy != null,
            onClick = { showSortMenu = true },
            label = { Text(sortOptions[sortBy] ?: "Sắp xếp") }
        )
        DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
        ) {
            sortOptions.forEach { (key, value) ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        onSortByChanged(key)
                        showSortMenu = false
                    }
                )
            }
        }
    }
}

@Composable
private fun QuestionsList(
    questions: List<ForumQuestion>,
    userVotes: Map<String, Int>,
    bookmarks: Set<String>,
    currentUserId: Int?,
    selectedTag: String?,
    onQuestionClick: (String) -> Unit,
    onVote: (String, Int) -> Unit,
    onBookmarkClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(questions, key = { it.id }) { question ->
            QuestionCard(
                question = question,
                userVote = userVotes["1-${question.id}"],
                isBookmarked = bookmarks.contains(question.id),
                currentUserId = currentUserId ?: 0,
                onClick = { onQuestionClick(question.id) },
                onVote = { voteType -> onVote(question.id, voteType) },
                onBookmarkClick = { onBookmarkClick(question.id) },
                onTagClick = onTagClick
            )
        }
    }
}


@Composable
private fun QuestionCard(
    question: ForumQuestion,
    userVote: Int?,
    isBookmarked: Boolean,
    currentUserId: Int,
    onClick: () -> Unit,
    onVote: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
    onTagClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (question.isPinned) 2.dp else 0.dp,
                color = if (question.isPinned) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (question.isPinned)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuestionStatsColumn(
                voteCount = question.voteCount,
                answerCount = question.answerCount,
                viewCount = question.viewCount,
                userVote = userVote,
                isBookmarked = isBookmarked,
                showActions = currentUserId > 0,
                onVote = onVote,
                onBookmarkClick = onBookmarkClick
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuestionHeader(
                    title = question.title,
                    isPinned = question.isPinned,
                    isSolved = question.isSolved
                )
                QuestionTagsRow(tags = question.tags, onTagClick = onTagClick)
                QuestionMetaInfo(
                    userName = question.userName,
                    categoryName = question.categoryName,
                    lastActivity = question.lastActivityAt ?: question.updatedAt ?: question.createdAt
                )
            }
        }
    }
}

@Composable
private fun QuestionStatsColumn(
    voteCount: Int,
    answerCount: Int,
    viewCount: Int,
    userVote: Int?,
    isBookmarked: Boolean,
    showActions: Boolean,
    onVote: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
) {
    Column(
        modifier = Modifier.width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showActions) {
            IconButton(onClick = { onVote(1) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Warning, null, tint = if (userVote == 1) MaterialTheme.colorScheme
                    .primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }

        Text(text = "$voteCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = when {
            voteCount > 0 -> Color(0xFF10B981)
            voteCount < 0 -> Color(0xFFEF4444)
            else -> MaterialTheme.colorScheme.onSurface
        })
        Text("votes", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (showActions) {
            IconButton(onClick = { onVote(-1) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Warning, null, tint = if (userVote == -1) Color(0xFFEF4444) else
                    MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(text = "$answerCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (answerCount > 0) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant)
        Text("answers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text(text = "$viewCount", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("views", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (showActions) {
            Spacer(Modifier.height(8.dp))
            IconButton(onClick = onBookmarkClick, modifier = Modifier.size(32.dp)) {
                Icon(if (isBookmarked) Icons.Default.Warning else Icons.Default.Warning, null,
                    tint = if (isBookmarked) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun QuestionHeader(title: String, isPinned: Boolean, isSolved: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPinned) {
            Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("Ghim", fontSize = 10.sp) }
        }
        if (isSolved) {
            Badge(containerColor = Color(0xFF10B981)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Đã giải quyết", fontSize = 10.sp)
                }
            }
        }
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun QuestionTagsRow(tags: List<String>, onTagClick: (String) -> Unit) {
    if (tags.isNotEmpty()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(tags.take(5)) { tag ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable { onTagClick(tag) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Warning, null, Modifier.size(12.dp))
                        Text(tag, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionMetaInfo(userName: String, categoryName: String?, lastActivity: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetaInfoItem(icon = Icons.Default.Person, text = userName)
            categoryName?.let { category ->
                Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Text(category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 11.sp)
                }
            }
            MetaInfoItem(icon = Icons.Default.Warning, text = FormatTimeAgo(lastActivity))
        }
    }
}

@Composable
private fun MetaInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}




@Composable
private fun EmptyQuestionsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Warning, null,
                Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text("Không tìm thấy câu hỏi nào", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Hãy thử thay đổi bộ lọc hoặc từ khóa tìm kiếm của bạn.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

//@Composable
//private fun ErrorView(message: String, onRetry: () -> Unit) {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Icon(Icons.Default.Warning, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme
//                .error)
//            Text(text = message, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
//            Button(onClick = onRetry) { Text("Thử lại") }
//        }
//    }
//}


