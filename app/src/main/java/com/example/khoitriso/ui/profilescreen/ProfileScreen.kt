package com.example.khoitriso.ui.profilescreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.ContactSupport
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
import com.example.khoitriso.utils.NavRoute
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun ProfileScreen(
    lazyListState: LazyListState,
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Image picker launcher
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

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
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
                onFullNameChange = { viewModel.updateFullName(it) },
                onEmailChange = { viewModel.updateEmail(it) },
                onEditProfile = { viewModel.setEditMode(true) },
                onSave = { viewModel.updateProfile(uiState.fullName, uiState.email) },
                onCancel = { 
                    viewModel.setEditMode(false)
                    uiState.user?.let { user ->
                        viewModel.updateFullName(user.fullName)
                        viewModel.updateEmail(user.email)
                    }
                },
                onAvatarClick = { imagePickerLauncher.launch("image/*") }
            )
        }

        // Error message
        uiState.error?.let { error ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // -- Phần 2: Cài đặt tài khoản --
        item {
            SettingsSection(
                title = stringResource(R.string.account_settings),
                items = listOf(
                    SettingsItemData(
                        stringResource(R.string.my_courses),
                        Icons.Default.School
                    ) {
                        navController.navigate(NavRoute.NavMyPurchases("courses"))
                    },
                    SettingsItemData(
                        stringResource(R.string.my_books),
                        Icons.Default.MenuBook
                    ) {
                        navController.navigate(NavRoute.NavMyPurchases("books"))
                    },
                )
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // -- Phần 3: Trợ giúp & Hỗ trợ --
        item {
            SettingsSection(
                title = stringResource(R.string.help_and_support),
                items = listOf(
                    SettingsItemData(
                        stringResource(R.string.help_center),
                        Icons.Default.Help
                    ) { /*TODO*/ },
                    SettingsItemData(
                        stringResource(R.string.contact_us),
                        Icons.Default.ContactSupport
                    ) { /*TODO*/ },
                )
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // -- Phần 4: Đăng xuất --
        item {
            SignOutButton {
                // TODO: Xử lý logic đăng xuất
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// Composable cho phần thông tin cá nhân
@Composable
private fun ProfileInfo(
    user: com.example.khoitriso.domain.models.User?,
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
    onAvatarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar với click để upload
        Box {
            if (user?.avatar?.isNotEmpty() == true) {
                AsyncImage(
                    model = user.avatar,
                    contentDescription = stringResource(R.string.user_avatar),
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isUploading, onClick = onAvatarClick),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.user_avatar),
                    modifier = Modifier
                        .size(100.dp)
                        .clickable(enabled = !isUploading, onClick = onAvatarClick),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                )
            }
        }

        if (isUploading) {
            Text(
                text = stringResource(R.string.uploading_avatar),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            TextButton(onClick = onAvatarClick) {
                Text(stringResource(R.string.change_avatar))
            }
        }

        if (!editMode) {
            // View mode
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
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onEditProfile) {
                Text(stringResource(R.string.edit_profile))
            }
        } else {
            // Edit mode
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

// Data class để lưu thông tin cho mỗi mục cài đặt
private data class SettingsItemData(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// Composable cho một nhóm các mục cài đặt
@Composable
private fun SettingsSection(title: String, items: List<SettingsItemData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            items.forEachIndexed { index, item ->
                SettingsItem(
                    title = item.title,
                    icon = item.icon,
                    onClick = item.onClick
                )
                if (index < items.size - 1) {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

// Composable cho một mục cài đặt (một hàng)
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
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Composable cho nút đăng xuất
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
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = stringResource(R.string.sign_out)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.sign_out),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

