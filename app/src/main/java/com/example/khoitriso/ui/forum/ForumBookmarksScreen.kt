package com.example.khoitriso.ui.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.request.ForumBookmarksResult
import com.example.khoitriso.ui.behavior.PaginationControls
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.NavRoute
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.getValue
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumBookmarksScreen(
    navController: NavController,
    viewModel: ForumBookMarksViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()

    // Bookmarks state - using Flow from ViewModel
    val bookmarksState by viewModel.bookmarks.collectAsState()
    var currentPage by remember { mutableStateOf(1) }
    val pageSize = 20

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks của tôi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (bookmarksState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                val bookmarks = (bookmarksState as UiState.Success<ForumBookmarksResult>).data.items
                if (bookmarks.isEmpty()) {
                    EmptyBookmarksView(navController)
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "${(bookmarksState as UiState.Success<ForumBookmarksResult>).data.total} câu hỏi đã bookmark",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(bookmarks) { bookmark ->
                            bookmark.question?.let { question ->
                                BookmarkCard(
                                    question = question,
                                    bookmarkDate = bookmark.createdAt,
                                    onRemoveBookmark = {
                                        currentUser.let { userId ->
                                            viewModel.toggleBookmark(
                                                question.id,
                                                userId,
                                                onSuccess = {
                                                    // Refresh list
                                                    val newPage = if (bookmarks.size == 1 && currentPage > 1) {
                                                        currentPage - 1
                                                    } else {
                                                        currentPage
                                                    }
                                                    currentPage = newPage
                                                },
                                                onError = {}
                                            )
                                        }
                                    },
                                    onClick = {
                                        navController.navigate(NavRoute.NavForumDetail(question.id))
                                    }
                                )
                            }
                        }

                        // Pagination
                        if ((bookmarksState as UiState.Success<ForumBookmarksResult>).data.totalPages > 1) {
                            item {
                                PaginationControls(
                                    currentPage = (bookmarksState as UiState.Success<ForumBookmarksResult>).data.page,
                                    totalPages = (bookmarksState as UiState.Success<ForumBookmarksResult>).data.totalPages,
                                    onPageChange = {},
                                )
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                ErrorView(
                    message = (bookmarksState as UiState.Error).message,
                    onRetry = { currentPage = 1 }
                )
            }
        }
    }
}

@Composable
fun BookmarkCard(
    question: ForumQuestion,
    bookmarkDate: String,
    onRemoveBookmark: () -> Unit,
    onClick: () -> Unit
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
            // Stats Column
            Column(
                modifier = Modifier.width(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${question.voteCount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        question.voteCount > 0 -> Color(0xFF10B981)
                        question.voteCount < 0 -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Text("votes", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${question.answerCount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (question.answerCount > 0) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("answers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${question.viewCount}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("views", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Content Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
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
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onRemoveBookmark,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Bookmark,
                            "Xóa bookmark",
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Content Preview
                Text(
                    text = question.content.replace(Regex("<[^>]*>"), "").take(150),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Tags
                if (question.tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        question.tags.take(3).forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Tag, null, Modifier.size(12.dp))
                                    Text(tag, fontSize = 11.sp)
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
                                formatTimeAgo(question.updatedAt ?: question.createdAt),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bookmark, null, Modifier.size(14.dp), tint = Color(0xFFFFB800))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Bookmarked ${formatTimeAgo(bookmarkDate)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyBookmarksView(navController: NavController) {
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
                Icons.Default.BookmarkBorder,
                null,
                Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "Chưa có bookmark",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Hãy bookmark các câu hỏi bạn quan tâm!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(
                onClick = { navController.navigate(NavRoute.FORUM) }
            ) {
                Text("Xem danh sách câu hỏi")
            }
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
