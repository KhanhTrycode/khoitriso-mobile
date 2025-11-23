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
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumListScreen(
    navController: NavController,
    viewModel: ForumViewModel = hiltViewModel()
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
    var showFilters by remember { mutableStateOf(false) }
    
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
                        Icon(Icons.Default.Bookmark, "Bookmarks")
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
            // Search and Filters
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
                    // Search Bar
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tìm kiếm câu hỏi...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { 
                                    searchText = ""
                                    viewModel.setSearchQuery("")
                                }) {
                                    Icon(Icons.Default.Clear, null)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Sort and Filter Row
                    var showSortMenu by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sort Dropdown
                        Box {
                            FilterChip(
                                selected = sortBy != null,
                                onClick = { showSortMenu = true },
                                label = { 
                                    Text(
                                        when (sortBy) {
                                            "newest" -> "Mới nhất"
                                            "oldest" -> "Cũ nhất"
                                            "votes" -> "Nhiều vote"
                                            "activity" -> "Hoạt động"
                                            "unanswered" -> "Chưa trả lời"
                                            else -> "Sắp xếp"
                                        }
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Hoạt động") },
                                    onClick = {
                                        viewModel.setSortBy("activity")
                                        showSortMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Mới nhất") },
                                    onClick = {
                                        viewModel.setSortBy("newest")
                                        showSortMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Cũ nhất") },
                                    onClick = {
                                        viewModel.setSortBy("oldest")
                                        showSortMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Nhiều vote nhất") },
                                    onClick = {
                                        viewModel.setSortBy("votes")
                                        showSortMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Chưa trả lời") },
                                    onClick = {
                                        viewModel.setSortBy("unanswered")
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                        
                        // Filter Pinned
                        FilterChip(
                            selected = isPinnedFilter == true,
                            onClick = { 
                                viewModel.setIsPinnedFilter(if (isPinnedFilter == true) null else true)
                            },
                            label = { Text("Ghim") }
                        )
                    }
                    
                    // Filter Buttons Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { viewModel.setSelectedCategory(null) },
                                label = { Text("Tất cả") }
                            )
                        }
                        
                        when (categoriesState) {
                            is UiState.Success -> {
                                items(categoriesState.data) { category ->
                                    FilterChip(
                                        selected = selectedCategory == category.id,
                                        onClick = { viewModel.setSelectedCategory(category.id) },
                                        label = { Text(category.name) }
                                    )
                                }
                            }
                            else -> {}
                        }
                        
                        item {
                            FilterChip(
                                selected = isSolvedFilter == null,
                                onClick = { viewModel.setIsSolvedFilter(null) },
                                label = { Text("Tất cả") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = isSolvedFilter == true,
                                onClick = { viewModel.setIsSolvedFilter(true) },
                                label = { Text("Đã giải quyết") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = isSolvedFilter == false,
                                onClick = { viewModel.setIsSolvedFilter(false) },
                                label = { Text("Chưa giải quyết") }
                            )
                        }
                        
                        // Popular Tags Filter
                        when (tagsState) {
                            is UiState.Success -> {
                                items(tagsState.data.take(10)) { tag ->
                                    FilterChip(
                                        selected = selectedTag == tag.name,
                                        onClick = { 
                                            viewModel.setSelectedTag(if (selectedTag == tag.name) null else tag.name)
                                        },
                                        label = { Text(tag.name) }
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                    
                    // Search Button
                    Button(
                        onClick = { viewModel.setSearchQuery(searchText) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = searchText != searchQuery
                    ) {
                        Icon(Icons.Default.Filter, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Lọc")
                    }
                }
            }
            
            // Questions List
            when (questionsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    val result = questionsState.data
                    if (result.items.isEmpty()) {
                        EmptyQuestionsView()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(result.items) { question ->
                                QuestionCard(
                                    question = question,
                                    userVote = userVotes["1-${question.id}"],
                                    isBookmarked = bookmarks.contains(question.id),
                                    currentUserId = currentUserId ?: 0,
                                    onClick = { navController.navigate(com.example.khoitriso.utils.NavRoute.NavForumDetail(question.id)) },
                                    onVote = { voteType ->
                                        currentUserId?.let { userId ->
                                            viewModel.vote(1, question.id, userId, voteType, {}, {})
                                        }
                                    },
                                    onBookmarkClick = {
                                        currentUserId?.let { userId ->
                                            viewModel.toggleBookmark(question.id, userId, {}, {})
                                        }
                                    },
                                    onTagClick = { tag ->
                                        viewModel.setSelectedTag(if (selectedTag == tag) null else tag)
                                    }
                                )
                            }
                            
                            // Pagination
                            if (result.totalPages > 1) {
                                item {
                                    PaginationControls(
                                        currentPage = result.page,
                                        totalPages = result.totalPages,
                                        onPrevious = { if (result.page > 1) viewModel.loadQuestions(result.page - 1) },
                                        onNext = { if (result.page < result.totalPages) viewModel.loadQuestions(result.page + 1) }
                                    )
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorView(message = questionsState.message) {
                        viewModel.loadQuestions()
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionCard(
    question: ForumQuestion,
    userVote: Int?,
    isBookmarked: Boolean,
    currentUserId: Int,
    onClick: () -> Unit,
    onVote: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
    onTagClick: (String) -> Unit
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
            // Vote Column
            Column(
                modifier = Modifier.width(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Vote buttons
                if (currentUserId > 0) {
                    IconButton(
                        onClick = { onVote(1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowUpward,
                            null,
                            tint = if (userVote == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Text(
                    text = "${question.voteCount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        question.voteCount > 0 -> Color(0xFF10B981)
                        question.voteCount < 0 -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Text("votes", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                if (currentUserId > 0) {
                    IconButton(
                        onClick = { onVote(-1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowDownward,
                            null,
                            tint = if (userVote == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "${question.answerCount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (question.answerCount > 0) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("answers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "${question.viewCount}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("views", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                // Bookmark button
                if (currentUserId > 0) {
                    Spacer(Modifier.height(8.dp))
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            null,
                            tint = if (isBookmarked) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // Content Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title and Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (question.isPinned) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text("Ghim", fontSize = 10.sp)
                        }
                    }
                    if (question.isSolved) {
                        Badge(containerColor = Color(0xFF10B981)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, Modifier.size(12.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Đã giải quyết", fontSize = 10.sp)
                            }
                        }
                    }
                    
                    Text(
                        text = question.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Content Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    HtmlContent(
                        html = question.content.substring(0, minOf(200, question.content.length)) + 
                               (if (question.content.length > 200) "..." else ""),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp) // Preview height
                    )
                }
                
                // Tags
                if (question.tags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(question.tags.take(5)) { tag ->
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
                                    Icon(Icons.Default.Tag, null, Modifier.size(12.dp))
                                    Text(tag, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }
                }
                
                // Meta Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(4.dp))
                            Text(question.userName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        
                        question.categoryName?.let { category ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    category,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                formatTimeAgo(question.lastActivityAt ?: question.updatedAt ?: question.createdAt),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Trang $currentPage / $totalPages",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = currentPage > 1
            ) {
                Text("Trước")
            }
            OutlinedButton(
                onClick = onNext,
                enabled = currentPage < totalPages
            ) {
                Text("Sau")
            }
        }
    }
}

@Composable
fun EmptyQuestionsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Message,
                null,
                Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "Không tìm thấy câu hỏi nào",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Error,
                null,
                Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Thử lại")
            }
        }
    }
}

@Composable
// HtmlContent đã được move ra file riêng HtmlContent.kt

@SuppressLint("SimpleDateFormat")
fun formatTimeAgo(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return dateString
        val now = Date()
        val diffInSeconds = (now.time - date.time) / 1000
        
        when {
            diffInSeconds < 60 -> "vừa xong"
            diffInSeconds < 3600 -> "${diffInSeconds / 60} phút trước"
            diffInSeconds < 86400 -> "${diffInSeconds / 3600} giờ trước"
            diffInSeconds < 2592000 -> "${diffInSeconds / 86400} ngày trước"
            diffInSeconds < 31536000 -> "${diffInSeconds / 2592000} tháng trước"
            else -> "${diffInSeconds / 31536000} năm trước"
        }
    } catch (e: Exception) {
        dateString
    }
}

