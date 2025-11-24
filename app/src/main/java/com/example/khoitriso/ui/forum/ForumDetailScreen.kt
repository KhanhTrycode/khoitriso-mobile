package com.example.khoitriso.ui.forum

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf
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
import com.example.khoitriso.domain.repository.*
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumDetailScreen(
    navController: NavController,
    questionId: String,
    viewModel: ForumViewModel = hiltViewModel()
) {
    val questionState by viewModel.question.collectAsState()
    val answersState by viewModel.answers.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val userVotes by viewModel.userVotes.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    
    var showAnswerForm by remember { mutableStateOf(false) }
    var answerContent by remember { mutableStateOf("") }
    var submittingAnswer by remember { mutableStateOf(false) }
    
    var showCommentForms by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var commentContents by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var submittingComments by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    
    val coroutineScope = rememberCoroutineScope()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val currentUserAvatar by viewModel.currentUserAvatar.collectAsState()
    
    LaunchedEffect(questionId) {
        viewModel.loadQuestionById(questionId)
        viewModel.loadAnswers(questionId)
        viewModel.loadComments(1, questionId) // 1 = Question
    }
    
    LaunchedEffect(questionId, currentUserId) {
        currentUserId?.let { userId ->
            viewModel.loadUserVote(1, questionId, userId)
            viewModel.loadBookmarkStatus(questionId, userId)
        }
    }
    
    LaunchedEffect(answersState) {
        when (answersState) {
            is UiState.Success -> {
                answersState.data.forEach { answer ->
                    viewModel.loadComments(2, answer.id) // 2 = Answer
                    if (currentUserId != null) {
                        viewModel.loadUserVote(2, answer.id, currentUserId)
                    }
                }
            }
            else -> {}
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
        when (questionState) {
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
                val question = questionState.data
                val answers = when (answersState) {
                    is UiState.Success -> answersState.data.sortedWith(
                        compareBy<ForumAnswer>(
                            { !it.isAccepted }, // Accepted first
                            { -it.voteCount } // Then by vote count
                        )
                    )
                    else -> emptyList()
                }
                
                val isQuestionOwner = currentUserId != null && currentUserId == question.userId
                val canAcceptAnswer = isQuestionOwner // Only question owner can accept
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Question Section
                    item {
                        QuestionDetailCard(
                            question = question,
                            userVote = userVotes["1-${question.id}"],
                            isBookmarked = bookmarks.contains(question.id),
                            currentUserId = currentUserId ?: 0,
                            onVote = { voteType ->
                                currentUserId?.let { userId ->
                                    viewModel.vote(1, question.id, userId, voteType, {}, {})
                                }
                            },
                            onBookmark = {
                                currentUserId?.let { userId ->
                                    viewModel.toggleBookmark(question.id, userId, {}, {})
                                }
                            },
                            comments = comments[question.id] ?: emptyList(),
                            showCommentForm = showCommentForms[question.id] == true,
                            commentContent = commentContents[question.id] ?: "",
                            onCommentContentChange = {
                                commentContents = commentContents.toMutableMap().apply {
                                    put(question.id, it)
                                }
                            },
                            onShowCommentForm = {
                                showCommentForms = showCommentForms.toMutableMap().apply {
                                    put(question.id, !(this[question.id] ?: false))
                                }
                            },
                            onSubmitComment = {
                                currentUserId?.let { userId ->
                                    coroutineScope.launch {
                                        viewModel.createComment(
                                            CreateCommentRequest(
                                                parentId = question.id,
                                                parentType = 1,
                                                content = commentContents[question.id] ?: "",
                                                userId = userId,
                                                userName = currentUserName ?: "User",
                                                userAvatar = null
                                            ),
                                            {},
                                            {}
                                        )
                                        commentContents = commentContents.toMutableMap().apply {
                                            remove(question.id)
                                        }
                                        showCommentForms = showCommentForms.toMutableMap().apply {
                                            put(question.id, false)
                                        }
                                    }
                                }
                            },
                            onDeleteQuestion = {
                                // TODO: Implement delete
                            }
                        )
                    }
                    
                    // Answers Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${answers.size} ${if (answers.size == 1) "Câu trả lời" else "Câu trả lời"}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentUserId != null && !question.isSolved) {
                                Button(
                                    onClick = { showAnswerForm = !showAnswerForm }
                                ) {
                                    Text("Trả lời")
                                }
                            }
                        }
                    }
                    
                    // Answer Form
                    if (showAnswerForm && currentUserId != null) {
                        item {
                            AnswerFormCard(
                                content = answerContent,
                                onContentChange = { answerContent = it },
                            onSubmit = {
                                currentUserId?.let { userId ->
                                    coroutineScope.launch {
                                        submittingAnswer = true
                                        viewModel.createAnswer(
                                            questionId,
                                            CreateAnswerRequest(
                                                content = answerContent,
                                                userId = userId,
                                                userName = currentUserName ?: "User",
                                                userAvatar = null
                                            ),
                                            onSuccess = {
                                                submittingAnswer = false
                                                answerContent = ""
                                                showAnswerForm = false
                                            },
                                            onError = {
                                                submittingAnswer = false
                                            }
                                        )
                                    }
                                }
                            },
                                onCancel = {
                                    showAnswerForm = false
                                    answerContent = ""
                                },
                                submitting = submittingAnswer
                            )
                        }
                    }
                    
                    // Answers List
                    if (answers.isEmpty()) {
                        item {
                            EmptyAnswersView(
                                showAnswerButton = currentUserId != null && !question.isSolved,
                                onAnswerClick = { showAnswerForm = true }
                            )
                        }
                    } else {
                        items(answers) { answer ->
                            AnswerCard(
                                answer = answer,
                                userVote = userVotes["2-${answer.id}"],
                                canAccept = canAcceptAnswer && !answer.isDeleted,
                                isAnswerOwner = currentUserId == answer.userId,
                            currentUserId = currentUserId ?: 0,
                            onVote = { voteType ->
                                currentUserId?.let { userId ->
                                    viewModel.vote(2, answer.id, userId, voteType, {}, {})
                                }
                            },
                                onAccept = {
                                    viewModel.acceptAnswer(answer.id, {}, {})
                                },
                                onUnaccept = {
                                    viewModel.unacceptAnswer(answer.id, {}, {})
                                },
                                comments = comments[answer.id] ?: emptyList(),
                                showCommentForm = showCommentForms[answer.id] == true,
                                commentContent = commentContents[answer.id] ?: "",
                                onCommentContentChange = {
                                    commentContents = commentContents.toMutableMap().apply {
                                        put(answer.id, it)
                                    }
                                },
                                onShowCommentForm = {
                                    showCommentForms = showCommentForms.toMutableMap().apply {
                                        put(answer.id, !(this[answer.id] ?: false))
                                    }
                                },
                                onSubmitComment = {
                                    currentUserId?.let { userId ->
                                        coroutineScope.launch {
                                            viewModel.createComment(
                                                CreateCommentRequest(
                                                    parentId = answer.id,
                                                    parentType = 2,
                                                    content = commentContents[answer.id] ?: "",
                                                    userId = userId,
                                                    userName = currentUserName ?: "User",
                                                    userAvatar = null
                                                ),
                                                {},
                                                {}
                                            )
                                            commentContents = commentContents.toMutableMap().apply {
                                                remove(answer.id)
                                            }
                                            showCommentForms = showCommentForms.toMutableMap().apply {
                                                put(answer.id, false)
                                            }
                                        }
                                    }
                                },
                                onDelete = {
                                    // TODO: Implement delete
                                }
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Default.Error, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Text(questionState.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Quay lại")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionDetailCard(
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
    onDeleteQuestion: () -> Unit
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
                IconButton(
                    onClick = { onVote(1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        null,
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
                IconButton(
                    onClick = { onVote(-1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        null,
                        tint = if (userVote == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (currentUserId != null) {
                    IconButton(
                        onClick = onBookmark,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
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
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textDecoration = if (question.isDeleted) TextDecoration.LineThrough else null
                    )
                }
                
                // Content HTML
                HtmlContent(
                    html = question.content,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Tags
                if (question.tags.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(question.tags) { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Tag, null, Modifier.size(12.dp))
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(question.userName, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(formatTimeAgo(question.createdAt), fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("${question.viewCount} lượt xem", fontSize = 12.sp)
                        }
                    }
                }
                
                // Comments Section
                if (comments.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        comments.forEach { comment ->
                            CommentItem(comment = comment)
                        }
                    }
                }
                
                // Add Comment Button/Form
                if (currentUserId != null) {
                    if (showCommentForm) {
                        CommentForm(
                            content = commentContent,
                            onContentChange = onCommentContentChange,
                            onSubmit = onSubmitComment,
                            onCancel = onShowCommentForm
                        )
                    } else {
                        TextButton(onClick = onShowCommentForm) {
                            Text("Thêm bình luận", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerCard(
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
    onDelete: () -> Unit
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
                IconButton(
                    onClick = { onVote(1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        null,
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
                IconButton(
                    onClick = { onVote(-1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        null,
                        tint = if (userVote == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (canAccept) {
                    if (answer.isAccepted) {
                        // Show unaccept button when answer is already accepted
                        IconButton(
                            onClick = onUnaccept,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                "Hủy chấp nhận",
                                tint = Color(0xFF10B981)
                            )
                        }
                    } else {
                        // Show accept button when answer is not accepted
                        IconButton(
                            onClick = onAccept,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircleOutline,
                                "Chấp nhận câu trả lời",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (answer.isAccepted) {
                    Text(
                        "✓ Đã chấp nhận",
                        fontSize = 10.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Content Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HtmlContent(
                    html = answer.content,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Meta Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(answer.userName, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(formatTimeAgo(answer.createdAt), fontSize = 12.sp)
                        }
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
                if (comments.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Divider()
                        comments.forEach { comment ->
                            CommentItem(comment = comment)
                        }
                    }
                }
                
                // Add Comment Button/Form
                if (currentUserId != null) {
                    if (showCommentForm) {
                        CommentForm(
                            content = commentContent,
                            onContentChange = onCommentContentChange,
                            onSubmit = onSubmitComment,
                            onCancel = onShowCommentForm
                        )
                    } else {
                        TextButton(onClick = onShowCommentForm) {
                            Text("Thêm bình luận", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: ForumComment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        HtmlContent(
            html = comment.content,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                comment.userName,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                "•",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                formatTimeAgo(comment.createdAt),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CommentForm(
    content: String,
    onContentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
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
            Button(onClick = onSubmit, enabled = content.isNotBlank()) {
                Text("Gửi")
            }
            OutlinedButton(onClick = onCancel) {
                Text("Hủy")
            }
        }
    }
}

@Composable
fun AnswerFormCard(
    content: String,
    onContentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    submitting: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Câu trả lời của bạn",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                placeholder = { Text("Nhập câu trả lời của bạn...") },
                maxLines = 10
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSubmit,
                    enabled = content.isNotBlank() && !submitting
                ) {
                    if (submitting) {
                        CircularProgressIndicator(Modifier.size(16.dp))
                    } else {
                        Text("Gửi câu trả lời")
                    }
                }
                OutlinedButton(onClick = onCancel) {
                    Text("Hủy")
                }
            }
        }
    }
}

@Composable
fun EmptyAnswersView(
    showAnswerButton: Boolean,
    onAnswerClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Message,
                null,
                Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                "Chưa có câu trả lời",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Hãy là người đầu tiên trả lời câu hỏi này!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (showAnswerButton) {
                Button(onClick = onAnswerClick) {
                    Text("Trả lời")
                }
            }
        }
    }
}

@Composable
// HtmlContent đã được move ra file riêng HtmlContent.kt

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

