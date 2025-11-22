package com.example.khoitriso.domain.models

data class LearningPath(
    val approvalStatus: Int,
    val approvalStatusName: String,
    val category: Category,
    val categoryId: Int,
    val courseCount: Int,
    val courses: List<Course>,
    val createdAt: String,
    val description: String,
    val difficultyLevel: Int,
    val difficultyLevelName: String,
    val enrollmentCount: Int,
    val estimatedDuration: Int,
    val id: Int,
    val instructor: Instructor,
    val instructorId: Int,
    val isActive: Boolean,
    val isEnrolled: Boolean,
    val isPublished: Boolean,
    val price: Int,
    val qualityScore: Float,
    val reviewNotes: String,
    val thumbnail: String,
    val title: String,
    val updatedAt: String
)
