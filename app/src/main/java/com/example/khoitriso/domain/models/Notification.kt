package com.example.khoitriso.domain.models

data class Notification(
    val id: Int,
    val userId: Int,
    val title: String,
    val content: String? = null,
    val type: Int? = null, // 1-14 (system, course, lesson, assignment, etc.)
    val priority: Int? = null, // 1 = High, 2 = Medium, 3 = Low
    val isRead: Boolean = false,
    val actionUrl: String? = null,
    val relatedId: String? = null,
    val relatedType: String? = null,
    val createdAt: String
)
