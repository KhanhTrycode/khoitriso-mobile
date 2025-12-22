package com.example.khoitriso.ui.profilescreen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import com.example.khoitriso.data.local.AppLanguage
import com.example.khoitriso.data.local.AppTheme
import com.example.khoitriso.data.local.LanguageManager
import com.example.khoitriso.data.local.ThemeManager
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.ui.common.ObserverAsEvent
import com.example.khoitriso.ui.common.SafeImage
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun ProfileScreen(
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState() // State thông tin user
    val activationState by viewModel.activationState.collectAsState() // State kích hoạt sách

    val context = LocalContext.current

    // State quản lý hiển thị Dialog kích hoạt
    var showActivationDialog by remember { mutableStateOf(false) }

    // Xử lý chọn ảnh Avatar
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val file = File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
            inputStream?.use { stream ->
                file.outputStream().use { out ->
                    stream.copyTo(out)
                }
            }
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("File", file.name, requestFile)
            viewModel.uploadAvatar(multipartBody)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
    }

    // Observe navigation events for token expiration
    ObserverAsEvent(viewModel.navigationEvents) { event ->
        when (event) {
            is com.example.khoitriso.ui.behavior.NavigationEvent.NavigateToLogin -> {
                navController.navigate(com.example.khoitriso.utils.NavRoute.LOGIN) {
                    popUpTo(0) { inclusive = true } // Clear entire back stack
                }
            }
        }
    }

    // Lắng nghe kết quả kích hoạt sách
    LaunchedEffect(activationState) {
        when (val state = activationState) {
            is UiState.Success -> {
                showActivationDialog = false
                Toast.makeText(context, state.data, Toast.LENGTH_LONG).show()
                viewModel.resetActivationState()
                // TODO: Có thể reload lại danh sách sách của tôi ở đây nếu cần
            }
            is UiState.Error -> {
                // Lỗi sẽ hiển thị trực tiếp trên Dialog, không cần Toast
            }
            else -> {}
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -- Phần 1: Thông tin cá nhân --
        item {
            ProfileInfo(
                user = uiState.user,
                fullName = uiState.fullName,
                email = uiState.email,
                editMode = uiState.editMode,
                isLoading = uiState.isLoading,
                isUploading = uiState.isUploading,
                onFullNameChange = viewModel::updateFullName,
                onEmailChange = viewModel::updateEmail,
                onEditProfile = { viewModel.setEditMode(true) },
                onSave = { viewModel.updateProfile(uiState.fullName, uiState.email) },
                onCancel = {
                    viewModel.setEditMode(false)
                    uiState.user?.let { user ->
                        viewModel.updateFullName(user.fullName)
                        viewModel.updateEmail(user.email)
                    }
                },

            )
        }

        uiState.error?.let { error ->
            item { ErrorCard(error) }
        }

        // -- Phần 2: Cài đặt --
        item {
            var showLanguageDialog by remember { mutableStateOf(false) }
            var showThemeDialog by remember { mutableStateOf(false) }
            
            SettingsSection(
                title = stringResource(R.string.account_settings),
                items = listOf(
                    SettingsItemData(
                        title = stringResource(R.string.language),
                        icon = Icons.Default.Language
                    ) {
                        showLanguageDialog = true
                    },
                    SettingsItemData(
                        title = stringResource(R.string.theme),
                        icon = Icons.Default.Palette
                    ) {
                        showThemeDialog = true
                    }
                )
            )
            
            if (showLanguageDialog) {
                LanguageDialog(
                    onDismiss = { showLanguageDialog = false },
                    viewModel = viewModel
                )
            }
            
            if (showThemeDialog) {
                ThemeDialog(
                    onDismiss = { showThemeDialog = false },
                    viewModel = viewModel
                )
            }
        }

        // -- Phần 3: Quản lý & Tiện ích --
        item {
            var showWishlistDialog by remember { mutableStateOf(false) }
            var showQuestionLookupDialog by remember { mutableStateOf(false) }
            
            SettingsSection(
                title = stringResource(R.string.management_utilities),
                items = listOf(
                    SettingsItemData(stringResource(R.string.activate_book), Icons.Default.VpnKey) {
                        showActivationDialog = true
                    },
                    SettingsItemData("Tra cứu câu hỏi sách", Icons.Default.Search) {
                        showQuestionLookupDialog = true
                    },
                    SettingsItemData(stringResource(R.string.order_history), Icons.Default.History) {
                        navController.navigate(NavRoute.ORDER_HISTORY)
                    },
                    SettingsItemData(stringResource(R.string.wishlist), Icons.Default.Favorite) {
                        showWishlistDialog = true
                        viewModel.loadWishlist()
                    }
                )
            )
            
            if (showWishlistDialog) {
                WishlistDialog(
                    onDismiss = { showWishlistDialog = false },
                    viewModel = viewModel
                )
            }
            
            if (showQuestionLookupDialog) {
                QuestionLookupDialog(
                    onDismiss = { 
                        showQuestionLookupDialog = false
                        viewModel.resetQuestionLookup()
                    },
                    viewModel = viewModel
                )
            }
        }

        // -- Phần 4: Thư viện của tôi --
        item {
            SettingsSection(
                title = stringResource(R.string.account_settings),
                items = listOf(
                    SettingsItemData(stringResource(R.string.my_courses), Icons.Default.School) {
                        navController.navigate(NavRoute.NavMyPurchases("courses"))
                    },
                    SettingsItemData(stringResource(R.string.my_books), Icons.Default.MenuBook) {
                        navController.navigate(NavRoute.NavMyPurchases("books"))
                    },
                )
            )
        }

        // -- Phần 5: Trợ giúp & Hỗ trợ --
        item {
            SettingsSection(
                title = stringResource(R.string.help_and_support),
                items = listOf(
                    SettingsItemData(stringResource(R.string.help_center), Icons.Default.Help) { },
                    SettingsItemData(stringResource(R.string.contact_us), Icons.Default.ContactSupport) { },
                )
            )
        }

        // -- Phần 6: Đăng xuất --
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SignOutButton {
                // TODO: viewModel.signOut()
                navController.navigate(NavRoute.LOGIN) {
                    popUpTo(0) // Xóa backstack
                }
            }
        }
    }

    // --- HIỂN THỊ DIALOG KÍCH HOẠT ---
    if (showActivationDialog) {
        // Reset state when dialog opens
        LaunchedEffect(showActivationDialog) {
            if (showActivationDialog) {
                viewModel.resetActivationState()
            }
        }
        ActivationDialog(
            onDismiss = {
                showActivationDialog = false
                viewModel.resetActivationState()
            },
            onActivate = { code -> viewModel.activateBook(code) },
            uiState = activationState
        )
    }
}

// --- COMPOSABLES CON ---

@Composable
fun ActivationDialog(
    onDismiss: () -> Unit,
    onActivate: (String) -> Unit,
    uiState: UiState<String>
) {
    var code by remember { mutableStateOf("") }
    // Only show loading if state is explicitly Loading, not Success("")
    val isLoading = uiState is UiState.Loading
    val errorMessage = (uiState as? UiState.Error)?.message
    val isSuccess = uiState is UiState.Success && (uiState as UiState.Success).data.isNotEmpty()

    // Reset code when dialog opens
    LaunchedEffect(Unit) {
        code = ""
    }
    
    // Reset code after successful activation
    LaunchedEffect(uiState) {
        if (isSuccess) {
            code = ""
        }
    }

    AlertDialog(
        onDismissRequest = {
            code = ""
            onDismiss()
        },
        icon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
        title = { Text(stringResource(R.string.activate_book_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.activate_book_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { if (!isLoading) code = it },
                    label = { Text(stringResource(R.string.activation_code)) },
                    placeholder = { Text(stringResource(R.string.activation_code_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    enabled = !isLoading,
                    readOnly = isLoading
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onActivate(code) },
                enabled = !isLoading && code.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.activate))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ProfileInfo(
    user: User?,
    fullName: String,
    email: String,
    editMode: Boolean,
    isLoading: Boolean,
    isUploading: Boolean,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onEditProfile: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onAvatarClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box {
            SafeImage(
                url = user?.avatar,
                contentDescription = stringResource(R.string.user_avatar),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(enabled = !isUploading, onClick = onAvatarClick),
                contentScale = ContentScale.Crop
            )
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            // Icon edit nhỏ ở góc avatar để user biết có thể đổi ảnh
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(6.dp)
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        AnimatedContent(targetState = editMode, label = "ProfileEditModeTransition") { isEditing ->
            if (!isEditing) {
                // View mode
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = fullName.ifEmpty { stringResource(R.string.loading) },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = email.ifEmpty { stringResource(R.string.loading) },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FilledTonalButton(onClick = onEditProfile, enabled = !isLoading) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.edit_profile))
                    }
                }
            } else {
                // Edit mode
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = onFullNameChange,
                        label = { Text(stringResource(R.string.full_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text(stringResource(R.string.email)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false, // Không cho sửa email
                        singleLine = true,
                        supportingText = { Text(stringResource(R.string.email_cannot_change)) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f), enabled = !isLoading) {
                            Text(stringResource(R.string.cancel))
                        }
                        Button(onClick = onSave, modifier = Modifier.weight(1f), enabled = !isLoading) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text(stringResource(R.string.save))
                            }
                        }
                    }
                }
            }
        }
    }
}

// Data class cho item settings
private data class SettingsItemData(val title: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
private fun SettingsSection(title: String, items: List<SettingsItemData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        // Sử dụng Card để gom nhóm các item
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingsItem(title = item.title, icon = item.icon, onClick = item.onClick)
                    if (index < items.size - 1) {
                        Divider(
                            modifier = Modifier.padding(start = 56.dp, end = 16.dp), // Indent divider
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SignOutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.sign_out))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.sign_out), fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun ErrorCard(error: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun LanguageDialog(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val currentLanguage by remember { mutableStateOf(viewModel.getCurrentLanguage()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Language, contentDescription = null) },
        title = { Text(stringResource(R.string.language)) },
        text = {
            Column {
                AppLanguage.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setLanguage(language)
                                // Recreate activity để apply language ngay lập tức
                                if (context is android.app.Activity) {
                                    context.recreate()
                                }
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == language,
                            onClick = {
                                viewModel.setLanguage(language)
                                // Recreate activity để apply language ngay lập tức
                                if (context is android.app.Activity) {
                                    context.recreate()
                                }
                                onDismiss()
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (language) {
                                AppLanguage.VIETNAMESE -> stringResource(R.string.vietnamese)
                                AppLanguage.ENGLISH -> stringResource(R.string.english)
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ThemeDialog(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel
) {
    val currentTheme by remember { mutableStateOf(viewModel.getCurrentTheme()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Palette, contentDescription = null) },
        title = { Text(stringResource(R.string.theme)) },
        text = {
            Column {
                AppTheme.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setTheme(theme)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentTheme == theme,
                            onClick = {
                                viewModel.setTheme(theme)
                                onDismiss()
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (theme) {
                                AppTheme.LIGHT -> stringResource(R.string.light_theme)
                                AppTheme.DARK -> stringResource(R.string.dark_theme)
                                AppTheme.SYSTEM -> stringResource(R.string.system_theme)
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun WishlistDialog(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel
) {
    val wishlistState by viewModel.wishlist.collectAsState()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
        title = { Text(stringResource(R.string.wishlist)) },
        text = {
            when (val state = wishlistState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is UiState.Success -> {
                    val items = state.data
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stringResource(R.string.wishlist_empty),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(items) {index, item ->
                                WishlistItemRow(
                                    item = item,
                                    onRemove = {
                                        viewModel.removeFromWishlist(item.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun QuestionLookupDialog(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel
) {
    var questionId by remember { mutableStateOf("") }
    val lookupState by viewModel.questionLookup.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Tra cứu câu hỏi sách")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = questionId,
                    onValueChange = { questionId = it },
                    label = { Text("ID câu hỏi") },
                    placeholder = { Text("Nhập ID câu hỏi") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    enabled = lookupState !is UiState.Loading
                )

                Button(
                    onClick = { viewModel.lookupQuestion(questionId) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = questionId.isNotBlank() && lookupState !is UiState.Loading
                ) {
                    if (lookupState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (lookupState is UiState.Loading) "Đang tra cứu..." else "Tra cứu")
                }

                // Display result
                when (val state = lookupState) {
                    is UiState.Success -> {
                        state.data?.let { question ->
                            QuestionDetailCard(question)
                        }
                    }
                    is UiState.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
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
                                    text = state.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
private fun QuestionDetailCard(question: com.example.khoitriso.domain.models.BookQuestion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Question header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Câu hỏi #${question.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                question.chapterTitle?.let { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider()

            // Question content
            Text(
                text = question.questionContent,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Options
            if (question.options.isNotEmpty()) {
                Text(
                    text = "Đáp án:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                question.options.sortedBy { it.orderIndex }.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            if (option.isCorrect) Icons.Default.CheckCircle else Icons.Default.Circle,
                            contentDescription = null,
                            tint = if (option.isCorrect) 
                                MaterialTheme.colorScheme.tertiary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = option.optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = if (option.isCorrect) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Explanation
            question.explanationContent?.let { explanation ->
                Divider()
                Text(
                    text = "Giải thích:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Video URL
            question.videoUrl?.let { url ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Video hướng dẫn",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun WishlistItemRow(
    item: com.example.khoitriso.domain.models.WishlistItem,
    onRemove: () -> Unit
) {
    val itemTitle = when {
        item.item is com.example.khoitriso.domain.models.Book -> {
            (item.item as com.example.khoitriso.domain.models.Book).title
        }
        item.item is com.example.khoitriso.domain.models.Course -> {
            (item.item as com.example.khoitriso.domain.models.Course).title
        }
        else -> "Unknown Item"
    }

    val itemTypeName = when (item.itemType) {
        com.example.khoitriso.utils.ItemType.Book -> stringResource(R.string.book)
        com.example.khoitriso.utils.ItemType.Course -> stringResource(R.string.course)
        else -> "Unknown"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hiển thị loại item (Book hoặc Course)
            Text(
                text = itemTypeName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )

            // Title
            Text(
                text = itemTitle,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            // Icon trái tim đỏ - luôn hiển thị vì đã trong wishlist
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.remove_from_wishlist),
                    tint = Color(0xFFFF6B6B) // Màu đỏ cho trái tim
                )
            }
        }
    }
}