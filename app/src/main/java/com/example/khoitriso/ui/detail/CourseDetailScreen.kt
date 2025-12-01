package com.example.khoitriso.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.ui.behavior.*
import com.example.khoitriso.utils.ItemBuyNow
import com.example.khoitriso.utils.ItemType
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiEvent
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug

@Composable
fun CourseDetailScreen(
    navController: NavController,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val courseState by viewModel.course.collectAsState()
    val player by viewModel.playerState.collectAsState()
    val snackBarState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initializePlayer(
            context,
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        )
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.releasePlayer() }
    }

    ObserverAsEvent(viewModel.events) { event ->
        when (event) {
            is UiEvent.ShowSnackbar -> {
                snackBarState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        topBar = {
            DetailHeader(onBack = { navController.popBackStack() })
        },
        bottomBar = {
            if (courseState is UiState.Success) {
                ActionButtons(
                    price = (courseState as UiState.Success<CourseDetail>).data.price,
                    onBuy = {
                        val course =(courseState as UiState.Success<CourseDetail>).data
                        navController.navigate(ItemBuyNow(
                            itemId = course.id,
                            itemType = ItemType.Book,
                            coverImage = course.thumbnail,
                            price = course.price,
                            title = course.title,
                        ))
                    },
                    onCart = { viewModel.addToCart((courseState as UiState.Success<CourseDetail>).data.id) }
                )
            }
        }
    ) { innerPadding ->
        when (courseState) {
            is UiState.Error -> { /* Handle Error UI */
            }

            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success<CourseDetail> -> {
                val course = (courseState as UiState.Success<CourseDetail>).data
                DetailContent(
                    course,
                    player,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    course: CourseDetail,
    player: ExoPlayer?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp) // Chỉ padding bottom cho list
    ) {
        // 1. Video Player (Full Width - Không padding ngang)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(color = Color.Black)
            ) {
                Media3AndroidView(player)
            }
        }

        // 2. Thông tin chính (Có padding ngang)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                CourseInfo(course)
            }
        }

        // 3. Mục "What you'll learn"
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoSection(
                        title = "Bạn sẽ học được gì",
                        icon = Icons.Default.CheckCircle,
                        items = course.whatYouWillLearn,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 4. Mục "Requirements"
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                InfoSection(
                    title = "Yêu cầu",
                    icon = Icons.Default.CheckCircle,
                    items = course.requirements,
                    modifier = Modifier
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
            }
        }

        // 5. Mục "Course Lessons"
        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nội dung khóa học",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${course.lessons.size} bài học",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        items(course.lessons) { lesson ->
            LessonRow(
                lesson = lesson,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun CourseInfo(course: CourseDetail) {
    Text(
        text = course.title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = course.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(12.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {

        FractionalRatingStars(rating = course.rating) // Giả sử component này có size nhỏ
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "(${course.totalReviews} đánh giá)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    CreateBy(
        fullName = course.instructor.name,
        avatar = course.instructor.avatar
    )
}

@Composable
private fun InfoSection(
    title: String,
    icon: ImageVector,
    items: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        items.forEach { item ->
            InfoRow(text = item, icon = icon)
        }
    }
}

@Composable
private fun InfoRow(text: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LessonRow(
    lesson: Lesson,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon trạng thái (Play hoặc Lock)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (lesson.isFree) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (lesson.isFree) Icons.Default.PlayCircle else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (lesson.isFree) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )
                Text(
                    text = "${lesson.videoDuration / 60} phút",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}