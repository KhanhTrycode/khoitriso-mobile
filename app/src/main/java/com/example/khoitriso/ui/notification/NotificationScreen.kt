package com.example.khoitriso.ui.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Notification
import com.example.khoitriso.utils.UiState
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isUnreadOnly by viewModel.filterUnreadOnly.collectAsState()
    val selectedType by viewModel.filterType.collectAsState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Thông báo",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    actions = {
                        IconButton(onClick = { viewModel.markAllAsRead() }) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Đọc tất cả")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Thanh tìm kiếm & Filter
                NotificationFilterBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    isUnreadOnly = isUnreadOnly,
                    onToggleUnread = viewModel::toggleUnreadFilter,
                    selectedType = selectedType,
                    onSelectType = viewModel::setTypeFilter
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Có lỗi xảy ra", color = MaterialTheme.colorScheme.error)
                    }
                }
                is UiState.Success -> {
                    val notifications = state.data
                    if (notifications.isEmpty()) {
                        EmptyNotificationState()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(items = notifications, key = { it.id }) { notification ->
                                NotificationItem(
                                    notification = notification,
                                    onClick = {
                                        viewModel.markAsRead(notification.id)
                                        // Handle navigation logic here based on notification.actionUrl
                                    }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 72.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isUnreadOnly: Boolean,
    onToggleUnread: () -> Unit,
    selectedType: Int?,
    onSelectType: (Int?) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {


        Spacer(modifier = Modifier.height(12.dp))

        // Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = !isUnreadOnly && selectedType == null,
                    onClick = {
                        if (isUnreadOnly) onToggleUnread()
                        onSelectType(null)
                    },
                    label = { Text("Tất cả") }
                )
            }
            item {
                FilterChip(
                    selected = isUnreadOnly,
                    onClick = onToggleUnread,
                    label = { Text("Chưa đọc") },
                    leadingIcon = if (isUnreadOnly) {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
            // Mock Types: 1=System, 2=Order, 5=Payment (Ví dụ)
            item {
                FilterChip(
                    selected = selectedType == 2,
                    onClick = { onSelectType(if (selectedType == 2) null else 2) },
                    label = { Text("Đơn hàng") }
                )
            }
            item {
                FilterChip(
                    selected = selectedType == 1,
                    onClick = { onSelectType(if (selectedType == 1) null else 1) },
                    label = { Text("Hệ thống") }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    // Background thay đổi dựa trên trạng thái đọc
    val backgroundColor = if (notification.isRead)
        MaterialTheme.colorScheme.surface
    else
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 1. Icon (Dựa trên Type)
        val iconConfig = getNotificationIcon(0)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconConfig.second.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconConfig.first,
                contentDescription = null,
                tint = iconConfig.second,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 2. Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Thời gian
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatRelativeTime(notification.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!notification.content.isNullOrEmpty()) {
                Text(
                    text = notification.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 3. Unread Indicator (Chấm đỏ)
        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun EmptyNotificationState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Không có thông báo nào",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Bạn sẽ thấy thông báo mới tại đây",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// --- Helpers ---

// Helper để chọn icon và màu dựa trên type (Giả định từ MockData)
@Composable
fun getNotificationIcon(type: Int): Pair<ImageVector, Color> {
    return when (type) {
        1 -> Icons.Default.SystemUpdate to Color(0xFF2196F3) // System
        2 -> Icons.Default.ShoppingCart to Color(0xFF4CAF50) // Order
        4 -> Icons.Default.Assignment to Color(0xFFFF9800)   // Assignment
        5 -> Icons.Default.Payment to Color(0xFF9C27B0)      // Payment
        else -> Icons.Default.Notifications to MaterialTheme.colorScheme.primary // Default
    }
}

// Format thời gian đơn giản (Giả sử string là ISO8601)
@RequiresApi(Build.VERSION_CODES.O)
fun formatRelativeTime(dateString: String): String {
    return try {
        // Cần chỉnh lại logic parse này tùy theo format MockData của bạn
        // Đây là ví dụ demo
        val zdt = ZonedDateTime.parse(dateString)
        val now = ZonedDateTime.now()
        val diffSeconds = java.time.Duration.between(zdt, now).seconds

        when {
            diffSeconds < 60 -> "Vừa xong"
            diffSeconds < 3600 -> "${diffSeconds / 60} phút trước"
            diffSeconds < 86400 -> "${diffSeconds / 3600} giờ trước"
            else -> DateTimeFormatter.ofPattern("dd/MM", Locale.getDefault()).format(zdt)
        }
    } catch (e: Exception) {
        "Gần đây"
    }
}