package com.example.khoitriso.data.dto.admin

data class Lesson(
    val Description: String,
    val Id: Int,
    val IsFree: Boolean,
    val IsPublished: Boolean,
    val LessonOrder: Int,
    val Title: String,
    val VideoDuration: Int,
    val VideoUrl: String
)