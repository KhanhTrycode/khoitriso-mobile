package com.example.khoitriso.ui.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.models.Material
import com.example.khoitriso.ui.behavior.ErrorDisplay
import com.example.khoitriso.ui.behavior.Media3AndroidView
import com.example.khoitriso.ui.behavior.MyLoadingProcessing
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.toFileSize

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
                        style = MaterialTheme.typography.titleSmall,
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
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// --- COMPOSABLE NỘI DUNG CHÍNH ---

@Composable
fun ContentScreen(
    navController: NavController,
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
                        viewModel.onLessonClicked(lesson.data, context)
                        CurrentLessonInfo(
                            lesson = lesson.data
                        )
                        // 3. THANH NAV VÀ DANH SÁCH BÀI HỌC
                        CourseContentTabs(
                            course = course,
                            currentLesson = lesson.data,
                            onLessonClick = { lesson ->
                                viewModel.onLessonClicked(lesson, context)
                            },
                            onAssignmentClick = { assignment ->
                                navController.navigate(NavRoute.NavAssignment(assignment.id))
                            },
                            onMaterialClick = {material ->
                                viewModel.downloadMaterial(context = context, url = material.fileUrl,
                                    fileName = material.title, fileType = material.fileType)
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
    onAssignmentClick: (Assignment) -> Unit,
    onMaterialClick: (Material) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tổng quan", "Bài tập", "Tài liệu", "Hỏi đáp")

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, fontSize = 10.sp) }
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

            1 -> AssignmentList(
                assignments = currentLesson?.assignments ?: emptyList(),
                currentLesson = currentLesson,
                onAssignmentClick = onAssignmentClick
            )

            2 -> MaterialList(
                materials = currentLesson?.materials ?: emptyList(),
                onMaterialClick = onMaterialClick
            )

            3 -> PlaceholderContent(text = "Tính năng Tài liệu sắp ra mắt")
        }
    }
}

@Composable
fun MaterialList(materials: List<Material>, onMaterialClick: (Material) -> Unit) {
    if (materials.isEmpty()) {
        PlaceholderContent(text = "Bài học này không có tài liệu đính kèm.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(materials) { index, material ->
            MaterialItem(
                material = material,
                onClick = { onMaterialClick(material) }
            )
            Divider()
        }
    }
}

@Composable
fun MaterialItem(
    material: Material,
    onClick: () -> Unit,
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedMaterial by remember { mutableStateOf<Material?>(null) }
    // Chọn icon dựa trên loại file
    val icon = when (material.fileType.lowercase()) {
        "pdf" -> Icons.Default.PictureAsPdf
        "zip", "rar" -> Icons.Default.CloudDownload
        else -> Icons.AutoMirrored.Filled.InsertDriveFile
    }

    val iconColor = when (material.fileType.lowercase()) {
        "pdf" -> Color(0xFFE53935) // Đỏ cho PDF
        "zip" -> Color(0xFFFFB300) // Vàng cho Zip
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon File
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Thông tin File
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = material.fileType.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = material.fileSize.toFileSize(), // Nếu có size
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Nút Download
            IconButton(onClick = {
                selectedMaterial = material
                showConfirmDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = "Tải về",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showConfirmDialog && selectedMaterial != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = { Icon(Icons.Default.CloudDownload, contentDescription = null) },
            title = { Text(text = "Tải xuống tài liệu?") },
            text = {
                Text("Bạn có muốn tải xuống file \"${selectedMaterial!!.fileName}\" không?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClick()
                        showConfirmDialog = false
                    }
                ) {
                    Text("Tải ngay")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun AssignmentList(
    assignments: List<Assignment>,
    currentLesson: Lesson?,
    onAssignmentClick: (Assignment) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(assignments) { index, assignment ->
            AssignmentItem(
                assignment = assignment,
                index = index + 1,
                isPlaying = assignment.id == currentLesson?.id,
                onClick = { onAssignmentClick(assignment) }
            )
            Divider()
        }
    }
}

@Composable
fun AssignmentItem(assignment: Assignment, index: Int, isPlaying: Boolean, onClick: () -> Unit) {
    // Sử dụng Card để có shadow và bo góc
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Phần 1: Title và Description
            Column {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (assignment.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = assignment.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Điểm số
                InfoTag(label = "Điểm tối đa", value = "${assignment.maxScore}")
                InfoTag(label = "Điểm đạt", value = "${assignment.passingScore}")

                // Thời gian
                InfoTag(label = "Thời gian", value = "${assignment.timeLimit} phút")
            }
            InfoTag(label = "Số lần làm bài", value = "${assignment.maxAttempts} lần")


            // Phần 3: Nút Làm bài
            Button(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Làm bài ngay")
            }
        }
    }
}

/**
 * Composable con để hiển thị một tag thông tin (như điểm, thời gian).
 */
@Composable
private fun InfoTag(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
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
