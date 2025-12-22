package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Notification

fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = Id,
        userId = UserId ?: 0,
        title = Title,
        content = Message,
        type = Type,
        priority = Priority,
        isRead = IsRead,
        actionUrl = ActionUrl,
        relatedId = RelatedId,
        relatedType = RelatedType,
        createdAt = CreatedAt
    )
}
