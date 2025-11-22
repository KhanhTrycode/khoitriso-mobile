package com.example.khoitriso.ui.profilescreen

import androidx.compose.foundation.Image
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.khoitriso.R // Thay bằng R của project bạn

@Composable
fun ProfileScreen(
    lazyListState: LazyListState,
    navController: NavHostController,
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        // -- Phần 1: Thông tin cá nhân --
        item {
            ProfileInfo(
                avatarUrl = R.drawable.course_test, // Thay bằng avatar thật
                fullName = "Nguyen Van A",
                email = "nguyenvana@example.com",
                onEditProfile = { /* TODO: Navigate to edit profile screen */ }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // -- Phần 2: Cài đặt tài khoản --
        item {
            SettingsSection(
                title = "Account Settings",
                items = listOf(
                    SettingsItemData("Account Security", Icons.Default.Star) { /*TODO*/ },
                    SettingsItemData("My Courses", Icons.Default.AccountCircle) { /*TODO*/ },
                    SettingsItemData("App Settings", Icons.Default.Settings) { /*TODO*/ },
                )
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // -- Phần 3: Trợ giúp & Hỗ trợ --
        item {
            SettingsSection(
                title = "Help and Support",
                items = listOf(
                    SettingsItemData("Help Center", Icons.Default.Star) { /*TODO*/ },
                    SettingsItemData("Contact Us", Icons.Default.Star) { /*TODO*/ },
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
    avatarUrl: Int,
    fullName: String,
    email: String,
    onEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = avatarUrl),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = fullName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onEditProfile) {
            Text("Edit Profile")
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
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
            contentDescription = "Sign Out"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Sign Out",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

