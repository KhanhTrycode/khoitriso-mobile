package com.example.khoitriso.ui.notification

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUsecase: NotificationUsecase,
    private val signalRService: SignalRService
) : BaseViewModel() {

    // Notifications list
    private val _notifications = MutableStateFlow<UiState<NotificationsResult>>(UiState.Loading)
    val notifications: StateFlow<UiState<NotificationsResult>> = _notifications.asStateFlow()

    // Filter states
    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterRead = MutableStateFlow<Boolean?>(null)
    val filterRead: StateFlow<Boolean?> = _filterRead.asStateFlow()

    private val _filterType = MutableStateFlow<Int?>(null)
    val filterType: StateFlow<Int?> = _filterType.asStateFlow()

    private val _filterPriority = MutableStateFlow<Int?>(null)
    val filterPriority: StateFlow<Int?> = _filterPriority.asStateFlow()

    private val _currentPage = MutableStateFlow<Int>(1)
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
        _filterType,
        _filterPriority
    ) { notificationsState, realtime, search, readFilter, typeFilter, priorityFilter ->
        val apiNotifications = when (notificationsState) {
            is UiState.Success -> notificationsState.data.items
            else -> emptyList()
        }
        
        // Combine API notifications with real-time ones (avoid duplicates)
        val allNotifications = (apiNotifications + realtime).distinctBy { it.id }
        
        // Apply filters
        allNotifications.filter { notification ->
            // Search filter
            val matchesSearch = search.isEmpty() || 
                notification.title.contains(search, ignoreCase = true) ||
                (notification.content?.contains(search, ignoreCase = true) == true)
            
            // Read filter
            val matchesRead = readFilter == null || notification.isRead == readFilter
            
            // Type filter
            val matchesType = typeFilter == null || notification.type == typeFilter
            
            // Priority filter
            val matchesPriority = priorityFilter == null || notification.priority == priorityFilter
            
            matchesSearch && matchesRead && matchesType && matchesPriority
        }
    }.asStateFlow()

    init {
        // Listen to SignalR notifications
        viewModelScope.launch {
            signalRService.notifications.collect { notificationDto ->
                notificationDto?.let {
                    val notification = it.toDomain()
                    // Add to real-time notifications list
                    _realtimeNotifications.value = _realtimeNotifications.value + notification
                    // Refresh notifications list
                    loadNotifications(_currentPage.value)
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

    fun loadNotifications(page: Int = 1) {
        _currentPage.value = page
        _notifications.value = UiState.Loading
        viewModelScope.launch {
            val result = notificationUsecase.getUserNotifications(
                isRead = _filterRead.value,
                type = _filterType.value,
                priority = _filterPriority.value,
                page = page,
                pageSize = 20
            )
            _notifications.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load notifications") }
            )
        }
    }

    fun markAsRead(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = notificationUsecase.markAsRead(id)
            result.fold(
                onSuccess = {
                    onSuccess()
                    loadNotifications(_currentPage.value) // Refresh list
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
                    onSuccess(count)
                    loadNotifications(_currentPage.value) // Refresh list
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
        _currentPage.value = 1
        loadNotifications(1)
    }
}
