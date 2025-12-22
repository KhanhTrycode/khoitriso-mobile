package com.example.khoitriso.ui.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.example.khoitriso.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.request.ForumBookmarksResult
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.NavRoute
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.example.khoitriso.ui.common.SafeImage

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
                title = { Text(stringResource(R.string.my_bookmarks)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = bookmarksState) {
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
                val bookmarksData = state.data
                val bookmarks = bookmarksData
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
                                stringResource(R.string.bookmarks_count, bookmarksData.size),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(bookmarks) { bookmark ->
                            bookmark.question?.let { question ->
                                BookmarkCard(
                                    question = question,
                                    onClick = { 
                                        navController.navigate(NavRoute.NavForumDetail(question.id))
                                    }
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
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SafeImage(
                    url = question.userAvatar,
                    contentDescription = "Avatar: ${question.userName}",
                    error = R.drawable.avatar_default,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = question.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Title
            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Content preview
            Text(
                text = question.content.replace(Regex("<[^>]*>"), "").take(100),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
                text = stringResource(R.string.no_bookmarks),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.no_bookmarks_message),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(
                onClick = { navController.navigate(NavRoute.FORUM) }
            ) {
                Text(stringResource(R.string.view_question_list))
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
                Text(stringResource(R.string.try_again))
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
            diffInSeconds < 60 -> "vừa xong" // TODO: Use stringResource
            diffInSeconds < 3600 -> "${diffInSeconds / 60} phút trước" // TODO: Use stringResource
            diffInSeconds < 86400 -> "${diffInSeconds / 3600} giờ trước" // TODO: Use stringResource
            diffInSeconds < 2592000 -> "${diffInSeconds / 86400} ngày trước" // TODO: Use stringResource
            diffInSeconds < 31536000 -> "${diffInSeconds / 2592000} tháng trước" // TODO: Use stringResource
            else -> "${diffInSeconds / 31536000} năm trước" // TODO: Use stringResource
        }
    } catch (e: Exception) {
        dateString
    }
}
