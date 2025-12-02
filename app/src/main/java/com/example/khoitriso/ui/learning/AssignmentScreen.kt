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
import androidx.compose.material.icons.filled.Menu
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
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.ui.common.ErrorDisplay
import com.example.khoitriso.ui.common.LoadingIndicator
import com.example.khoitriso.ui.common.QuestionItem
import com.example.khoitriso.utils.UiState
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

    // --- DRAWER CONTENT (Navigation) ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isAssignmentStarted.value,
        drawerContent = {
            when (val state = assignmentState) {
                is UiState.Success -> {
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
                            CountDownTimer(timeLimitInMinutes = timeLimit)

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
            when (val assignment = assignmentState) {
                is UiState.Error -> {
                    ErrorDisplay(assignment.message) {}
                }

                UiState.Loading -> {
                    LoadingIndicator()
                }

                is UiState.Success<Assignment> -> {
                    AssignmentContent(
                        assignment = assignment.data,
                        isAssignmentStarted = isAssignmentStarted.value,
                        isDone = isDone,
                        listState = listState, // Truyền listState xuống
                        onStartAssignment = {
                            isAssignmentStarted.value = true
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

// --- UI COMPONENT: COUNTDOWN TIMER ---
@SuppressLint("DefaultLocale")
@Composable
fun CountDownTimer(timeLimitInMinutes: Int) {
    // Chuyển đổi phút sang giây
    var ticks by remember { mutableLongStateOf(timeLimitInMinutes * 60L) }

    LaunchedEffect(Unit) {
        while (ticks > 0) {
            delay(1000)
            ticks--
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

                val isAnswered = isAnswers[index] == true

                val backgroundColor = if (isAnswered) {
                    MaterialTheme.colorScheme.primaryContainer // Màu xanh (hoặc màu theme)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant // Màu xám nhạt
                }

                val contentColor = if (isAnswered) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1f) // Hình vuông
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .clickable { onQuestionSelected(index) }
                        .border(
                            width = 1.dp,
                            color = if (isAnswered) Color.Transparent else MaterialTheme.colorScheme.outline.copy(
                                alpha = 0.3f
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
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

// --- MAIN CONTENT ---
@Composable
fun AssignmentContent(
    assignment: Assignment,
    isAssignmentStarted: Boolean,
    isDone: Boolean,
    listState: LazyListState, // Nhận state
    onStartAssignment: () -> Unit,
    modifier: Modifier = Modifier,
    onOptionSelected: (Int, Int) -> Unit,
) {
    val questions = assignment.questions

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
                    Text(stringResource(R.string.no_questions_in_chapter), style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            // Hiển thị danh sách câu hỏi
            items(items = questions, key = { it.id }) { question ->
                QuestionItem(
                    question = question,
                    onOptionSelected = onOptionSelected,

                    )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nút Nộp bài ở cuối danh sách
            item {
                Button(
                    onClick = { /* Handle submit */ },
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
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.question_count, assignment.questions.size)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onStartAssignment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !enable,
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