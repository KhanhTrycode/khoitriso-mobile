package com.example.khoitriso.ui.profilescreen

import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.ui.behavior.SafeImage
import com.example.khoitriso.utils.NavRoute
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
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var activationCode by remember { mutableStateOf("") }


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
                onAvatarClick = { imagePickerLauncher.launch("image/*") }
            )
        }

        // Error message
        uiState.error?.let { error ->
            item {
                ErrorCard(error)
            }
        }

        // -- Phần 2: Kích hoạt sách --
        item {
            ActivateBookSection(
                code = activationCode,
                onCodeChange = { activationCode = it },
                onActivate = {
                    // TODO: Gọi viewModel.activateBook(activationCode)
                }
            )
        }


        // -- Phần 3: Cài đặt tài khoản --
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
                    SettingsItemData(stringResource(R.string.help_center), Icons.Default.Help) { /*TODO*/ },
                    SettingsItemData(stringResource(R.string.contact_us), Icons.Default.ContactSupport) { /*TODO*/ },
                )
            )
        }

        // -- Phần 5: Đăng xuất --
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SignOutButton {
                // TODO: Xử lý logic đăng xuất
            }
        }
    }
}

// --- COMPOSABLES CON ---

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
                    Button(onClick = onEditProfile, enabled = !isLoading) {
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
                        singleLine = true
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
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
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

@Composable
private fun ActivateBookSection(code: String, onCodeChange: (String) -> Unit, onActivate: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Kích hoạt sách",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = code,
                onValueChange = onCodeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nhập mã kích hoạt") },
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
                singleLine = true
            )
            Button(
                onClick = onActivate,
                modifier = Modifier.fillMaxWidth(),
                enabled = code.isNotBlank()
            ) {
                Text("Kích hoạt")
            }
        }
    }
}

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
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        OutlinedCard {
            Column {
                items.forEachIndexed { index, item ->
                    SettingsItem(title = item.title, icon = item.icon, onClick = item.onClick)
                    if (index < items.size - 1) {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
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
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SignOutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(50.dp),
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Text(
            text = error,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

