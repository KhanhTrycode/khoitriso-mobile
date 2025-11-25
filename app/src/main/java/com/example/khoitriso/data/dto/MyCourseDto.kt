package com.example.khoitriso.data.dto

data class MyCourseDto(
    val CourseId: Int,
    val Course: CourseDto,
    val ProgressPercentage: Double,
    val EnrolledAt: String,
    val LastAccessed: String?,
    val IsCompleted: Boolean,
    val CompletedAt: String?
)

