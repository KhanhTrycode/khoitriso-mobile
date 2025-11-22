package com.example.khoitriso.domain.models

data class Lesson(
    val contentText: Int,
    val courseId: Int,
    val description: String,
    val id: Int,
    val isFree: Boolean,
    val isPublished: Boolean,
    val lessonOrder: Int,
    val materials: List<Material>,
    val title: String,
    val userProgress: Any?,
    val videoDuration: Int,
    val videoUrl: String
)
