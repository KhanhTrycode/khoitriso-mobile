package com.example.khoitriso.domain.models

data class Course(
    val category: Category,
    val categoryId: Int = 0,
    val createdAt: String = "",
    val description: String,
    val estimatedDuration: Int,
    val id: Int,
    val instructor: Instructor,
    val instructorId: Int = 0,
    val isEnrolled: Boolean = false,
    val isFree: Boolean,
    val lessons: List<Lesson> = emptyList(),
    val level: Int,
    val price: Int,
    val rating: Float,
    val requirements: List<String> = emptyList(),
    val thumbnail: String,
    val title: String,
    val totalLessons: Int,
    val totalReviews: Int,
    val totalStudents: Int,
    val updatedAt: String = "",
    val whatYouWillLearn: List<String> = emptyList()
)