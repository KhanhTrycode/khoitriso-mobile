package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Course

data class CourseDto(
    val Category: CategoryDto,
    val CategoryId: Int,
    val CreatedAt: String,
    val Description: String,
    val Id: Int,
    val Instructor: InstructorDto,
    val InstructorId: Int,
    val IsEnrolled: Boolean,
    val IsFree: Boolean,
    val Lessons: List<LessonDto>,
    val Level: Int,
    val Price: Int?,
    val Rating: Float?,
    val Requirements: List<String>?,
    val Thumbnail: String,
    val Title: String,
    val TotalReviews: Int,
    val TotalStudents: Int,
    val UpdatedAt: String,
    val WhatYouWillLearn: List<String>,
    val EstimatedDuration:Int?,
    val TotalLessons: Int?
)

fun CourseDto.toDomain() = Course(
    category = Category.toDomain(),
    createdAt = CreatedAt,
    description = Description,
    estimatedDuration = EstimatedDuration?: 0,
    id = Id,
    instructor = Instructor.toDomain(),
    isEnrolled = IsEnrolled,
    isFree = IsFree,
    lessons = Lessons.map { it.toDomain() },
    level = Level,
    price = Price?: 0,
    rating = Rating?: 0f,
    requirements = Requirements ?: emptyList(),
    thumbnail = Thumbnail,
    title = Title,
    totalLessons = TotalLessons?: 0,
    totalReviews = TotalReviews,
    totalStudents = TotalStudents,
    updatedAt = UpdatedAt,
    whatYouWillLearn = WhatYouWillLearn
)