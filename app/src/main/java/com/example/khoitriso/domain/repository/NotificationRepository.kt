package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Notification

interface NotificationRepository {
    suspend fun getUserNotifications(
        isRead: Boolean? = null,
        type: Int? = null,
        priority: Int? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<NotificationsResult>

    suspend fun getNotificationById(id: Int): Result<Notification>
    suspend fun markAsRead(id: Int): Result<Boolean>
    suspend fun markAllAsRead(): Result<Int> // Returns count of marked notifications
}

data class NotificationsResult(
    var items: List<Notification>,
    val total: Int,
    val unreadCount: Int,
    val page: Int,
    val pageSize: Int
)
