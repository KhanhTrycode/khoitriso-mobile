package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Notification

data class NotificationDto(
    val Id: Int,
    val UserId: Int? = null,
    val Title: String,
    val Message: String? = null,
    val Type: Int? = null,
    val Priority: Int? = null,
    val IsRead: Boolean = false,
    val ActionUrl: String? = null,
    val RelatedId: String? = null,
    val RelatedType: String? = null,
    val CreatedAt: String
)

data class NotificationsResponse(
    val Data: List<NotificationDto>? = null,
    val Total: Int = 0,
    val UnreadCount: Int = 0,
    val Page: Int = 1,
    val PageSize: Int = 20
)
