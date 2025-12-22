package com.example.khoitriso.ui.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.ArrowBack
import com.example.khoitriso.R
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.AssignmentPreview
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.models.LessonDiscussion
import com.example.khoitriso.domain.models.Material
import com.example.khoitriso.ui.common.ErrorDisplay
import com.example.khoitriso.ui.common.Media3AndroidView
import com.example.khoitriso.ui.common.VideoPlayerView
import com.example.khoitriso.ui.common.LoadingIndicator
import com.example.khoitriso.ui.common.SafeImage
import com.example.khoitriso.ui.common.FormatTimeAgo
import com.example.khoitriso.ui.forum.KatexHtmlContent
import com.example.khoitriso.ui.common.SafeImage
import com.example.khoitriso.ui.common.FormatTimeAgo
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
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
                        stringResource(R.string.course_content),
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
        ) { LoadingIndicator() }

        is UiState.Error -> ErrorDisplay(message = state.message) { viewModel.getCourseDetail() }
        is UiState.Success -> {
            val course = state.data
            Column(modifier = modifier.fillMaxSize()) {
                // 1. VIDEO PLAYER
                when (val lesson = currentLesson) {
                    is UiState.Success -> {
                        VideoPlayerView(
                            videoUrl = lesson.data.videoUrl,
                            player = player,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {
                        if (player != null) {
                            Media3AndroidView(player = player)
                        }
                    }
                }

                when (val lesson = currentLesson) {
                    is UiState.Loading -> Box(
                        modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { LoadingIndicator() }

                    is UiState.Error -> ErrorDisplay(
                        message = lesson.message,
                        onRetry = {

                        }
                    )

                    is UiState.Success -> {
                        viewModel.onLessonClicked(lesson.data, context)
                        CurrentLessonInfo(
                            lesson = lesson.data,
                            viewModel = viewModel
                        )
                        // 3. THANH NAV VÀ DANH SÁCH BÀI HỌC
                        CourseContentTabs(
                            course = course,
                            currentLesson = lesson.data,
                            onLessonClick = { lesson ->
                                viewModel.onLessonClicked(lesson, context)
                            },
                            onAssignmentClick = { assignment ->
                                // Navigate với assignmentId
                                navController.navigate(NavRoute.NavAssignment(assignment.id))
                            },
                            onMaterialClick = {material ->
                                viewModel.downloadMaterial(context = context, url = material.fileUrl,
                                    fileName = material.title, fileType = material.fileType)
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

// --- CÁC COMPOSABLE CON ---

@Composable
fun CurrentLessonInfo(
    lesson: Lesson,
    viewModel: LearningCourseViewModel = hiltViewModel()
) {
    var showCompleteDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            // Nút hoàn thành bài học
            FilledTonalButton(
                onClick = { showCompleteDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hoàn thành")
            }
        }
        
        // Thay thế bằng mô tả thực của bài học nếu có
        Text(
            text = "Đây là mô tả cho bài học. Nội dung này sẽ giúp bạn hiểu rõ hơn về video.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            title = { Text("Hoàn thành bài học") },
            text = { Text("Đánh dấu bài học này đã hoàn thành?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.markLessonComplete(lesson.id)
                        showCompleteDialog = false
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
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
    onMaterialClick: (Material) -> Unit,
    viewModel: LearningCourseViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tổng quan", "Bài tập", "Tài liệu", "Hỏi đáp")
    
    val assignmentsState by viewModel.assignments.collectAsState()
    val fullAssignmentsState by viewModel.fullAssignments.collectAsState()

    // Load assignments when tab changes to "Bài tập" and lesson is available
    LaunchedEffect(selectedTabIndex, currentLesson?.id) {
        if (selectedTabIndex == 1 && currentLesson != null) {
            // Bước 1: Load danh sách assignment previews từ lesson
            viewModel.loadAssignments(currentLesson.id)
        }
        // Load discussions when tab changes to "Hỏi đáp" and lesson is available
        if (selectedTabIndex == 3 && currentLesson != null) {
            viewModel.loadDiscussions(currentLesson.id)
        }
    }

    // Khi có danh sách assignment previews, load full assignments
    LaunchedEffect(assignmentsState, selectedTabIndex) {
        if (selectedTabIndex == 1 && assignmentsState is UiState.Success) {
            val previews = (assignmentsState as UiState.Success<List<com.example.khoitriso.domain.models.AssignmentPreview>>).data
            viewModel.loadFullAssignments(previews)
        }
    }

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

            1 -> {
                when (val fullAssignments = fullAssignmentsState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Error -> {
                        ErrorDisplay(
                            message = fullAssignments.message,
                            onRetry = {
                                currentLesson?.let { 
                                    viewModel.loadAssignments(it.id)
                                    // Sau khi load previews, sẽ tự động load full assignments
                                }
                            }
                        )
                    }
                    is UiState.Success -> {
                        if (fullAssignments.data.isEmpty()) {
                            PlaceholderContent(text = "Bài học này không có bài tập.")
                        } else {
                            AssignmentList(
                                assignments = fullAssignments.data,
                                currentLesson = currentLesson,
                                onAssignmentClick = onAssignmentClick
                            )
                        }
                    }
                }
            }

            2 -> MaterialList(
                materials = currentLesson?.materials ?: emptyList(),
                onMaterialClick = onMaterialClick
            )

            3 -> {
                if (currentLesson != null) {
                    DiscussionList(
                        lessonId = currentLesson.id,
                        viewModel = viewModel
                    )
                } else {
                    PlaceholderContent(text = stringResource(R.string.please_select_lesson))
                }
            }
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
            title = { Text(stringResource(R.string.download_material_title)) },
            text = {
                Text(stringResource(R.string.download_file_question, selectedMaterial!!.fileName))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClick()
                        showConfirmDialog = false
                    }
                ) {
                    Text(stringResource(R.string.download_now))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(R.string.cancel))
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
                if (assignment.dueDate.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hạn nộp: ${assignment.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Phần 2: Thông tin bài tập (nếu có)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (assignment.timeLimit > 0) {
                    InfoTag(
                        label = "Thời gian",
                        value = "${assignment.timeLimit} phút"
                    )
                }
                if (assignment.maxScore > 0) {
                    InfoTag(
                        label = "Điểm tối đa",
                        value = "${assignment.maxScore} điểm"
                    )
                }
                if (assignment.maxAttempts > 0) {
                    InfoTag(
                        label = "Số lần làm",
                        value = "${assignment.maxAttempts} lần"
                    )
                }
            }

            // Phần 3: Nút Làm bài
            Button(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.start_assignment))
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
            debug(lessons.toString(),"Test")
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
            imageVector = when {
                lesson.isCompleted -> Icons.Default.CheckCircle
                isPlaying -> Icons.Default.PlayCircleOutline
                else -> Icons.Default.CheckCircle
            },
            contentDescription = when {
                lesson.isCompleted -> "Đã hoàn thành"
                isPlaying -> "Đang phát"
                else -> "Chưa phát"
            },
            tint = when {
                lesson.isCompleted -> Color.Green
                isPlaying -> MaterialTheme.colorScheme.primary
                else -> Color.Gray
            }
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$index. ${lesson.title}",
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${lesson.videoDuration} phút",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (lesson.isCompleted) {
                    Text(
                        text = "• Đã hoàn thành",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
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

@Composable
fun DiscussionList(
    lessonId: Int,
    viewModel: LearningCourseViewModel
) {
    val discussionsState by viewModel.discussions.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newDiscussionText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header với nút tạo câu hỏi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hỏi đáp",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showCreateDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.ask_question))
            }
        }

        when (val state = discussionsState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
            is UiState.Error -> {
                ErrorDisplay(
                    message = state.message,
                    onRetry = { viewModel.loadDiscussions(lessonId) }
                )
            }
            is UiState.Success -> {
                val discussions = state.data.items
                if (discussions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.no_discussions),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.be_first_to_ask),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(discussions) { discussion ->
                            DiscussionItem(
                                discussion = discussion,
                                onReplyClick = { }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog tạo câu hỏi mới
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(stringResource(R.string.ask_question)) },
            text = {
                OutlinedTextField(
                    value = newDiscussionText,
                    onValueChange = { newDiscussionText = it },
                    label = { Text(stringResource(R.string.question_content)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDiscussionText.isNotBlank()) {
                            viewModel.createDiscussion(lessonId, newDiscussionText)
                            newDiscussionText = ""
                            showCreateDialog = false
                        }
                    },
                    enabled = newDiscussionText.isNotBlank()
                ) {
                    Text(stringResource(R.string.post))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun DiscussionItem(
    discussion: LessonDiscussion,
    onReplyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header với avatar và tên
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SafeImage(
                    url = discussion.userAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = discussion.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = FormatTimeAgo(discussion.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (discussion.isPinned) {
                    Icon(
                        Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (discussion.isResolved) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Nội dung câu hỏi
            Text(
                text = discussion.content,
                style = MaterialTheme.typography.bodyMedium
            )

            // Video timestamp nếu có
            if (discussion.videoTimestamp > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Tại ${formatTimestamp(discussion.videoTimestamp)}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Divider()

            // Footer với vote và reply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "${discussion.voteCount}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(onClick = onReplyClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.replies_count, discussion.replyCount))
                }
            }
        }
    }
}

private fun formatTimestamp(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}
