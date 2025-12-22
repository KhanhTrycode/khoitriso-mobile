package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.MyCourse

data class MyCourseDto(
    val Id: Int,
    val Course: CourseDto,
    val ProgressPercentage: Float,
    val EnrolledAt: String,
    val IsCompleted: Boolean?,
    val CompletedAt: String?,
    val IsActive: Boolean
)

fun MyCourseDto.toDomain() = MyCourse(
    courseId = Id,
    course = Course.toDomain(),
    progressPercentage = ProgressPercentage,
    enrolledAt = EnrolledAt,
    lastAccessed = "", // Backend không trả về LastAccessed, để trống
    isCompleted = IsCompleted ?: false,
    completedAt = CompletedAt?: ""
)
