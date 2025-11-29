package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.MyCourse

data class MyCourseDto(
    val CourseId: Int,
    val Course: CourseDto,
    val ProgressPercentage: Double,
    val EnrolledAt: String,
    val LastAccessed: String?,
    val IsCompleted: Boolean,
    val CompletedAt: String?
)

fun MyCourseDto.toDomain() = MyCourse(
    courseId = CourseId,
    course = Course.toDomain(),
    progressPercentage = ProgressPercentage,
    enrolledAt = EnrolledAt,
    lastAccessed = LastAccessed?: "",
    isCompleted = IsCompleted,
    completedAt = CompletedAt?: ""
)
