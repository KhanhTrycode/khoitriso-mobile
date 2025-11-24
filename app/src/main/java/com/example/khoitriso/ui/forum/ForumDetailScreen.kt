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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.auth0.jwt.JWT
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.request.CreateAnswerRequest
import com.example.khoitriso.domain.request.CreateCommentRequest
import com.example.khoitriso.ui.behavior.FormatTimeAgo
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumDetailScreen(
    navController: NavController,
    questionId: String,
    viewModel: ForumViewModel = hiltViewModel(),
) {
    val questionState by viewModel.question.collectAsState()
    val answersState by viewModel.answers.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val userVotes by viewModel.userVotes.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    val currentUserId by viewModel.currentUserId.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()

    // State cho các form
    var showAnswerForm by remember { mutableStateOf(false) }
    var answerContent by remember { mutableStateOf("") }
    var submittingAnswer by remember { mutableStateOf(false) }

    var showCommentForms by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var commentContents by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Tải dữ liệu ban đầu
    LaunchedEffect(questionId) {
        viewModel.loadQuestionById(questionId)
        viewModel.loadAnswers(questionId)
        viewModel.loadComments(1, questionId) // 1 = Question type
    }

    // Tải trạng thái vote và bookmark của người dùng
    LaunchedEffect(questionId, currentUserId) {
        currentUserId?.let { userId ->
            viewModel.loadUserVote(1, questionId, userId)
            viewModel.loadBookmarkStatus(questionId, userId)
        }
    }

    // Tải comment và vote cho các câu trả lời
    LaunchedEffect(answersState) {
        if (answersState is UiState.Success) {
            (answersState as UiState.Success<List<ForumAnswer>>).data.forEach { answer ->
                viewModel.loadComments(2, answer.id) // 2 = Answer type
                currentUserId?.let { userId ->
                    viewModel.loadUserVote(2, answer.id, userId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết câu hỏi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = questionState) {
            is UiState.Loading -> {
                Box(Modifier
                    .fillMaxSize()
                    .padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                val question = state.data
                val answers = (answersState as? UiState.Success)?.data?.sortedWith(
                    compareBy({ !it.isAccepted }, { -it.voteCount })
                ) ?: emptyList()

                val isQuestionOwner = currentUserId != null && currentUserId == question.userId

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // PHẦN CÂU HỎI
                    item {
                        QuestionDetailCard(
                            question = question,
                            userVote = userVotes["1-${question.id}"],
                            isBookmarked = bookmarks.contains(question.id),
                            currentUserId = currentUserId ?: 0,
                            onVote = { voteType ->
                                currentUserId?.let { viewModel.vote(1, question.id, it, voteType) }
                            },
                            onBookmark = {
                                currentUserId?.let { viewModel.toggleBookmark(question.id, it) }
                            },
                            comments = comments[question.id] ?: emptyList(),
                            showCommentForm = showCommentForms[question.id] == true,
                            commentContent = commentContents[question.id] ?: "",
                            onCommentContentChange = { newContent -> commentContents = commentContents + (question.id to newContent) },
                            onShowCommentForm = { showCommentForms = showCommentForms + (question.id to !(showCommentForms[question.id] ?: false)) },
                            onSubmitComment = {
                                currentUserId?.let { userId ->
                                    val content = commentContents[question.id] ?: ""
                                    viewModel.createComment(CreateCommentRequest(question.id, 1, content, userId, currentUserName ?: "User"))
                                    commentContents = commentContents - question.id
                                    showCommentForms = showCommentForms - question.id
                                }
                            },
                            onDeleteQuestion = { /* TODO */ }
                        )
                    }

                    // PHẦN CÂU TRẢ LỜI
                    item {
                        AnswersHeader(
                            answerCount = answers.size,
                            showAnswerButton = currentUserId != null && !question.isSolved,
                            onAnswerClick = { showAnswerForm = !showAnswerForm }
                        )
                    }

                    if (showAnswerForm && currentUserId != null) {
                        item {
                            AnswerFormCard(
                                content = answerContent,
                                onContentChange = { answerContent = it },
                                onSubmit = {
                                    submittingAnswer = true
                                    viewModel.createAnswer(
                                        questionId,
                                        CreateAnswerRequest(answerContent, currentUserId!!, currentUserName ?: "User"),
                                        onSuccess = {
                                            submittingAnswer = false; answerContent = ""; showAnswerForm = false
                                        },
                                        onError = { submittingAnswer = false }
                                    )
                                },
                                onCancel = { showAnswerForm = false; answerContent = "" },
                                submitting = submittingAnswer
                            )
                        }
                    }

                    if (answers.isEmpty()) {
                        item {
                            EmptyAnswersView(
                                showAnswerButton = currentUserId != null && !question.isSolved,
                                onAnswerClick = { showAnswerForm = true }
                            )
                        }
                    } else {
                        items(answers, key = { it.id }) { answer ->
                            AnswerCard(
                                answer = answer,
                                userVote = userVotes["2-${answer.id}"],
                                canAccept = isQuestionOwner && !answer.isDeleted,
                                isAnswerOwner = currentUserId == answer.userId,
                                currentUserId = currentUserId ?: 0,
                                onVote = { voteType ->
                                    currentUserId?.let { viewModel.vote(2, answer.id, it, voteType) }
                                },
                                onAccept = { viewModel.acceptAnswer(answer.id) },
                                onUnaccept = { viewModel.unacceptAnswer(answer.id) },
                                comments = comments[answer.id] ?: emptyList(),
                                showCommentForm = showCommentForms[answer.id] == true,
                                commentContent = commentContents[answer.id] ?: "",
                                onCommentContentChange = { newContent -> commentContents = commentContents + (answer.id to newContent) },
                                onShowCommentForm = { showCommentForms = showCommentForms + (answer.id to !(showCommentForms[answer.id] ?: false)) },
                                onSubmitComment = {
                                    currentUserId?.let { userId ->
                                        val content = commentContents[answer.id] ?: ""
                                        viewModel.createComment(CreateCommentRequest(answer.id, 2, content, userId, currentUserName ?: "User"))
                                        commentContents = commentContents - answer.id
                                        showCommentForms = showCommentForms - answer.id
                                    }
                                },
                                onDelete = { /* TODO */ }
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                ErrorDisplay(message = state.message, onRetry = { navController.popBackStack() })
            }
        }
    }
}

// --- TÁCH COMPOSABLE CON RA ĐÂY ---

@Composable
private fun AnswersHeader(answerCount: Int, showAnswerButton: Boolean, onAnswerClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$answerCount ${if (answerCount <= 1) "Câu trả lời" else "Câu trả lời"}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (showAnswerButton) {
            Button(onClick = onAnswerClick) { Text("Trả lời") }
        }
    }
}

@Composable
private fun QuestionDetailCard(
    question: ForumQuestion,
    userVote: Int?,
    isBookmarked: Boolean,
    currentUserId: Int,
    onVote: (Int) -> Unit,
    onBookmark: () -> Unit,
    comments: List<ForumComment>,
    showCommentForm: Boolean,
    commentContent: String,
    onCommentContentChange: (String) -> Unit,
    onShowCommentForm: () -> Unit,
    onSubmitComment: () -> Unit,
    onDeleteQuestion: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onVote(1) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.Warning, null,
                        tint = if (userVote == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${question.voteCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        question.voteCount > 0 -> Color(0xFF10B981)
                        question.voteCount < 0 -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                IconButton(onClick = { onVote(-1) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.Warning, null,
                        tint = if (userVote == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (currentUserId != 0) {
                    IconButton(onClick = onBookmark, modifier = Modifier.size(40.dp)) {
                        Icon(
                            if (isBookmarked) Icons.Default.Warning else Icons.Default.Warning,
                            null,
                            tint = if (isBookmarked) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Content Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title and Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (question.isPinned) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("Ghim", fontSize = 10.sp) }
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
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textDecoration = if (question.isDeleted) TextDecoration.LineThrough else null
                    )
                }

                // Content HTML
                Text(
                    text = android.text.Html.fromHtml(question.content, android.text.Html.FROM_HTML_MODE_COMPACT).toString()
                )


                // Tags
                if (question.tags.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(question.tags) { tag ->
                            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, null, Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
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
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetaInfoItem(icon = Icons.Default.Person, text = question.userName)
                        MetaInfoItem(icon = Icons.Default.Warning, text = FormatTimeAgo(question
                            .createdAt))
                        MetaInfoItem(icon = Icons.Default.Warning, text = "${question.viewCount} lượt " +
                                "xem")
                    }
                }

                // Comments Section
                CommentSection(comments, showCommentForm, commentContent, onCommentContentChange, onShowCommentForm, onSubmitComment, currentUserId != 0)
            }
        }
    }
}

@Composable
private fun AnswerCard(
    answer: ForumAnswer,
    userVote: Int?,
    canAccept: Boolean,
    isAnswerOwner: Boolean,
    currentUserId: Int,
    onVote: (Int) -> Unit,
    onAccept: () -> Unit,
    onUnaccept: () -> Unit,
    comments: List<ForumComment>,
    showCommentForm: Boolean,
    commentContent: String,
    onCommentContentChange: (String) -> Unit,
    onShowCommentForm: () -> Unit,
    onSubmitComment: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (answer.isAccepted)
                Color(0xFF10B981).copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (answer.isAccepted)
            BorderStroke(2.dp, Color(0xFF10B981))
        else null
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onVote(1) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.Warning, null,
                        tint = if (userVote == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${answer.voteCount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        answer.voteCount > 0 -> Color(0xFF10B981)
                        answer.voteCount < 0 -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                IconButton(onClick = { onVote(-1) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.Warning, null,
                        tint = if (userVote == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (canAccept) {
                    IconButton(
                        onClick = if (answer.isAccepted) onUnaccept else onAccept,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            if (answer.isAccepted) Icons.Default.CheckCircle else Icons.Default.Warning,
                            if (answer.isAccepted) "Hủy chấp nhận" else "Chấp nhận câu trả lời",
                            tint = if (answer.isAccepted) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (answer.isAccepted) {
                    Text("✓ Đã chấp nhận", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }

            // Content Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = android.text.Html.fromHtml(answer.content, android.text.Html.FROM_HTML_MODE_COMPACT).toString()
                )


                // Meta Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(answer.userName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        MetaInfoItem(icon = Icons.Default.Warning, text = FormatTimeAgo(answer
                            .createdAt))
                    }
                    if (isAnswerOwner) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { /* TODO: Edit */ }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, null, Modifier.size(16.dp))
                            }
                            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, null, Modifier.size(16.dp), tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }

                // Comments Section
                CommentSection(comments, showCommentForm, commentContent, onCommentContentChange, onShowCommentForm, onSubmitComment, currentUserId != 0, isNested = true)
            }
        }
    }
}

@Composable
private fun CommentSection(
    comments: List<ForumComment>,
    showCommentForm: Boolean,
    commentContent: String,
    onCommentContentChange: (String) -> Unit,
    onShowCommentForm: () -> Unit,
    onSubmitComment: () -> Unit,
    canComment: Boolean,
    isNested: Boolean = false,
) {
    Column(
        modifier = if (isNested) Modifier.padding(start = 16.dp) else Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (comments.isNotEmpty()) {
            if (isNested) Divider()
            comments.forEach { comment -> CommentItem(comment = comment) }
        }
        if (canComment) {
            if (showCommentForm) {
                CommentForm(
                    content = commentContent,
                    onContentChange = onCommentContentChange,
                    onSubmit = onSubmitComment,
                    onCancel = onShowCommentForm
                )
            } else {
                TextButton(onClick = onShowCommentForm, contentPadding = PaddingValues(0.dp)) {
                    Text("Thêm bình luận", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: ForumComment) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(text = comment.content, fontSize = 14.sp)
        Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(comment.userName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(FormatTimeAgo(comment.createdAt), fontSize = 11.sp, color = MaterialTheme
                .colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CommentForm(
    content: String,
    onContentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Thêm bình luận...") },
            maxLines = 3
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onSubmit, enabled = content.isNotBlank()) { Text("Gửi") }
            OutlinedButton(onClick = onCancel) { Text("Hủy") }
        }
    }
}

@Composable
private fun AnswerFormCard(
    content: String,
    onContentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    submitting: Boolean,
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Câu trả lời của bạn", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text("Nhập câu trả lời của bạn...") },
                maxLines = 10
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSubmit, enabled = content.isNotBlank() && !submitting) {
                    if (submitting) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Gửi câu trả lời")
                    }
                }
                OutlinedButton(onClick = onCancel) { Text("Hủy") }
            }
        }
    }
}

@Composable
private fun EmptyAnswersView(showAnswerButton: Boolean, onAnswerClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Warning, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme
                .onSurfaceVariant.copy(alpha = 0.5f))
            Text("Chưa có câu trả lời", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Hãy là người đầu tiên trả lời câu hỏi này!", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (showAnswerButton) {
                Button(onClick = onAnswerClick) { Text("Trả lời") }
            }
        }
    }
}

@Composable
private fun ErrorDisplay(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Warning, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme
                .error)
            Text(message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) { Text("Quay lại") }
        }
    }
}

@Composable
private fun MetaInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

