package com.example.khoitriso.data.dto

data class NotificationDto(
    val Id: Int,
    val UserId: Int,
    val Title: String,
    val Content: String? = null,
    val Type: Int? = null,
    val Priority: Int? = null,
    val IsRead: Boolean = false,
    val ActionUrl: String? = null,
    val RelatedId: String? = null,
    val RelatedType: String? = null,
    val CreatedAt: String
)

data class NotificationsResponse(
    val Items: List<NotificationDto>? = null,
    val Total: Int = 0,
    val UnreadCount: Int = 0,
    val Page: Int = 1,
    val PageSize: Int = 20
)
