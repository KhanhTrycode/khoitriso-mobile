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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.khoitriso.ui.behavior.SafeImage
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

        // -- Phần 2: Quản lý & Tiện ích (MỚI) --
        item {
            SettingsSection(
                title = "Quản lý & Tiện ích",
                items = listOf(
                    // Item 1: Mở Dialog kích hoạt
                    SettingsItemData("Kích hoạt sách", Icons.Default.VpnKey) {
                        showActivationDialog = true
                    },
                    // Item 2: Xem lịch sử đơn hàng
                    SettingsItemData("Lịch sử đơn hàng", Icons.Default.History) {
                        navController.navigate(NavRoute.ORDER_HISTORY)
                    }
                )
            )
        }

        // -- Phần 3: Thư viện của tôi --
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

        // -- Phần 4: Trợ giúp & Hỗ trợ --
        item {
            SettingsSection(
                title = stringResource(R.string.help_and_support),
                items = listOf(
                    SettingsItemData(stringResource(R.string.help_center), Icons.Default.Help) { },
                    SettingsItemData(stringResource(R.string.contact_us), Icons.Default.ContactSupport) { },
                )
            )
        }

        // -- Phần 5: Đăng xuất --
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
    val isLoading = uiState is UiState.Loading
    val errorMessage = (uiState as? UiState.Error)?.message

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
        title = { Text("Kích hoạt sách") },
        text = {
            Column {
                Text(
                    text = "Nhập mã kích hoạt được gửi trong hóa đơn mua hàng để thêm sách vào thư viện:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Mã code") },
                    placeholder = { Text("VD: BOOK-XXXX-XXXX") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    enabled = !isLoading
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
                    Text("Kích hoạt")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Hủy")
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
                        supportingText = { Text("Email không thể thay đổi") }
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