package com.example.khoitriso.ui.learning

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.Chapter
import com.example.khoitriso.domain.models.Option
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.ui.forum.HtmlContent
import com.example.khoitriso.utils.QuestionType
import com.example.khoitriso.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningBookScreen(
    navController: NavController,
    viewModel: LearningBookViewModel = hiltViewModel(),
) {
    val bookState by viewModel.book.collectAsState()
    val chapterState by viewModel.chapters.collectAsState()
    val questionsState by viewModel.questions.collectAsState()
    val currentChapterIndex by viewModel.currentChapterIndex.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface, // Màu nền sạch hơn
        topBar = {
            LearningBookTopBar(
                title = (bookState as? UiState.Success)?.data?.title ?: "Đang tải...",
                onBackClicked = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (bookState is UiState.Success && chapterState is UiState.Success) {
                val bookData = (bookState as UiState.Success<BookDetail>).data
                LearningBookBottomBar(
                    currentChapterIndex = currentChapterIndex,
                    totalChapters = bookData.chapters.size,
                    onPreviousChapter = { viewModel.changeChapter(-1) },
                    onNextChapter = { viewModel.changeChapter(1) }
                )
            }
        },
        floatingActionButton = {
            // Nút Submit nổi bật (Floating Action Button)
            ExtendedFloatingActionButton(
                onClick = { /* Handle submit */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Rounded.Check, contentDescription = null) },
                text = { Text("Nộp bài") }
            )
        }
    ) { paddingValues ->
        // Hiệu ứng chuyển cảnh mượt mà hơn (Slide + Fade)
        AnimatedContent(
            targetState = currentChapterIndex, // Dùng index để trigger animation
            modifier = Modifier.padding(paddingValues),
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut())
                }
            },
            label = "ChapterTransition"
        ) { targetIndex ->
            // Lưu ý: targetIndex là index, cần lấy data từ state
            val currentState = chapterState // Capture state hiện tại

            when (currentState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Lỗi: ${currentState.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is UiState.Success -> {
                    val chapter = currentState.data.getOrNull(targetIndex)
                    val questions = (questionsState as? UiState.Success)?.data ?: emptyList()

                    if (chapter != null) {
                        ChapterContent(chapter = chapter, questions = questions)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningBookTopBar(title: String, onBackClicked: () -> Unit) {
    CenterAlignedTopAppBar( // Canh giữa tiêu đề trông trang trọng hơn
        title = {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}

@Composable
fun ChapterContent(modifier: Modifier = Modifier, chapter: Chapter, questions: List<Question>) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp), // Để tránh bị che bởi FAB
    ) {
        // Phần Header của Chương
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        OptimizedHtmlContent(html = chapter.description)
                    }
                }
            }
        }

        // Tiêu đề phần câu hỏi
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Help,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Câu hỏi ôn tập (${questions.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (questions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chương này chưa có câu hỏi.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        } else {
            items(items = questions, key = { it.id }) { question ->
                QuestionItem(question = question, onOptionSelected = {})
            }
        }
    }
}

@Composable
fun QuestionItem(question: Question, onOptionSelected: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "Câu ${question.orderIndex}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Nội dung câu hỏi
                Box(modifier = Modifier.weight(1f)) {
                    OptimizedHtmlContent(html = question.questionContent)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            OptionList(
                questionType = question.questionType,
                options = question.options,
                onOptionSelected = onOptionSelected
            )
        }
    }
}

@Composable
fun OptionList(
    questionType: Int,
    options: List<Option>,
    onOptionSelected: (Int) -> Unit,
) {
    when (questionType) {
        QuestionType.MultipleChoice -> MultipleChoiceOptionList(options, onOptionSelected)
        QuestionType.TrueFalse -> TrueFalseOptionList(options, onOptionSelected)
        QuestionType.ShortAnswer -> ShortAnswerInput()
        else -> Text("Loại câu hỏi chưa hỗ trợ", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun OptimizedHtmlContent(html: String) {
    val rememberedHtml = remember(html) { html }
    HtmlContent(rememberedHtml)
}

@Composable
fun MultipleChoiceOptionList(options: List<Option>, onOptionSelected: (Int) -> Unit) {
    val (selectedOptionId, setSelectedOption) = remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            val isSelected = option.id == selectedOptionId

            // Animation màu sắc
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                label = "bgColor"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                label = "borderColor"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            setSelectedOption(option.id)
                            onOptionSelected(option.id)
                        }
                    ),
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Radio Icon
                    Icon(
                        imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        OptimizedHtmlContent(html = option.optionText)
                    }
                }
            }
        }
    }
}

@Composable
fun TrueFalseOptionList(
    options: List<Option>,
    onOptionSelected: (Int) -> Unit,
) {
    val (selectedId, setSelected) = remember { mutableStateOf<Int?>(null) }
    val trueOption = options.firstOrNull { it.optionText.equals("Đúng", ignoreCase = true) || it.optionText.equals("True", ignoreCase = true) }
    val falseOption = options.firstOrNull { it.optionText.equals("Sai", ignoreCase = true) || it.optionText.equals("False", ignoreCase = true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (trueOption != null) {
            TrueFalseCard(
                text = "Đúng",
                isSelected = selectedId == trueOption.id,
                icon = Icons.Rounded.Check,
                color = Color(0xFF4CAF50), // Màu xanh lá
                modifier = Modifier.weight(1f),
                onClick = {
                    setSelected(trueOption.id)
                    onOptionSelected(trueOption.id)
                }
            )
        }
        if (falseOption != null) {
            TrueFalseCard(
                text = "Sai",
                isSelected = selectedId == falseOption.id,
                icon = Icons.Rounded.Close,
                color = Color(0xFFE53935), // Màu đỏ
                modifier = Modifier.weight(1f),
                onClick = {
                    setSelected(falseOption.id)
                    onOptionSelected(falseOption.id)
                }
            )
        }
    }
}

@Composable
fun TrueFalseCard(
    text: String,
    isSelected: Boolean,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
    )
    val borderColor by animateColorAsState(
        if (isSelected) color else MaterialTheme.colorScheme.outlineVariant
    )

    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ShortAnswerInput() {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Nhập câu trả lời...") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            Icon(Icons.Rounded.Edit, contentDescription = null)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
fun LearningBookBottomBar(
    currentChapterIndex: Int,
    totalChapters: Int,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
) {
    val currentChapter = currentChapterIndex + 1
    val isPreviousEnabled = currentChapterIndex > 0
    val isNextEnabled = currentChapterIndex < totalChapters - 1
    val progress = if (totalChapters > 0) currentChapter.toFloat() / totalChapters else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .shadow(elevation = 16.dp)
    ) {
        // Thanh tiến trình mảnh
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nút Previous
            FilledTonalButton(
                onClick = onPreviousChapter,
                enabled = isPreviousEnabled,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Trước")
            }

            // Text chỉ số
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CHƯƠNG",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$currentChapter / $totalChapters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Nút Next
            Button(
                onClick = onNextChapter,
                enabled = isNextEnabled,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Sau")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
