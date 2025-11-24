package com.example.khoitriso.ui.forum

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.repository.*
import com.example.khoitriso.domain.request.CreateQuestionRequest
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.launch

// file: ui/forum/ForumAskScreen.kt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumAskScreen(
    navController: NavController,
    viewModel: ForumViewModel = hiltViewModel(),
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentTag by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categoriesState by viewModel.categories.collectAsState()
    val tagsState by viewModel.tags.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadTags(30)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đặt câu hỏi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { AskScreenHeader() }

            item { TitleInputCard(title = title, onTitleChange = { title = it }) }

            item {
                CategorySelectionCard(
                    categoriesState = categoriesState,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelected = { selectedCategoryId = it }
                )
            }

            item { ContentInputCard(content = content, onContentChange = { content = it }) }

            item {
                TagsSelectionCard(
                    tagsState = tagsState,
                    selectedTags = selectedTags,
                    currentTag = currentTag,
                    onCurrentTagChange = { currentTag = it },
                    onAddTag = {
                        if (currentTag.isNotBlank() && !selectedTags.contains(currentTag.trim())) {
                            selectedTags = selectedTags + currentTag.trim()
                            currentTag = ""
                        }
                    },
                    onRemoveTag = { tagToRemove -> selectedTags = selectedTags - tagToRemove },
                    onToggleTag = { tagToToggle ->
                        if (selectedTags.contains(tagToToggle)) {
                            selectedTags = selectedTags - tagToToggle
                        } else if (selectedTags.size < 5) {
                            selectedTags = selectedTags + tagToToggle
                        }
                    }
                )
            }

            if (errorMessage != null) {
                item { ErrorMessageCard(errorMessage = errorMessage!!) }
            }

            item {
                SubmitActions(
                    submitting = submitting,
                    onCancel = { navController.popBackStack() },
                    onSubmit = {
                        // Validate and submit
                        if (validateInputs(title, content, selectedCategoryId, selectedTags) { errMsg -> errorMessage = errMsg }) {
                            errorMessage = null
                            submitting = true
                            val categoryName = (categoriesState as? UiState.Success<List<ForumCategory>>)?.data?.find { it.id == selectedCategoryId }?.name
                            viewModel.createQuestion(
                                CreateQuestionRequest(
                                    title = title.trim(),
                                    content = content.trim(),
                                    userId = currentUserId ?: 0,
                                    userName = currentUserName ?: "User",
                                    tags = selectedTags,
                                    categoryId = selectedCategoryId,
                                    categoryName = categoryName
                                ),
                                onSuccess = {
                                    submitting = false
                                    navController.popBackStack()
                                },
                                onError = {
                                    submitting = false
                                    errorMessage = it
                                }
                            )
                        }
                    }
                )
            }

            item { AskScreenTipsCard() }
        }
    }
}

private fun validateInputs(
    title: String,
    content: String,
    categoryId: String?,
    tags: List<String>,
    onError: (String) -> Unit
): Boolean {
    if (title.trim().length < 10) {
        onError("Tiêu đề phải có ít nhất 10 ký tự")
        return false
    }
    if (content.trim().replace(Regex("<[^>]*>"), "").length < 20) {
        onError("Mô tả phải có ít nhất 20 ký tự")
        return false
    }
    if (categoryId == null) {
        onError("Vui lòng chọn danh mục")
        return false
    }
    if (tags.isEmpty()) {
        onError("Vui lòng thêm ít nhất 1 tag")
        return false
    }
    return true
}

@Composable
private fun ErrorMessageCard(errorMessage: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            errorMessage,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun SubmitActions(submitting: Boolean, onCancel: () -> Unit, onSubmit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
            Text("Hủy")
        }
        Button(
            onClick = onSubmit,
            modifier = Modifier.weight(1f),
            enabled = !submitting
        ) {
            if (submitting) {
                CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Text("Đăng câu hỏi")
            }
        }
    }
}


@Composable
fun AskScreenHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Đặt câu hỏi",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Chia sẻ thắc mắc của bạn với cộng đồng",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AskScreenTipsCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Mẹo để có câu trả lời tốt", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            TipsItem("Tìm kiếm trước khi đặt câu hỏi để tránh trùng lặp")
            TipsItem("Viết tiêu đề rõ ràng, cụ thể về vấn đề")
            TipsItem("Mô tả chi tiết những gì bạn đã thử")
            TipsItem("Sử dụng tags phù hợp để dễ tìm kiếm")
            TipsItem("Đính kèm hình ảnh nếu cần thiết")
        }
    }
}

@Composable
private fun TipsItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.CheckCircle, null, Modifier.size(16.dp), tint = Color(0xFF10B981))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TagsSelectionCard(
    tagsState: UiState<List<ForumTag>>,
    selectedTags: List<String>,
    currentTag: String,
    onCurrentTagChange: (String) -> Unit,
    onAddTag: () -> Unit,
    onRemoveTag: (String) -> Unit,
    onToggleTag: (String) -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Tags * (Tối đa 5)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

            if (selectedTags.isNotEmpty()) {
                SelectedTagsRow(selectedTags = selectedTags, onRemoveTag = onRemoveTag)
            }

            TagInputField(
                currentTag = currentTag,
                onCurrentTagChange = onCurrentTagChange,
                onAddTag = onAddTag,
                isEnabled = selectedTags.size < 5
            )

            PopularTagsRow(
                tagsState = tagsState,
                selectedTags = selectedTags,
                onToggleTag = onToggleTag
            )
        }
    }
}

@Composable
private fun SelectedTagsRow(selectedTags: List<String>, onRemoveTag: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(selectedTags) { tag ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(tag, fontSize = 12.sp)
                    Spacer(Modifier.width(6.dp))
                    IconButton(
                        onClick = { onRemoveTag(tag) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.Close, null, Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TagInputField(
    currentTag: String,
    onCurrentTagChange: (String) -> Unit,
    onAddTag: () -> Unit,
    isEnabled: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = currentTag,
            onValueChange = onCurrentTagChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Nhập tag và nhấn Thêm") },
            enabled = isEnabled,
            singleLine = true
        )
        IconButton(
            onClick = onAddTag,
            enabled = currentTag.isNotBlank() && isEnabled
        ) {
            Icon(Icons.Default.Add, "Thêm tag")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PopularTagsRow(
    tagsState: UiState<List<ForumTag>>,
    selectedTags: List<String>,
    onToggleTag: (String) -> Unit
) {
    if (tagsState is UiState.Success) {
        Text(
            "Tags phổ biến:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(tagsState.data.take(15)) { tag ->
                val isSelected = selectedTags.contains(tag.name)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleTag(tag.name) },
                    enabled = selectedTags.size < 5 || isSelected,
                    label = { Text(tag.name, fontSize = 11.sp) }
                )
            }
        }
    }
}

@Composable
fun TitleInputCard(title: String, onTitleChange: (String) -> Unit) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Tiêu đề câu hỏi *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ví dụ: Cách tính đạo hàm...") },
                singleLine = true
            )
            Text(
                "Tiêu đề ngắn gọn, rõ ràng và mô tả chính xác vấn đề",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategorySelectionCard(
    categoriesState: UiState<List<ForumCategory>>,
    selectedCategoryId: String?,
    onCategorySelected: (String) -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Danh mục *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            when (categoriesState) {
                is UiState.Success -> {
                    categoriesState.data.forEach { category ->
                        CategoryItem(
                            category = category,
                            isSelected = selectedCategoryId == category.id,
                            onClick = { onCategorySelected(category.id) }
                        )
                    }
                }
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UiState.Error -> {
                    Text(
                        "Lỗi tải danh mục: ${categoriesState.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(category: ForumCategory, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (category.color != null) { // Logic parse color sẽ cần thêm sau
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color = Color.Cyan, RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(8.dp))
            }
            Column {
                Text(category.name, fontWeight = FontWeight.Medium)
                category.description?.let {
                    Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        RadioButton(selected = isSelected, onClick = onClick)
    }
}

@Composable
fun ContentInputCard(content: String, onContentChange: (String) -> Unit) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Mô tả chi tiết *", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text("Mô tả chi tiết câu hỏi của bạn...") },
                maxLines = 10
            )
            Text(
                "Mô tả càng chi tiết, bạn càng có khả năng nhận được câu trả lời chính xác",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
