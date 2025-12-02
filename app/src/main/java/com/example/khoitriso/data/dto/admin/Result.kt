package com.example.khoitriso.data.dto.admin

data class Result(
    val ApprovalStatus: Int,
    val Category: Category,
    val CategoryId: Int,
    val CreatedAt: String,
    val Description: String,
    val Id: Int,
    val Instructor: Instructor,
    val InstructorId: Int,
    val IsActive: Boolean,
    val IsEnrolled: Boolean,
    val IsFree: Boolean,
    val IsPublished: Boolean,
    val Lessons: List<Lesson>,
    val Level: Int,
    val Price: Int,
    val Rating: Int,
    val Requirements: List<String>,
    val Thumbnail: String,
    val Title: String,
    val TotalReviews: Int,
    val TotalStudents: Int,
    val UpdatedAt: String,
    val WhatYouWillLearn: List<String>
)