package com.example.khoitriso.domain.models

data class Course(
    val category: Category,
    val createdAt: String = "",
    val description: String,
    val estimatedDuration: Int,
    val id: Int,
    val instructor: Instructor,
    val isFree: Boolean,
    val level: Int,
    val price: Double,
    val rating: Float,
    val thumbnail: String,
    val title: String,
    val totalLessons: Int,
    val totalReviews: Int,
    val totalStudents: Int,
)