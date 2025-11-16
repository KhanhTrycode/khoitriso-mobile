package com.example.khoitriso.domain.models

data class Course(
    val approvalStatus: Int,
    val category: Category,
    val description: String,
    val estimatedDuration: Int,
    val id: Int,
    val instructor: Instructor,
    val isFree: Boolean,
    val isPublished: Boolean,
    val level: Int,
    val price: Int,
    val rating: Float,
    val thumbnail: String,
    val title: String,
    val totalLessons: Int,
    val totalReviews: Int,
    val totalStudents: Int
)