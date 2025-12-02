package com.example.khoitriso.ui.forum

import android.text.Html
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.requestFocus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.request.CreateAnswerRequest
import com.example.khoitriso.domain.request.CreateCommentRequest
import com.example.khoitriso.ui.common.*
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug

// --- DATA CLASSES VÀ ENUMS CHO QUẢN LÝ STATE ---

enum class BottomBarMode {
    HIDDEN,
    ANSWER_QUESTION,
    COMMENT_ANSWER,
    COMMENT_QUESTION,
}

data class CommentBarState(
    val mode: BottomBarMode = BottomBarMode.HIDDEN,
    val targetId: String? = null,
    val targetType: Int? = null,
    val placeholderText: String = "",
)


// --- MÀN HÌNH CHÍNH ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumDetailScreen(
    navController: NavController,
    viewModel: ForumDetalQuestionViewModel = hiltViewModel(),
) {
    val currentUserState by viewModel.currentUser.collectAsState()
    val questionState by viewModel.question.collectAsState()

    var commentBarState by remember { mutableStateOf(CommentBarState()) }

    // Thiết lập chế độ mặc định cho bottom bar khi câu hỏi tải xong
    LaunchedEffect(questionState) {
        val question = (questionState as? UiState.Success)?.data
        if (question != null) {
            commentBarState = CommentBarState(
                mode = BottomBarMode.ANSWER_QUESTION,
                targetId = question.id,
                targetType = 1,
                placeholderText = "Viết câu trả lời của bạn..."
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết câu hỏi",
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
        },
        bottomBar = {
            val question = (questionState as? UiState.Success)?.data
            val currentUser = (currentUserState as? UiState.Success)?.data
            // Chỉ hiển thị bottom bar nếu đã có câu hỏi, người dùng đã đăng nhập và câu hỏi chưa được giải quyết
            if (question != null && currentUser != null) {
                ForumBottomBar(
                    state = commentBarState,
                    currentUser = currentUser,
                    viewModel = viewModel,
                    onDismiss = {
                        // Sau khi gửi, quay lại chế độ trả lời câu hỏi mặc định
                        commentBarState = CommentBarState(
                            mode = BottomBarMode.ANSWER_QUESTION,
                            targetId = question.id,
                            targetType = 1,
                            placeholderText = "Viết câu trả lời của bạn..."
                        )
                    }
                )
            }
        },
    ) { paddingValues ->
        when (val state = questionState) {
            is UiState.Loading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { LoadingIndicator() }

            is UiState.Success -> {
                ContentScreen(
                    viewModel = viewModel,
                    question = state.data,
                    modifier = Modifier.padding(paddingValues),
                    onCommentBarStateChange = { newState -> commentBarState = newState }
                )
            }

            is UiState.Error -> ErrorDisplay(
                message = state.message,
                onRetry = { viewModel.getQuestionById() })
        }
    }
}


// --- COMPOSABLE NỘI DUNG CHÍNH ---

@Composable
private fun ContentScreen(
    viewModel: ForumDetalQuestionViewModel,
    question: ForumQuestion,
    modifier: Modifier = Modifier,
    onCommentBarStateChange: (CommentBarState) -> Unit,
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val answersState by viewModel.answers.collectAsState()
    val comments by viewModel.comments.collectAsState() // Map<String, List<ForumComment>>
    val userVoteAnswers by viewModel.userVoteAnswers.collectAsState()
    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val isVote by viewModel.isVoted.collectAsState()

    val sortedAnswers = (answersState as? UiState.Success)?.data?.sortedWith(
        compareBy({ !it.isAccepted }, { -it.voteCount })
    ) ?: emptyList()

    val currentUserId = (currentUser as? UiState.Success<User>)?.data?.id
    val isQuestionOwner = currentUserId != null && currentUserId == question.userId

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- PHẦN CÂU HỎI ---
        item {
            QuestionCard(
                question = question,
                userVote = isVote,
                comments = comments[question.id] ?: emptyList(),
                isBookmarked = isBookmarked,
                onVote = { voteType ->
                    currentUserId?.let { viewModel.vote(1, question.id, it, voteType) }
                },
                onBookmarkClick = {
                    currentUserId?.let { viewModel.toggleBookmark(question.id, it) }
                },
                onTagClick = { },
                onCommentBarStateChange = onCommentBarStateChange
            )
        }

        // --- PHẦN BÌNH LUẬN CỦA CÂU HỎI ---
        item {
            CommentSection(
                comments = comments[question.id] ?: emptyList(),
            )
        }

        // --- PHẦN HEADER CÂU TRẢ LỜI ---
        item {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            AnswersHeader(answerCount = sortedAnswers.size)
        }

        // --- DANH SÁCH CÂU TRẢ LỜI ---
        if (sortedAnswers.isEmpty()) {
            item { EmptyAnswersView() }
        } else {
            items(sortedAnswers, key = { it.id }) { answer ->
                AnswerCard(
                    answer = answer,
                    userVote = userVoteAnswers["2-${question.id}"],
                    canAccept = isQuestionOwner && !answer.isDeleted,
                    onVote = { voteType ->
                        currentUserId?.let { viewModel.vote(2, answer.id, it, voteType) }
                    },
                    onAccept = { viewModel.acceptAnswer(answer.id) },
                    onUnaccept = { viewModel.unacceptAnswer(answer.id) },
                    onCommentClick = {
                        onCommentBarStateChange(
                            CommentBarState(
                                mode = BottomBarMode.COMMENT_ANSWER,
                                targetId = answer.id,
                                targetType = 2,
                                placeholderText = "Trả lời bình luận của ${answer.userName}..."
                            )
                        )
                    },
                    comments = comments[answer.id] ?: emptyList(),
                    onCommentBarStateChange = onCommentBarStateChange
                    )
            }
        }
    }
}

// --- BOTTOM BAR ĐA NĂNG ---

@Composable
private fun ForumBottomBar(
    state: CommentBarState,
    currentUser: User,
    viewModel: ForumDetalQuestionViewModel,
    onDismiss: () -> Unit,
) {
    if (state.mode == BottomBarMode.HIDDEN) return

    var inputText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val localFocusManager = LocalFocusManager.current

    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(state.mode, state.targetId) {
        if (state.mode == BottomBarMode.COMMENT_ANSWER || state.mode == BottomBarMode.COMMENT_QUESTION) {
            kotlinx.coroutines.delay(100) // Đợi UI sẵn sàng
            focusRequester.requestFocus()
        }
    }

    val isReplyingMode =
        state.mode == BottomBarMode.COMMENT_ANSWER || state.mode == BottomBarMode.COMMENT_QUESTION

    // Bọc toàn bộ BottomBar trong một Surface để có màu nền và độ cao đồng nhất
    Surface(
        tonalElevation = 3.dp
    ) {
        Column {
            // Box "Đang trả lời..."
            if (isReplyingMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.placeholderText.replace("...", ""),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, "Hủy")
                    }
                }
            }

            // Hàng nhập liệu chính
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Thêm padding top nếu không ở chế độ trả lời để có khoảng cách
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = if(isReplyingMode) 0.dp else 8.dp),
                verticalAlignment = if (isFocused) Alignment.Top else Alignment.CenterVertically, // Căn chỉnh nút gửi
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState -> isFocused = focusState.isFocused },
                    placeholder = { Text(if (isReplyingMode) "Thêm bình luận..." else state.placeholderText) },
                    // Thay đổi maxLines để cho phép mở rộng khi focus
                    maxLines = if (isFocused) 5 else 1,
                    shape = RoundedCornerShape(24.dp),
                    // Giảm padding bên trong để textfield nhỏ lại khi không focus
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
                )

                IconButton(
                    onClick = {
                        // Vô hiệu hóa nút và bàn phím ngay lập tức
                        isSubmitting = true
                        localFocusManager.clearFocus()

                        val commonOnSuccess = {
                            isSubmitting = false
                            inputText = ""
                            onDismiss()
                        }
                        val commonOnError = {
                            isSubmitting = false
                            // TODO: Hiển thị toast hoặc snackbar báo lỗi
                        }

                        when (state.mode) {
                            BottomBarMode.ANSWER_QUESTION -> {
                                viewModel.createAnswer(
                                    questionId = state.targetId!!,
                                    request = CreateAnswerRequest(inputText, currentUser.id, currentUser.fullName),
                                    onSuccess = commonOnSuccess,
                                    onError = { commonOnError() } // Thêm xử lý lỗi
                                )
                            }
                            BottomBarMode.COMMENT_ANSWER, BottomBarMode.COMMENT_QUESTION -> {
                                viewModel.createComment(
                                    request = CreateCommentRequest(state.targetId!!, state.targetType!!, inputText, currentUser.id, currentUser.fullName),
                                    onSuccess = commonOnSuccess,
                                    onFailure = commonOnError // Sửa lại thành onFailure nếu ViewModel dùng tên đó
                                )
                            }
                            else -> isSubmitting = false
                        }
                    },
                    enabled = inputText.isNotBlank() && !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Send, "Gửi", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

// --- CÁC CARD HIỂN THỊ ---

@Composable
private fun QuestionCard(
    question: ForumQuestion,
    comments : List<ForumComment>,
    userVote: Int?,
    isBookmarked: Boolean,
    onVote: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
    onTagClick: (String) -> Unit,
    onCommentBarStateChange: (CommentBarState) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuestionHeader(
            userName = question.userName,
            avatar = question.userAvatar,
            timeAgo = question.createdAt,
            title = question.title,
            isPinned = question.isPinned,
            isSolved = question.isSolved,
            isBookmarked = isBookmarked,
            onBookmarkClick = onBookmarkClick
        )
        QuestionTagsRow(tags = question.tags, onTagClick = onTagClick)
        QuestionContent(question.content)
        QuestionStatsRow(
            voteCount = question.voteCount,
            viewCount = question.viewCount,
            commentCount = comments.size,
            userVote = userVote,
            onVote = onVote,
            onCommentClick = {
                onCommentBarStateChange(
                    CommentBarState(
                        mode = BottomBarMode.COMMENT_QUESTION,
                        targetId = question.id,
                        targetType = 1,
                        placeholderText = "Đang bình luận cho câu hỏi..."
                    )
                )
            }
        )
    }
}

@Composable
private fun AnswerCard(
    answer: ForumAnswer,
    userVote: Int?,
    canAccept: Boolean,
    onVote: (Int) -> Unit,
    onAccept: () -> Unit,
    onUnaccept: () -> Unit,
    onCommentClick: () -> Unit,
    comments: List<ForumComment>,
    onCommentBarStateChange: (CommentBarState) -> Unit,
) {
    debug("comments: $comments", "ForumDetailScreen")
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (answer.isAccepted) Color(0xFF10B981).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        border = if (answer.isAccepted) BorderStroke(2.dp, Color(0xFF10B981)) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnswerHeader(
                answer.userName,
                answer.userAvatar,
                answer.createdAt,
                answer.isAccepted
            )
            Text(Html.fromHtml(answer.content, Html.FROM_HTML_MODE_COMPACT).toString())
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VoteGroup(userVote, answer.voteCount, onVote)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onCommentClick) {
                        Icon(Icons.Default.Comment, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${comments.size}") // Số lượng không cần stringResource
                    }
                    if (canAccept) {
                        IconButton(onClick = if (answer.isAccepted) onUnaccept else onAccept) {
                            Icon(
                                if (answer.isAccepted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                null,
                                tint = if (answer.isAccepted) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            CommentSection(comments)
        }
    }
}

// --- CÁC COMPOSABLE HỖ TRỢ ---

@Composable
fun CommentSection(
    comments: List<ForumComment>,
) {
    if (comments.isEmpty()) return
    val isExpanded = remember { mutableStateOf(false) }

    val commentsToShow = if (comments.size > 2) {
        if (isExpanded.value) comments else comments.take(2)
    } else comments

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 16.dp)
    ) {
        commentsToShow.forEach { CommentItem(it) }
        if (comments.size > 2) {
            TextButton(
                onClick = { isExpanded.value = !isExpanded.value },
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Text(
                    if (isExpanded.value) "Ẩn bớt" else "Xem thêm ${comments.size - 2} bình luận",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CommentItem(comment: ForumComment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = comment.content, fontSize = 14.sp)
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(comment.userName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.bullet), fontSize = 11.sp)
            Text(FormatTimeAgo(comment.createdAt), fontSize = 11.sp)
        }
    }
}

@Composable
private fun QuestionHeader(
    userName: String, avatar: String, timeAgo: String, title: String,
    isPinned: Boolean, isSolved: Boolean, isBookmarked: Boolean, onBookmarkClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = 48.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SafeImage(
                    url = avatar,
                    error = R.drawable.avatar_default,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentDescription = ""
                )
                Text(text = userName, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = FormatTimeAgo(timeAgo),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isPinned) Badge { Text(stringResource(R.string.pinned)) }
                if (isSolved) Badge(containerColor = Color(0xFF10B981)) { Text(stringResource(R.string.solved_badge)) }
            }
        }
        IconButton(onClick = onBookmarkClick, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(
                if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                "Bookmark",
                tint = if (isBookmarked) Color(0xFFFFB800) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AnswerHeader(
    userName: String,
    avatar: String,
    timeAgo: String,
    isAccepted: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SafeImage(
                url = avatar,
                error = R.drawable.avatar_default,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentDescription = ""
            )
            Text(userName, style = MaterialTheme.typography.titleSmall)
            Text(FormatTimeAgo(timeAgo), style = MaterialTheme.typography.labelMedium)
        }
        if (isAccepted) {
            Badge(containerColor = Color(0xFF10B981)) {
                Text(
                    "✓ Đã chấp nhận",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun QuestionTagsRow(tags: List<String>, onTagClick: (String) -> Unit) {
    if (tags.isNotEmpty()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(tags) { tag ->
                SuggestionChip(onClick = { onTagClick(tag) }, label = { Text(tag) })
            }
        }
    }
}

@Composable
private fun QuestionContent(content: String) {
    Text(
        Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT).toString(),
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun QuestionStatsRow(
    voteCount: Int,
    viewCount: Int,
    commentCount: Int,
    userVote: Int?,
    onVote: (Int) -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VoteGroup(userVote, voteCount, onVote)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.clickable(onClick = onCommentClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.ChatBubbleOutline, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$commentCount", style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.RemoveRedEye, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$viewCount", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun VoteGroup(userVote: Int?, voteCount: Int, onVote: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            debug("Vote up", "ForumDetailScreen")
            onVote(1)
        }, modifier = Modifier.size(40.dp)) {
            Icon(
                painterResource(R.drawable.arrow_up),
                null,
                tint = if (userVote == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text("$voteCount", fontWeight = FontWeight.Bold)
        IconButton(onClick = {
            onVote(-1)
            debug("Vote down", "ForumDetailScreen")

        }, modifier = Modifier.size(40.dp)) {
            Icon(
                painterResource(R.drawable.arrow_down),
                null,
                tint = if (userVote == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AnswersHeader(answerCount: Int) {
    Text(
        "$answerCount câu trả lời",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EmptyAnswersView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.ChatBubbleOutline,
                null,
                Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(stringResource(R.string.no_answers), style = MaterialTheme.typography.titleMedium)
            Text(
                "Hãy là người đầu tiên giúp đỡ bằng cách đưa ra câu trả lời của bạn.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
