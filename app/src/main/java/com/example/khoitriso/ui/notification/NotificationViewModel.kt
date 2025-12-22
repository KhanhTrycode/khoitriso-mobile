package com.example.khoitriso.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.models.Notification
import com.example.khoitriso.domain.usecase.notification.NotificationUsecase
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUsecase: NotificationUsecase
) : ViewModel() {

    // --- STATE ---
    private val _uiState = MutableStateFlow<UiState<List<Notification>>>(UiState.Loading)
    
    // Các biến Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterUnreadOnly = MutableStateFlow(false)
    val filterUnreadOnly = _filterUnreadOnly.asStateFlow()

    // 0: All, 1: System, 2: Order, 3: Promotion... (Tuỳ logic MockData)
    private val _filterType = MutableStateFlow<Int?>(null)
    val filterType = _filterType.asStateFlow()

    // --- COMBINED LOGIC ---
    val uiState: StateFlow<UiState<List<Notification>>> = _uiState.asStateFlow()

    init {
        loadNotifications()
        observeFilters()
    }
    
    private fun observeFilters() {
        viewModelScope.launch {
            combine(
                _searchQuery,
                _filterUnreadOnly,
                _filterType
            ) { query, unreadOnly, type ->
                Triple(query, unreadOnly, type)
            }.collect { (query, unreadOnly, type) ->
                applyFilters(query, unreadOnly, type)
            }
        }
    }
    
    private fun applyFilters(query: String, unreadOnly: Boolean, type: Int?) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val allNotifications = currentState.data
            val filtered = allNotifications.filter { item ->
                val matchQuery = query.isEmpty() ||
                        item.title.contains(query, ignoreCase = true) ||
                        (item.content?.contains(query, ignoreCase = true) == true)

                val matchRead = if (unreadOnly) !item.isRead else true
                val matchType = if (type == null) true else item.type == type

                matchQuery && matchRead && matchType
            }.sortedByDescending { it.createdAt }

            _uiState.value = UiState.Success(filtered)
        }
    }

    // --- ACTIONS ---
    
    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            notificationUsecase.getUserNotifications(
                isRead = null,
                type = null,
                priority = null,
                page = 1,
                pageSize = 100
            ).fold(
                onSuccess = { result ->
                    _uiState.value = UiState.Success(result.items)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Không thể tải thông báo")
                }
            )
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleUnreadFilter() {
        _filterUnreadOnly.update { !it }
    }

    fun setTypeFilter(type: Int?) {
        _filterType.value = type
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            notificationUsecase.markAsRead(notificationId).fold(
                onSuccess = {
                    // Cập nhật local state
                    val currentState = _uiState.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.map {
                            if (it.id == notificationId) it.copy(isRead = true) else it
                        }
                        _uiState.value = UiState.Success(updatedList)
                    }
                },
                onFailure = {
                    // Vẫn cập nhật local để UX mượt hơn
                    val currentState = _uiState.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.map {
                            if (it.id == notificationId) it.copy(isRead = true) else it
                        }
                        _uiState.value = UiState.Success(updatedList)
                    }
                }
            )
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationUsecase.markAllAsRead().fold(
                onSuccess = {
                    // Cập nhật tất cả thành đã đọc
                    val currentState = _uiState.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.map { it.copy(isRead = true) }
                        _uiState.value = UiState.Success(updatedList)
                    }
                },
                onFailure = {
                    // Vẫn cập nhật local
                    val currentState = _uiState.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.map { it.copy(isRead = true) }
                        _uiState.value = UiState.Success(updatedList)
                    }
                }
            )
        }
    }

    fun refresh() {
        loadNotifications()
    }
}