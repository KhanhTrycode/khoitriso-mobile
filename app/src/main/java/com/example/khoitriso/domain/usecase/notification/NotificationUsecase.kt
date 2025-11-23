package com.example.khoitriso.domain.usecase.notification

import com.example.khoitriso.domain.models.Notification
import com.example.khoitriso.domain.repository.NotificationRepository
import com.example.khoitriso.domain.repository.NotificationsResult

data class NotificationUsecase(
    val getUserNotifications: GetUserNotifications,
    val getNotificationById: GetNotificationById,
    val markAsRead: MarkAsRead,
    val markAllAsRead: MarkAllAsRead
)

class GetUserNotifications(private val repo: NotificationRepository) {
    suspend operator fun invoke(
        isRead: Boolean? = null,
        type: Int? = null,
        priority: Int? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<NotificationsResult> {
        return repo.getUserNotifications(isRead, type, priority, page, pageSize)
    }
}

class GetNotificationById(private val repo: NotificationRepository) {
    suspend operator fun invoke(id: Int): Result<Notification> {
        return repo.getNotificationById(id)
    }
}

class MarkAsRead(private val repo: NotificationRepository) {
    suspend operator fun invoke(id: Int): Result<Boolean> {
        return repo.markAsRead(id)
    }
}

class MarkAllAsRead(private val repo: NotificationRepository) {
    suspend operator fun invoke(): Result<Int> {
        return repo.markAllAsRead()
    }
}
