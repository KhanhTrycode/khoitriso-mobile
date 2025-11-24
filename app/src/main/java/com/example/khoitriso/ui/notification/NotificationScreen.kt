package com.example.khoitriso.ui.notification

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Notification
import com.example.khoitriso.data.signalr.SignalRService
import com.example.khoitriso.utils.UiState
import java.text.SimpleDateFormat
import java.util.*

val notificationTypeLabels = mapOf(
    1 to ("Hệ thống" to Color(0xFF3B82F6)),
    2 to ("Khóa học" to Color(0xFF10B981)),
    3 to ("Bài học" to Color(0xFF8B5CF6)),
    4 to ("Bài tập" to Color(0xFFF97316)),
    5 to ("Đơn hàng" to Color(0xFFEAB308)),
    6 to ("Thanh toán" to Color(0xFF6366F1)),
    7 to ("Chứng chỉ" to Color(0xFFEC4899)),
    8 to ("Diễn đàn" to Color(0xFF06B6D4)),
    9 to ("Đánh giá" to Color(0xFF14B8A6)),
    10 to ("Thông báo" to Color(0xFF6B7280)),
    11 to ("Lớp học trực tiếp" to Color(0xFFEF4444)),
    12 to ("Lộ trình học" to Color(0xFFA855F7)),
    13 to ("Sách" to Color(0xFFF59E0B)),
    14 to ("Yêu thích" to Color(0xFFF43F5E))
)

val priorityLabels = mapOf(
    1 to ("Cao" to Color(0xFFEF4444)),
    2 to ("Trung bình" to Color(0xFFEAB308)),
    3 to ("Thấp" to Color(0xFF6B7280))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val filteredNotifications by viewModel.filteredNotifications.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterRead by viewModel.filterRead.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    
    var searchText by remember { mutableStateOf("") }
    var markingAll by remember { mutableStateOf(false) }
    
    // Get unread count from filtered notifications
    val unreadCount = remember {
        derivedStateOf {
            filteredNotifications.count { !it.isRead }
        }
    }.value
    
    LaunchedEffect(Unit) {
        viewModel.startSignalRConnection()
        viewModel.loadNotifications()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            // Don't disconnect SignalR here - keep it running for real-time updates
            // viewModel.stopSignalRConnection()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Connection status indicator
                    when (connectionState) {
                        is SignalRService.ConnectionState.Connected -> {
                            Icon(
                                Icons.Default.CheckCircle,
                                "Connected",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp).padding(end = 8.dp)
                            )
                        }
                        is SignalRService.ConnectionState.Connecting -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp).padding(end = 8.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        is SignalRService.ConnectionState.Reconnecting -> {
                            Icon(
                                Icons.Default.Refresh,
                                "Reconnecting",
                                modifier = Modifier.size(20.dp).padding(end = 8.dp)
                            )
                        }
                        else -> {}
                    }
                    
                    // Mark all as read button
                    if (unreadCount > 0) {
                        IconButton(
                            onClick = {
                                markingAll = true
                                viewModel.markAllAsRead(
                                    onSuccess = { markingAll = false },
                                    onError = { markingAll = false }
                                )
                            }
                        ) {
                            if (markingAll) {
                                CircularProgressIndicator(Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.Done, "Đánh dấu tất cả đã đọc")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Header with unread count
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Thông báo",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (unreadCount > 0) "$unreadCount thông báo chưa đọc" else "Tất cả thông báo đã được đọc",
                            fontSize = 14.sp,
                            color = if (unreadCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Filters
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.List, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Bộ lọc và tìm kiếm",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Search Bar
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            viewModel.setSearchQuery(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tìm kiếm theo tiêu đề hoặc nội dung...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchText = ""
                                    viewModel.setSearchQuery("")
                                }) {
                                    Icon(Icons.Default.Clear, null)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Filter Chips Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Read/Unread Filter
                        FilterChip(
                            selected = filterRead == false,
                            onClick = { viewModel.setFilterRead(if (filterRead == false) null else false) },
                            label = { Text("Chưa đọc") }
                        )
                        FilterChip(
                            selected = filterRead == true,
                            onClick = { viewModel.setFilterRead(if (filterRead == true) null else true) },
                            label = { Text("Đã đọc") }
                        )
                        
                        // Type Filter Dropdown
                        var showTypeMenu by remember { mutableStateOf(false) }
                        Box {
                            FilterChip(
                                selected = filterType != null,
                                onClick = { showTypeMenu = true },
                                label = {
                                    Text(
                                        if (filterType != null) notificationTypeLabels[filterType]?.first ?: "Loại" else "Loại"
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = showTypeMenu,
                                onDismissRequest = { showTypeMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Tất cả loại") },
                                    onClick = {
                                        viewModel.setFilterType(null)
                                        showTypeMenu = false
                                    }
                                )
                                notificationTypeLabels.forEach { (type, pair) -> // Bước 1: Chỉ phân rã thành key và value (pair)
                                    val (label, _) = pair // Bước 2: Phân rã pair bên trong lambda

                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            viewModel.setFilterType(type)
                                            showTypeMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    // Clear Filters Button
                    if (filterRead != null || filterType != null || searchText.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Text("Xóa tất cả bộ lọc")
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Notifications List
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            null,
                            Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            "Không có thông báo nào",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredNotifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkAsRead = {
                                viewModel.markAsRead(
                                    notification.id,
                                    onSuccess = {},
                                    onError = {}
                                )
                            },
                            onClick = {
                                // Navigate to related content
                                getNotificationRoute(notification)?.let { route ->
                                    navController.navigate(route)
                                }
                                // Mark as read when clicked
                                if (!notification.isRead) {
                                    viewModel.markAsRead(notification.id, {}, {})
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: Notification,
    onMarkAsRead: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (!notification.isRead) 4.dp else 0.dp,
                color = if (!notification.isRead) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) 
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Unread Indicator
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
                Spacer(Modifier.width(4.dp))
            } else {
                Spacer(Modifier.width(12.dp))
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title with Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Type Badge
                    notification.type?.let { type ->
                        notificationTypeLabels[type]?.let { (label, color) ->
                            Badge(containerColor = color.copy(alpha = 0.2f)) {
                                Text(label, fontSize = 10.sp, color = color)
                            }
                        }
                    }
                    
                    // Priority Badge
                    notification.priority?.let { priority ->
                        priorityLabels[priority]?.let { (label, color) ->
                            Badge(
                                containerColor = color.copy(alpha = 0.2f),
                                modifier = Modifier.border(1.dp, color, RoundedCornerShape(4.dp))
                            ) {
                                Text(label, fontSize = 10.sp, color = color)
                            }
                        }
                    }
                }
                
                // Content
                notification.content?.let { content ->
                    if (content.isNotBlank()) {
                        Text(
                            text = content,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
                
                // Meta Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formatNotificationDate(notification.createdAt),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (notification.isRead) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                Modifier.size(14.dp),
                                tint = Color(0xFF10B981)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Đã đọc",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
            
            // Mark as Read Button
            if (!notification.isRead) {
                IconButton(
                    onClick = { onMarkAsRead() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        "Đánh dấu đã đọc",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

fun getNotificationRoute(notification: Notification): String? {
    if (!notification.actionUrl.isNullOrBlank()) {
        return notification.actionUrl
    }
    
    notification.relatedId?.let { relatedId ->
        notification.relatedType?.let { relatedType ->
            return when (relatedType.lowercase()) {
                "course" -> "courseDetail/$relatedId"
                "lesson" -> "courseDetail/$relatedId/lessons/$relatedId" // TODO: Fix lesson route
                "assignment" -> "assignments/$relatedId" // TODO: Add assignment route
                "forum" -> "forum/$relatedId"
                else -> null
            }
        }
    }
    return null
}

@SuppressLint("SimpleDateFormat")
fun formatNotificationDate(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return dateString
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        dateString
    }
}

