package com.example.khoitriso.ui.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.ui.behavior.ErrorDisplay
import com.example.khoitriso.ui.behavior.Media3AndroidView
import com.example.khoitriso.ui.behavior.MyLoadingProcessing
import com.example.khoitriso.utils.UiState

// --- MÀN HÌNH CHÍNH (SCAFFOLD) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningCourseScreen(
    navController: NavController,
    viewModel: LearningCourseViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        viewModel.onScreenAppeared(context)
        onDispose {
            viewModel.releasePlayer()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nội dung khóa học",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        ContentScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// --- COMPOSABLE NỘI DUNG CHÍNH ---

@Composable
fun ContentScreen(
    viewModel: LearningCourseViewModel,
    modifier: Modifier = Modifier,
) {
    val courseState by viewModel.courseDetail.collectAsState()
    val player by viewModel.playerState.collectAsState()
    val currentLesson by viewModel.currentLesson.collectAsState()
    val context = LocalContext.current

    when (val state = courseState) {
        is UiState.Loading -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { MyLoadingProcessing() }

        is UiState.Error -> ErrorDisplay(message = state.message) { viewModel.getCourseDetail() }
        is UiState.Success -> {
            val course = state.data
            Column(modifier = modifier.fillMaxSize()) {
                // 1. VIDEO PLAYER
                Media3AndroidView(player = player)

                when (val lesson = currentLesson) {
                    is UiState.Loading -> Box(
                        modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { MyLoadingProcessing() }

                    is UiState.Error -> ErrorDisplay(
                        message = lesson.message,
                        onRetry = {

                        }
                    )

                    is UiState.Success -> {
                        viewModel.onLessonClicked(lesson.data,context)
                        CurrentLessonInfo(
                            lesson = lesson.data
                        )
                        // 3. THANH NAV VÀ DANH SÁCH BÀI HỌC
                        CourseContentTabs(
                            course = course,
                            currentLesson = lesson.data,
                            onLessonClick = { lesson ->
                                viewModel.onLessonClicked(lesson, context)
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- CÁC COMPOSABLE CON ---

@Composable
fun CurrentLessonInfo(lesson: Lesson) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        // Thay thế bằng mô tả thực của bài học nếu có
        Text(
            text = "Đây là mô tả cho bài học. Nội dung này sẽ giúp bạn hiểu rõ hơn về video.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Chứa TabRow (Lesson, Notes, ...) và nội dung tương ứng.
 */
@Composable
fun CourseContentTabs(
    course: CourseDetail,
    currentLesson: Lesson?,
    onLessonClick: (Lesson) -> Unit,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Bài học", "Ghi chú", "Bài tập", "Tài liệu")

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        // Hiển thị nội dung tương ứng với tab được chọn
        when (selectedTabIndex) {
            0 -> LessonList(
                lessons = course.lessons,
                currentLesson = currentLesson,
                onLessonClick = onLessonClick
            )

            1 -> PlaceholderContent(text = "Tính năng Ghi chú sắp ra mắt")
            2 -> PlaceholderContent(text = "Tính năng Bài tập sắp ra mắt")
            3 -> PlaceholderContent(text = "Tính năng Tài liệu sắp ra mắt")
        }
    }
}

/**
 * Danh sách các bài học trong khóa học.
 */
@Composable
fun LessonList(
    lessons: List<Lesson>,
    currentLesson: Lesson?,
    onLessonClick: (Lesson) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(lessons) { index, lesson ->
            LessonItem(
                lesson = lesson,
                index = index + 1,
                isPlaying = lesson.id == currentLesson?.id,
                onClick = { onLessonClick(lesson) }
            )
            Divider()
        }
    }
}

/**
 * Một mục (item) trong danh sách bài học.
 */
@Composable
fun LessonItem(
    lesson: Lesson,
    index: Int,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isPlaying) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.PlayCircleOutline else Icons.Default.CheckCircle,
            contentDescription = if (isPlaying) "Đang phát" else "Chưa phát",
            tint = if (isPlaying) MaterialTheme.colorScheme.primary else Color.Gray
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$index. ${lesson.title}",
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${lesson.videoDuration} phút",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PlaceholderContent(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
    }
}
