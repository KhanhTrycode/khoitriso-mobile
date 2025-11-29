package com.example.khoitriso.domain.models

data class CourseDetail(
    val category: Category,
    val createdAt: String = "",
    val description: String,
    val id: Int,
    val instructor: Instructor,
    val isEnrolled: Boolean,
    val isFree: Boolean,
    val lessons: List<Lesson>,
    val level: Int,
    val price: Double,
    val rating: Float,
    val requirements: List<String>,
    val thumbnail: String,
    val title: String,
    val totalReviews: Int,
    val totalStudents: Int,
    val updatedAt: String = "",
    val whatYouWillLearn: List<String>
)