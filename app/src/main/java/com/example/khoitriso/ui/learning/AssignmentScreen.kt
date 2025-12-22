package com.example.khoitriso.ui.learning

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.example.khoitriso.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.AssignmentSubmission
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.ui.common.ErrorDisplay
import com.example.khoitriso.ui.common.LoadingIndicator
import com.example.khoitriso.ui.common.QuestionItem
import com.example.khoitriso.ui.forum.KatexHtmlContent
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    navController: NavController,
    viewModel: AssignmentViewModel = hiltViewModel(),
) {
    // --- STATE MANAGEMENT ---
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val listState = rememberLazyListState() // State để điều khiển cuộn trang

    val isAssignmentStarted = remember { mutableStateOf(false) }
    val assignmentState by viewModel.assignment.collectAsState()
    val isStarted by viewModel.isStarted.collectAsState()
    val isDone by viewModel.isDone.collectAsState()
    val isAnswers by viewModel.isAnswers.collectAsState()
    val showSubmitDialog by viewModel.showSubmitDialog.collectAsState()
    val submissionResult by viewModel.submissionResult.collectAsState()
    val attemptCount by viewModel.attemptCount.collectAsState()

    // Đảm bảo drawer luôn đóng khi vào screen hoặc khi assignment chưa started
    LaunchedEffect(Unit) {
        drawerState.close()
    }

    LaunchedEffect(isAssignmentStarted.value) {
        if (!isAssignmentStarted.value) {
            drawerState.close()
        }
    }

    // --- DRAWER CONTENT (Navigation) ---
    // Chỉ hiển thị drawer khi assignment đã started
    if (isAssignmentStarted.value) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                when (val state = assignmentState) {
                    is UiState.Success -> {
                        debug(
                            "Drawer: Questions count = ${state.data.questions.size}",
                            "AssignmentScreen"
                        )
                        AssignmentDrawerGrid(
                            questions = state.data.questions,
                            isAnswers = isAnswers,
                            onQuestionSelected = { index ->
                                scope.launch {
                                    drawerState.close()
                                    listState.animateScrollToItem(index)
                                }
                            }
                        )
                    }

                    else -> {}
                }
            }
        ) {
            AssignmentScaffold(
                navController = navController,
                viewModel = viewModel,
                assignmentState = assignmentState,
                isAssignmentStarted = isAssignmentStarted,
                isDone = isDone,
                isAnswers = isAnswers,
                showSubmitDialog = showSubmitDialog,
                submissionResult = submissionResult,
                attemptCount = attemptCount,
                drawerState = drawerState,
                listState = listState,
                scope = scope
            )
        }
    } else {
        // Không có drawer khi chưa started
        AssignmentScaffold(
            navController = navController,
            viewModel = viewModel,
            assignmentState = assignmentState,
            isAssignmentStarted = isAssignmentStarted,
            isDone = isDone,
            isAnswers = isAnswers,
            showSubmitDialog = showSubmitDialog,
            submissionResult = submissionResult,
            attemptCount = attemptCount,
            drawerState = drawerState,
            listState = listState,
            scope = scope
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentScaffold(
    navController: NavController,
    viewModel: AssignmentViewModel,
    assignmentState: UiState<Assignment>,
    isAssignmentStarted: MutableState<Boolean>,
    isDone: Boolean,
    isAnswers: Map<Int, Boolean>,
    showSubmitDialog: Boolean,
    submissionResult: UiState<AssignmentSubmission>?,
    attemptCount: Int,
    drawerState: DrawerState,
    listState: LazyListState,
    scope: CoroutineScope,
) {
    Scaffold(
        topBar = {
            val title = when (val state = assignmentState) {
                is UiState.Success -> state.data.title
                else -> "Bài tập"
            }

            val timeLimit = when (val state = assignmentState) {
                is UiState.Success -> state.data.timeLimit
                else -> 0
            }

            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    if (isAssignmentStarted.value) {
                        // --- HIỂN THỊ TIMER ---
                        CountDownTimer(
                            timeLimitInMinutes = timeLimit,
                            isStarted = isAssignmentStarted.value
                        )

                        // --- NÚT NỘP BÀI ---
                        if (!isDone) {
                            IconButton(onClick = { viewModel.showSubmitDialog() }) {
                                Icon(Icons.Default.Send, "Nộp bài")
                            }
                        }

                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, "Danh sách câu hỏi")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { contentPadding ->
        // --- HIỂN THỊ SUBMISSION RESULT ---
        when (submissionResult) {
            is UiState.Success -> {
                SubmissionSuccessScreen(
                    submission = submissionResult.data,
                    onBackToLesson = { navController.popBackStack() },
                    modifier = Modifier.padding(contentPadding)
                )
            }
            is UiState.Loading -> {
                LoadingIndicator()
            }
            is UiState.Error -> {
                ErrorDisplay(submissionResult.message) {}
            }
            null -> {
                // --- HIỂN THỊ ASSIGNMENT CONTENT ---
                when (val assignment = assignmentState) {
                    is UiState.Error -> {
                        ErrorDisplay(assignment.message) {}
                    }

                    UiState.Loading -> {
                        LoadingIndicator()
                    }

                    is UiState.Success<Assignment> -> {
                        val currentAssignment = assignment.data

                        // Debug log để kiểm tra số lượng questions
                        LaunchedEffect(currentAssignment.questions.size) {
                            debug(
                                "AssignmentScreen: Questions count = ${currentAssignment.questions.size}",
                                "AssignmentScreen"
                            )
                        }

                        // Sử dụng key để force recomposition khi questions thay đổi
                        AssignmentContent(
                            key = currentAssignment.id to currentAssignment.questions.size,
                            assignment = currentAssignment,
                            isAssignmentStarted = isAssignmentStarted.value,
                            isDone = isDone,
                            attemptCount = attemptCount,
                            canStartAssignment = viewModel.canStartAssignment(),
                            listState = listState, // Truyền listState xuống
                            onStartAssignment = {
                                viewModel.startAssignment()
                                isAssignmentStarted.value = true
                            },
                            onSubmitAssignment = {
                                viewModel.submitAssignment()
                            },
                            modifier = Modifier.padding(contentPadding),
                            onOptionSelected = { optionIndex, questionIndex ->
                                viewModel.onOptionSelected(
                                    questionId = questionIndex, optionId = optionIndex
                                )
                            }
                        )
                    }
                }
            }
        }
    }
    
    // --- DIALOG XÁC NHẬN NỘP BÀI ---
    if (showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideSubmitDialog() },
            title = { Text("Xác nhận nộp bài") },
            text = { Text("Bạn có chắc chắn muốn nộp bài không? Bạn sẽ không thể chỉnh sửa câu trả lời sau khi nộp.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitAssignment() }
                ) {
                    Text("Nộp bài")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideSubmitDialog() }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}


// --- UI COMPONENT: COUNTDOWN TIMER ---
@SuppressLint("DefaultLocale")
@Composable
fun CountDownTimer(timeLimitInMinutes: Int, isStarted: Boolean = true) {
    // Chuyển đổi phút sang giây
    var ticks by remember { mutableLongStateOf(timeLimitInMinutes * 60L) }

    // Chỉ bắt đầu timer khi isStarted = true
    LaunchedEffect(isStarted) {
        if (isStarted) {
            // Reset timer khi bắt đầu
            ticks = timeLimitInMinutes * 60L
            while (ticks > 0) {
                delay(1000)
                ticks--
            }
        }
    }

    val minutes = TimeUnit.SECONDS.toMinutes(ticks)
    val seconds = ticks % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    // Màu sẽ chuyển sang đỏ khi còn dưới 1 phút
    val timerColor =
        if (ticks < 60) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                color = timerColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = timerColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = timeString,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = timerColor
        )
    }
}

// --- NEW DRAWER: GRID LAYOUT ---
@Composable
fun AssignmentDrawerGrid(
    questions: List<Question>,
    isAnswers: Map<Int, Boolean>,
    onQuestionSelected: (Int) -> Unit,
) {
    ModalDrawerSheet(
        modifier = Modifier.width(100.dp)
    ) {
        debug("AssignmentDrawerGrid: Questions count = ${questions.size}", "AssignmentDrawerGrid")

        Text(
            "Tổng số: ${questions.size} câu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Divider()

        LazyVerticalGrid(
            columns = GridCells.Fixed(1), // 5 cột
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(questions) { index, question ->
                // GroupTitle không phải là câu hỏi để trả lời, hiển thị với style khác
                val isGroupTitle = com.example.khoitriso.utils.QuestionType.fromInt(question.questionType) == com.example.khoitriso.utils.QuestionType.GroupTitle
                val isAnswered = isAnswers[question.id] == true

                val backgroundColor = when {
                    isGroupTitle -> MaterialTheme.colorScheme.secondaryContainer
                    isAnswered -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                val contentColor = when {
                    isGroupTitle -> MaterialTheme.colorScheme.onSecondaryContainer
                    isAnswered -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1f) // Hình vuông
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .clickable { onQuestionSelected(index) }
                        .border(
                            width = if (isGroupTitle) 2.dp else 1.dp,
                            color = when {
                                isGroupTitle -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                isAnswered -> Color.Transparent
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    if (isGroupTitle) {
                        Icon(
                            imageVector = Icons.Default.Label,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

// --- MAIN CONTENT ---
@Composable
fun AssignmentContent(
    assignment: Assignment,
    isAssignmentStarted: Boolean,
    isDone: Boolean,
    attemptCount: Int,
    canStartAssignment: Boolean,
    listState: LazyListState, // Nhận state
    onStartAssignment: () -> Unit,
    onSubmitAssignment: () -> Unit,
    modifier: Modifier = Modifier,
    onOptionSelected: (Int, Int) -> Unit,
    key: Pair<Int, Int>? = null, // Key để force recomposition
) {
    val questions = assignment.questions

    // Debug log để kiểm tra
    LaunchedEffect(questions.size) {
        debug("AssignmentContent: Questions count = ${questions.size}", "AssignmentContent")
    }

    // Tính số thứ tự câu hỏi (bỏ GroupTitle) - tính trước khi render
    val questionNumbers = remember(questions) {
        var num = 0
        questions.map { question ->
            val isGroupTitle = com.example.khoitriso.utils.QuestionType.fromInt(question.questionType) == 
                com.example.khoitriso.utils.QuestionType.GroupTitle
            if (!isGroupTitle) {
                num++
                question.id to num
            } else {
                question.id to null
            }
        }.toMap()
    }

    LazyColumn(
        state = listState, // Gán state để điều khiển cuộn
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isAssignmentStarted) {
            item {
                AssignmentInfoCard(
                    assignment = assignment,
                    attemptCount = attemptCount,
                    canStartAssignment = canStartAssignment,
                    onStartAssignment = onStartAssignment,
                    enable = isDone,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else if (questions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_questions_in_chapter),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            // Hiển thị danh sách câu hỏi
            items(items = questions, key = { it.id }) { question ->
                QuestionItem(
                    question = question,
                    onOptionSelected = onOptionSelected,
                    questionNumber = questionNumbers[question.id]
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nút Nộp bài ở cuối danh sách
            item {
                Button(
                    onClick = onSubmitAssignment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.submit_assignment).uppercase())
                }
            }
        }
    }
}

@Composable
fun AssignmentInfoCard(
    assignment: Assignment,
    attemptCount: Int,
    canStartAssignment: Boolean,
    onStartAssignment: () -> Unit,
    enable: Boolean,
    modifier: Modifier = Modifier,
) {
    // UI Card đẹp hơn
    ElevatedCard(
        modifier = modifier.padding(vertical = 16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Các chip thông tin nhỏ
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.time_limit, assignment.timeLimit)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Timer,
                            null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                // Chỉ hiển thị số câu hỏi nếu đã có questions (khi đã started)
                if (assignment.questions.isNotEmpty()) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                stringResource(
                                    R.string.question_count,
                                    assignment.questions.size
                                )
                            )
                        }
                    )
                }
                
                // Hiển thị số lần đã nộp
                if (assignment.maxAttempts > 0) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Lần nộp: $attemptCount / ${assignment.maxAttempts}") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (!canStartAssignment) 
                                MaterialTheme.colorScheme.errorContainer 
                            else 
                                MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (assignment.description.isNotBlank()) {
                KatexHtmlContent(
                    html = assignment.description,
                    textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textSizeSp = 16f
                )
            }
            
            // Thông báo nếu vượt quá số lần nộp
            if (!canStartAssignment && assignment.maxAttempts > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Bạn đã hết lượt làm bài. Số lần nộp tối đa: ${assignment.maxAttempts}",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onStartAssignment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !enable && canStartAssignment,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "BẮT ĐẦU LÀM BÀI",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}