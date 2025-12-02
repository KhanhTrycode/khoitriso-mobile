package com.example.khoitriso.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.models.Notification
import com.example.khoitriso.test.MockData
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

) : ViewModel() {

    // --- STATE ---
    // Giả lập Database local bằng một MutableStateFlow
    private val _allNotifications = MutableStateFlow(MockData.mockNotifications)

    // Các biến Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterUnreadOnly = MutableStateFlow(false)
    val filterUnreadOnly = _filterUnreadOnly.asStateFlow()

    // 0: All, 1: System, 2: Order, 3: Promotion... (Tuỳ logic MockData)
    private val _filterType = MutableStateFlow<Int?>(null)
    val filterType = _filterType.asStateFlow()

    // --- COMBINED LOGIC ---
    // Tự động tính toán danh sách hiển thị dựa trên Source + Filters
    val uiState: StateFlow<UiState<List<Notification>>> = combine(
        _allNotifications,
        _searchQuery,
        _filterUnreadOnly,
        _filterType
    ) { notifications, query, unreadOnly, type ->


        val filtered = notifications.filter { item ->
            val matchQuery = query.isEmpty() ||
                    item.title.contains(query, ignoreCase = true) ||
                    (item.content?.contains(query, ignoreCase = true) == true)

            // 2. Lọc theo trạng thái Đọc
            val matchRead = if (unreadOnly) !item.isRead else true

            // 3. Lọc theo Loại
            val matchType = if (type == null) true else item.type == type

            matchQuery && matchRead && matchType
        }.sortedByDescending { it.createdAt } // Mới nhất lên đầu

        if (filtered.isEmpty()) UiState.Success(emptyList()) else UiState.Success(filtered)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    // --- ACTIONS ---

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
            // Cập nhật trực tiếp vào luồng dữ liệu giả lập
            _allNotifications.update { currentList ->
                currentList.map {
                    if (it.id == notificationId) it.copy(isRead = true) else it
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            _allNotifications.update { currentList ->
                currentList.map { it.copy(isRead = true) }
            }
        }
    }

    fun refresh() {
        // Giả lập reload
        viewModelScope.launch {
            // Có thể reset lại mock data gốc hoặc fetch mới
        }
    }
}