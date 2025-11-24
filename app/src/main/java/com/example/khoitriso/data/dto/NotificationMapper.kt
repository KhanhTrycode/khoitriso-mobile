package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Notification

fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = Id,
        userId = UserId,
        title = Title,
        content = Content,
        type = Type,
        priority = Priority,
        isRead = IsRead,
        actionUrl = ActionUrl,
        relatedId = RelatedId,
        relatedType = RelatedType,
        createdAt = CreatedAt
    )
}
