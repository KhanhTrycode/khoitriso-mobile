package com.example.khoitriso.domain.models

data class Course(
    val ApprovalStatus: Int,
    val Category: Category,
    val Description: String,
    val EstimatedDuration: Int,
    val Id: Int,
    val Instructor: Instructor,
    val IsFree: Boolean,
    val IsPublished: Boolean,
    val Level: Int,
    val Price: Int,
    val Rating: Float,
    val Thumbnail: Int,
    val Title: String,
    val TotalLessons: Int,
    val TotalReviews: Int,
    val TotalStudents: Int
)