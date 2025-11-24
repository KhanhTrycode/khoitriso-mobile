package com.example.khoitriso.ui.notification

import androidx.compose.animation.core.copy
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.input.key.type
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.NotificationDto
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.data.signalr.SignalRService
import com.example.khoitriso.domain.models.Notification
import com.example.khoitriso.domain.repository.NotificationsResult
import com.example.khoitriso.domain.usecase.notification.NotificationUsecase
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.contains

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUsecase: NotificationUsecase,
    private val signalRService: SignalRService,
) : BaseViewModel() {

    // Notifications list
    private val _notifications = MutableStateFlow<UiState<NotificationsResult>>(UiState.Loading)
    val notifications: StateFlow<UiState<NotificationsResult>> = _notifications.asStateFlow()

    // Filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterRead = MutableStateFlow<Boolean?>(null)
    val filterRead: StateFlow<Boolean?> = _filterRead.asStateFlow()

    private val _filterType = MutableStateFlow<Int?>(null)
    val filterType: StateFlow<Int?> = _filterType.asStateFlow()

    private val _filterPriority = MutableStateFlow<Int?>(null)
    val filterPriority: StateFlow<Int?> = _filterPriority.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    // SignalR connection state
    val connectionState: StateFlow<SignalRService.ConnectionState> = signalRService.connectionState

    // Real-time notifications from SignalR
    private val _realtimeNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val realtimeNotifications: StateFlow<List<Notification>> = _realtimeNotifications.asStateFlow()

    // Filtered notifications (from API + real-time)
    val filteredNotifications: StateFlow<List<Notification>> = combine(
        _notifications,
        _realtimeNotifications,
        _searchQuery,
        _filterRead,
        _filterType
    ) { notificationsState, realtime, search, readFilter, typeFilter ->
        val apiNotifications = when (notificationsState) {
            is UiState.Success -> notificationsState.data.items
            else -> emptyList()
        }

        // Combine API notifications with real-time ones (avoid duplicates)
        val allNotifications = (realtime + apiNotifications).distinctBy { it.id }

        // Apply filters
        allNotifications.filter { notification ->
            val matchesSearch = search.isEmpty() ||
                    notification.title.contains(search, ignoreCase = true) ||
                    (notification.content?.contains(search, ignoreCase = true) == true)
            val matchesRead = readFilter == null || notification.isRead == readFilter
            val matchesType = typeFilter == null || notification.type == typeFilter
            // Since we cannot combine priority directly, we get its value inside the filter logic
            val matchesPriority = _filterPriority.value == null || notification.priority == _filterPriority.value

            matchesSearch && matchesRead && matchesType && matchesPriority
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    init {
        // Listen to SignalR notifications
        viewModelScope.launch {
            signalRService.notifications.collect { notificationDto ->
                notificationDto?.let {
                    val notification = it.toDomain()
                    // Add to real-time notifications list
                    _realtimeNotifications.value = listOf(notification) + _realtimeNotifications.value
                }
            }
        }
    }

    fun startSignalRConnection() {
        viewModelScope.launch {
            signalRService.startConnection()
        }
    }

    fun stopSignalRConnection() {
        viewModelScope.launch {
            signalRService.stopConnection()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterRead(isRead: Boolean?) {
        _filterRead.value = isRead
        _currentPage.value = 1
        loadNotifications(1)
    }

    fun setFilterType(type: Int?) {
        _filterType.value = type
        _currentPage.value = 1
        loadNotifications(1)
    }

    fun setFilterPriority(priority: Int?) {
        _filterPriority.value = priority
        _currentPage.value = 1
        loadNotifications(1)
    }

    fun loadNotifications(page: Int = 1, pageSize: Int = 20) {
        if (page == 1) {
            _notifications.value = UiState.Loading
            _realtimeNotifications.value = emptyList() // Clear real-time on first page load
        }
        _currentPage.value = page

        viewModelScope.launch {
            val result = notificationUsecase.getUserNotifications(
                isRead = _filterRead.value,
                type = _filterType.value,
                priority = _filterPriority.value,
                page = page,
                pageSize = pageSize
            )
            _notifications.value = result.fold(
                onSuccess = {
                    val currentItems = (_notifications.value as? UiState.Success)?.data?.items ?: emptyList()
                    if (page > 1) {
                        it.items = currentItems + it.items
                    }
                    UiState.Success(it)
                },
                onFailure = { UiState.Error(it.message ?: "Failed to load notifications") }
            )
        }
    }

    fun markAsRead(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = notificationUsecase.markAsRead(id)
            result.fold(
                onSuccess = {
                    // Update local state immediately for better UX
                    updateNotificationInState(id) { it.copy(isRead = true) }
                    onSuccess()
                },
                onFailure = { onError(it.message ?: "Failed to mark as read") }
            )
        }
    }

    fun markAllAsRead(onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = notificationUsecase.markAllAsRead()
            result.fold(
                onSuccess = { count ->
                    // Reload for simplicity, or update all local items to isRead = true
                    loadNotifications(1)
                    onSuccess(count)
                },
                onFailure = { onError(it.message ?: "Failed to mark all as read") }
            )
        }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _filterRead.value = null
        _filterType.value = null
        _filterPriority.value = null
        if (_currentPage.value != 1 || _filterRead.value != null || _filterType.value != null || _filterPriority.value != null) {
            loadNotifications(1)
        }
    }

    private fun updateNotificationInState(id: Int, transform: (Notification) -> Notification) {
        val currentNotifications = (_notifications.value as? UiState.Success)?.data
        currentNotifications?.let {
            val updatedItems = it.items.map { notification ->
                if (notification.id == id) transform(notification) else notification
            }
            _notifications.value = UiState.Success(it.copy(items = updatedItems))
        }

        val updatedRealtime = _realtimeNotifications.value.map { notification ->
            if (notification.id == id) transform(notification) else notification
        }
        _realtimeNotifications.value = updatedRealtime
    }
}
