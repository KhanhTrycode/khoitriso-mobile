package com.example.khoitriso.ui.forum

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.repository.*
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumAskScreen(
    navController: NavController,
    viewModel: ForumViewModel = hiltViewModel()
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
    val currentUserAvatar by viewModel.currentUserAvatar.collectAsState()
    
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Help,
                        null,
                        Modifier.size(48.dp),
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
            
            // Title Input
            item {
                Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Tiêu đề câu hỏi *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
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
            
            // Category Selection
            item {
                Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Danh mục *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        when (categoriesState) {
                            is UiState.Success -> {
                                categoriesState.data.forEach { category ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedCategoryId = category.id }
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (category.color != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            android.graphics.Color.parseColor(category.color),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                )
                                                Spacer(Modifier.width(8.dp))
                                            }
                                            Column {
                                                Text(category.name, fontWeight = FontWeight.Medium)
                                                category.description?.let {
                                                    Text(
                                                        it,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                        RadioButton(
                                            selected = selectedCategoryId == category.id,
                                            onClick = { selectedCategoryId = category.id }
                                        )
                                    }
                                }
                            }
                            is UiState.Loading -> {
                                CircularProgressIndicator()
                            }
                            else -> {}
                        }
                    }
                }
            }
            
            // Content Input
            item {
                Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Mô tả chi tiết *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
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
            
            // Tags Selection
            item {
                Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Tags * (Tối đa 5)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        // Selected Tags
                        if (selectedTags.isNotEmpty()) {
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
                                                onClick = { selectedTags = selectedTags - tag },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(Icons.Default.Close, null, Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Tag Input
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = currentTag,
                                onValueChange = { currentTag = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Nhập tag và nhấn Enter") },
                                enabled = selectedTags.size < 5,
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    if (currentTag.isNotBlank() && selectedTags.size < 5 && !selectedTags.contains(currentTag)) {
                                        selectedTags = selectedTags + currentTag.trim()
                                        currentTag = ""
                                    }
                                },
                                enabled = currentTag.isNotBlank() && selectedTags.size < 5
                            ) {
                                Icon(Icons.Default.Add, null)
                            }
                        }
                        
                        // Popular Tags
                        when (tagsState) {
                            is UiState.Success -> {
                                Text(
                                    "Tags phổ biến:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(tagsState.data.take(15)) { tag ->
                                        FilterChip(
                                            selected = selectedTags.contains(tag.name),
                                            onClick = {
                                                if (selectedTags.contains(tag.name)) {
                                                    selectedTags = selectedTags - tag.name
                                                } else if (selectedTags.size < 5) {
                                                    selectedTags = selectedTags + tag.name
                                                }
                                            },
                                            enabled = selectedTags.size < 5 || selectedTags.contains(tag.name),
                                            label = { Text(tag.name, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
            
            // Error Message
            if (errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            errorMessage ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Submit Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hủy")
                    }
                    Button(
                        onClick = {
                            // Validate
                            if (title.trim().length < 10) {
                                errorMessage = "Tiêu đề phải có ít nhất 10 ký tự"
                                return@Button
                            }
                            if (content.trim().replace(Regex("<[^>]*>"), "").length < 20) {
                                errorMessage = "Mô tả phải có ít nhất 20 ký tự"
                                return@Button
                            }
                            if (selectedCategoryId == null) {
                                errorMessage = "Vui lòng chọn danh mục"
                                return@Button
                            }
                            if (selectedTags.isEmpty()) {
                                errorMessage = "Vui lòng thêm ít nhất 1 tag"
                                return@Button
                            }
                            
                            errorMessage = null
                            submitting = true
                            
                            // Get category name
                            val categoryName = when (categoriesState) {
                                is UiState.Success -> {
                                    categoriesState.data.find { it.id == selectedCategoryId }?.name
                                }
                                else -> null
                            }
                            
                            // Submit
                            val coroutineScope = rememberCoroutineScope()
                            coroutineScope.launch {
                                viewModel.createQuestion(
                                    CreateQuestionRequest(
                                        title = title.trim(),
                                        content = content.trim(),
                                        userId = currentUserId ?: 0,
                                        userName = currentUserName ?: "User",
                                        userAvatar = null,
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
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !submitting
                    ) {
                        if (submitting) {
                            CircularProgressIndicator(Modifier.size(16.dp))
                        } else {
                            Text("Đăng câu hỏi")
                        }
                    }
                }
            }
            
            // Tips Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Mẹo để có câu trả lời tốt",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TipsItem("Tìm kiếm trước khi đặt câu hỏi để tránh trùng lặp")
                        TipsItem("Viết tiêu đề rõ ràng, cụ thể về vấn đề")
                        TipsItem("Mô tả chi tiết những gì bạn đã thử")
                        TipsItem("Sử dụng tags phù hợp để dễ tìm kiếm")
                        TipsItem("Đính kèm hình ảnh nếu cần thiết")
                    }
                }
            }
        }
    }
}

@Composable
fun TipsItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            Icons.Default.CheckCircle,
            null,
            Modifier.size(16.dp),
            tint = Color(0xFF10B981)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}

