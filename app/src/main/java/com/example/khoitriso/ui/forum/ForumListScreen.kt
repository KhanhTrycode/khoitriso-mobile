package com.example.khoitriso.ui.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.ui.behavior.FormatTimeAgo
import com.example.khoitriso.ui.behavior.PaginationControls
import com.example.khoitriso.ui.behavior.SafeImage
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import java.util.*
import com.example.khoitriso.R
import com.example.khoitriso.utils.icon.ArrowBigDown
import com.example.khoitriso.utils.icon.ArrowBigUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumListScreen(
    navController: NavController,
    viewModel: ForumListViewModel = hiltViewModel(),
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
    val currentUser by viewModel.currentUser.collectAsState()

    var searchText by remember { mutableStateOf("") }


    Scaffold(topBar = {
        TopAppBar(title = { Text("Diễn đàn học tập") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        }, actions = {
            IconButton(onClick = { navController.navigate("forum/bookmarks") }) {
                Icon(Icons.Default.Bookmarks, "Bookmarks")
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { navController.navigate("forum/ask") },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, "Đặt câu hỏi")
        }
    }) { paddingValues ->
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
                onSolvedFilterChanged = { viewModel.setIsSolvedFilter(it) })

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
                            currentUserId = (currentUser as UiState.Success<User>).data.id,
                            selectedTag = selectedTag,
                            onQuestionClick = { id ->
                                navController.navigate(
                                    NavRoute.NavForumDetail(
                                        id
                                    )
                                )
                            },
                            onVote = { questionId, voteType ->
                                (currentUser as UiState.Success<User>).data.id.let { userId ->
                                    viewModel.vote(1, questionId, userId, voteType)
                                }
                            },
                            onBookmarkClick = { questionId ->
                                (currentUser as UiState.Success<User>).data.id.let { userId ->
                                    viewModel.toggleBookmark(questionId, userId)
                                }
                            },
                            onTagClick = { tag ->
                                viewModel.setSelectedTag(if (selectedTag == tag) null else tag)
                            })
                    }

                    // Phân trang
                    if (result.totalPages > 1) {
                        PaginationControls(
                            currentPage = result.page,
                            totalPages = result.totalPages,
                            onPageChange = { newPage -> viewModel.loadQuestions(newPage) })
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
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    label = { Text("Ghim") })
            }

            // Hàng các bộ lọc khác
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategorySelected(null) },
                        label = { Text("Tất cả") })
                }
                if (categoriesState is UiState.Success) {
                    items(categoriesState.data) { category ->
                        FilterChip(
                            selected = selectedCategory == category.id,
                            onClick = { onCategorySelected(category.id) },
                            label = { Text(category.name) })
                    }
                }
                item { Spacer(Modifier.width(8.dp)) } // Separator
                item {
                    FilterChip(
                        selected = isSolvedFilter == null,
                        onClick = { onSolvedFilterChanged(null) },
                        label = { Text("Tất cả") })
                }
                item {
                    FilterChip(
                        selected = isSolvedFilter == true,
                        onClick = { onSolvedFilterChanged(true) },
                        label = { Text("Đã giải quyết") })
                }
                item {
                    FilterChip(
                        selected = isSolvedFilter == false,
                        onClick = { onSolvedFilterChanged(false) },
                        label = { Text("Chưa giải quyết") })
                }
                if (tagsState is UiState.Success) {
                    items(tagsState.data.take(10)) { tag ->
                        FilterChip(
                            selected = selectedTag == tag.name,
                            onClick = { onTagSelected(if (selectedTag == tag.name) null else tag.name) },
                            label = { Text(tag.name) })
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
            label = { Text(sortOptions[sortBy] ?: "Sắp xếp") })
        DropdownMenu(
            expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
            sortOptions.forEach { (key, value) ->
                DropdownMenuItem(text = { Text(value) }, onClick = {
                    onSortByChanged(key)
                    showSortMenu = false
                })
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
    Column {
        QuestionHeader(
            userName = question.userName,
            avatar = question.userAvatar,
            timeAgo = question.createdAt,
            title = question.title,
            isPinned = question.isPinned,
            isSolved = question.isSolved
        )
        QuestionTagsRow(tags = question.tags, onTagClick = onTagClick)
        QuestionContent(question.content)
        QuestionStatsRow(
            voteCount = question.voteCount,
            answerCount = question.answerCount,
            userVote = userVote,
            isBookmarked = isBookmarked,
            onVote = onVote,
            onBookmarkClick = onBookmarkClick
            )
    }
}

@Composable
fun QuestionContent(content: String) {
    Text(content, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        color =
    MaterialTheme
        .colorScheme
        .onBackground)
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
                Icon(
                    Icons.Default.Warning,
                    null,
                    tint = if (userVote == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Text(
            text = "$voteCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = when {
                voteCount > 0 -> Color(0xFF10B981)
                voteCount < 0 -> Color(0xFFEF4444)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
        Text("votes", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (showActions) {
            IconButton(onClick = { onVote(-1) }, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Warning,
                    null,
                    tint = if (userVote == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$answerCount",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (answerCount > 0) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text("answers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$viewCount",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text("views", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (showActions) {
            Spacer(Modifier.height(8.dp))
            IconButton(onClick = onBookmarkClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    if (isBookmarked) Icons.Default.Warning else Icons.Default.Warning,
                    null,
                    tint = if (isBookmarked) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun QuestionHeader(
    userName: String,
    avatar: String,
    timeAgo: String,
    title: String,
    isPinned: Boolean,
    isSolved: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SafeImage(
            url = avatar,
            contentDescription = "Avatar: ${userName}",
            error = R.drawable.avatar_default,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Text(text = userName, style = MaterialTheme.typography.titleSmall)
        Text(text = FormatTimeAgo(timeAgo), style = MaterialTheme.typography.labelMedium)


        if (isPinned) {
            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                Text(
                    "Ghim", fontSize = 10.sp
                )
            }
        }
        if (isSolved) {
            Badge(containerColor = Color(0xFF10B981), modifier = Modifier.padding(20.dp,10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.solved), fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun QuestionTagsRow(tags: List<String>, onTagClick: (String) -> Unit) {
    if (tags.isNotEmpty()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(tags.take(5)) { tag ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable { onTagClick(tag) }) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            tag,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionStatsRow(
    voteCount: Int,
    answerCount: Int,
    userVote: Int?,
    isBookmarked: Boolean,
    onVote: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Khối Vote
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp)), // Clip để ripple effect có góc bo tròn
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút Upvote
                IconButton(onClick = { onVote(1) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_up),
                        contentDescription = "Upvote",
                        tint = if (userVote == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "$voteCount",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Nút Downvote
                IconButton(onClick = { onVote(-1) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_down),
                        contentDescription = "Downvote",
                        tint = if (userVote == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Khối số câu trả lời
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Answers",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$answerCount",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Khối Bookmark
        IconButton(
            onClick = onBookmarkClick,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (isBookmarked) Color(0xFFFFB800) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .size(40.dp)
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                contentDescription = "Bookmark",
                tint = if (isBookmarked) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmptyQuestionsView() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Warning,
                null,
                Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text("Không tìm thấy câu hỏi nào", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                "Hãy thử thay đổi bộ lọc hoặc từ khóa tìm kiếm của bạn.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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


