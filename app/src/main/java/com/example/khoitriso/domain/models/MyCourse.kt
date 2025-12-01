package com.example.khoitriso.domain.models

import com.example.khoitriso.data.dto.CourseDto

data class MyCourse(
    val courseId: Int,
    val course: Course,
    val progressPercentage: Float,
    val enrolledAt: String,
    val lastAccessed: String,
    val isCompleted: Boolean,
    val completedAt: String
)