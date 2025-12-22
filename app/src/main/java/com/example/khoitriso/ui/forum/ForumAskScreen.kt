package com.example.khoitriso.ui.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khoitriso.ui.theme.EdPrimary
import com.example.khoitriso.ui.theme.EdSecondary
import com.example.khoitriso.ui.theme.EdSuccess
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.ForumCategory
import com.example.khoitriso.domain.models.ForumTag
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.request.CreateQuestionRequest
import com.example.khoitriso.ui.common.DetailHeader
import com.example.khoitriso.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumAskScreen(
    navController: NavController,
    viewModel: ForumAskViewModel = hiltViewModel(),
) {
    val categoriesState by viewModel.categories.collectAsState()
    val tagsState by viewModel.tags.collectAsState()
    val currentUserState by viewModel.currentUser.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentTag by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            DetailHeader(
                onBack = { navController.popBackStack() },
                title = stringResource(R.string.ask_question_title)
            )
        }
    ) { paddingValues ->
        when (val state = currentUserState) {
            is UiState.Success<User> -> {
                val currentUser = state.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item { AskScreenHeader() }

                    item { 
                        TitleInputCard(
                            title = title, 
                            onTitleChange = { title = it }
                        ) 
                    }

                    item {
                        CategorySelectionCard(
                            categoriesState = categoriesState,
                            selectedCategoryId = selectedCategoryId,
                            onCategorySelected = { selectedCategoryId = it }
                        )
                    }

                    item { 
                        ContentInputCard(
                            content = content, 
                            onContentChange = { content = it }
                        ) 
                    }

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
                            onRemoveTag = { tagToRemove -> 
                                selectedTags = selectedTags - tagToRemove 
                            },
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
                        item { 
                            ErrorMessageCard(errorMessage = errorMessage!!) 
                        }
                    }

                    item {
                        val titleErrorMsg = stringResource(R.string.validation_title_too_short_msg)
                        val contentErrorMsg = stringResource(R.string.validation_content_too_short_msg)
                        val categoryErrorMsg = stringResource(R.string.validation_category_required_msg)
                        val tagsErrorMsg = stringResource(R.string.validation_tags_required_msg)
                        
                        SubmitActions(
                            submitting = submitting,
                            onCancel = { navController.popBackStack() },
                            onSubmit = {
                                val validationError = validateInputs(
                                    title, 
                                    content, 
                                    selectedCategoryId, 
                                    selectedTags,
                                    titleErrorMsg,
                                    contentErrorMsg,
                                    categoryErrorMsg,
                                    tagsErrorMsg
                                )
                                if (validationError == null) {
                                    errorMessage = null
                                    submitting = true
                                    val categoryName = (categoriesState as? UiState.Success<List<ForumCategory>>)
                                        ?.data?.find { it.id == selectedCategoryId }?.name
                                        
                                    viewModel.createQuestion(
                                        CreateQuestionRequest(
                                            title = title.trim(),
                                            content = content.trim(),
                                            userId = currentUser.id,
                                            userName = currentUser.fullName,
                                            userAvatar = currentUser.avatar,
                                            tags = selectedTags,
                                            categoryId = selectedCategoryId,
                                            categoryName = categoryName
                                        ),
                                        onSuccess = {
                                            submitting = false
                                            navController.popBackStack()
                                        },
                                        onError = { error ->
                                            submitting = false
                                            errorMessage = error
                                            scope.launch {
                                                snackbarHostState.showSnackbar(error)
                                            }
                                        }
                                    )
                                } else {
                                    errorMessage = validationError
                                    scope.launch {
                                        snackbarHostState.showSnackbar(validationError)
                                    }
                                }
                            }
                        )
                    }

                    item { AskScreenTipsCard() }
                }
            }
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
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { navController.popBackStack() }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            }
        }
    }
}

private fun validateInputs(
    title: String,
    content: String,
    categoryId: String?,
    tags: List<String>,
    titleErrorMsg: String,
    contentErrorMsg: String,
    categoryErrorMsg: String,
    tagsErrorMsg: String
): String? {
    if (title.trim().length < 10) {
        return titleErrorMsg
    }
    if (content.trim().replace(Regex("<[^>]*>"), "").length < 20) {
        return contentErrorMsg
    }
    if (categoryId == null) {
        return categoryErrorMsg
    }
    if (tags.isEmpty()) {
        return tagsErrorMsg
    }
    return null
}

@Composable
private fun ErrorMessageCard(errorMessage: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SubmitActions(
    submitting: Boolean, 
    onCancel: () -> Unit, 
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onCancel, 
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 2.dp
            )
        ) {
            Text(
                stringResource(R.string.cancel),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            enabled = !submitting,
            colors = ButtonDefaults.buttonColors(
                containerColor = EdPrimary
            )
        ) {
            if (submitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
            } else {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                stringResource(R.string.post_question),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AskScreenHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            EdPrimary.copy(alpha = 0.05f),
                            EdSecondary.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(EdPrimary, EdSecondary)
                            ),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.QuestionAnswer,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
                Text(
                    stringResource(R.string.ask_question_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    stringResource(R.string.ask_question_subtitle),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AskScreenTipsCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = EdPrimary.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(EdPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Info, 
                        null,
                        tint = EdPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.tips_title),
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            TipsItem(stringResource(R.string.tip_search_first))
            TipsItem(stringResource(R.string.tip_clear_title))
            TipsItem(stringResource(R.string.tip_detailed_description))
            TipsItem(stringResource(R.string.tip_use_tags))
            TipsItem(stringResource(R.string.tip_attach_images))
        }
    }
}

@Composable
private fun TipsItem(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            Icons.Default.CheckCircle, 
            null, 
            Modifier.size(18.dp), 
            tint = EdSuccess
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text, 
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
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
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.tags_label),
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (selectedTags.isNotEmpty()) {
                SelectedTagsRow(
                    selectedTags = selectedTags, 
                    onRemoveTag = onRemoveTag
                )
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
private fun SelectedTagsRow(
    selectedTags: List<String>, 
    onRemoveTag: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(selectedTags) { tag ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = EdPrimary.copy(alpha = 0.15f),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        tag,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = EdPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { onRemoveTag(tag) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.Close, 
                            null, 
                            Modifier.size(16.dp)
                        )
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
            placeholder = { Text(stringResource(R.string.tag_input_hint)) },
            enabled = isEnabled,
            singleLine = true
        )
        IconButton(
            onClick = onAddTag,
            enabled = currentTag.isNotBlank() && isEnabled
        ) {
            Icon(
                Icons.Default.Add, 
                stringResource(R.string.add_tag)
            )
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
            stringResource(R.string.popular_tags),
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
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.question_title_label),
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.question_title_hint)) },
                singleLine = true
            )
            Text(
                stringResource(R.string.question_title_helper),
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
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.category_label),
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is UiState.Error -> {
                    Text(
                        stringResource(R.string.error_loading_categories, categoriesState.message),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: ForumCategory, 
    isSelected: Boolean, 
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
        RadioButton(selected = isSelected, onClick = onClick)
    }
}

@Composable
fun ContentInputCard(content: String, onContentChange: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.content_label),
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text(stringResource(R.string.content_hint)) },
                maxLines = 10
            )
            Text(
                stringResource(R.string.content_helper),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
