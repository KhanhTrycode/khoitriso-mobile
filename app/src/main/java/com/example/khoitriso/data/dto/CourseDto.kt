package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Course

data class CourseDto(
    val ApprovalStatus: Int,
    val Category: CategoryDto,
    val CategoryId: Int,
    val Description: String,
    val EstimatedDuration: Int,
    val Id: Int,
    val Instructor: InstructorDto,
    val InstructorId: Int,
    val IsFree: Boolean,
    val IsPublished: Boolean,
    val Level: Int,
    val Price: Int,
    val Rating: Float,
    val Thumbnail: String,
    val Title: String,
    val TotalLessons: Int,
    val TotalReviews: Int,
    val TotalStudents: Int
)

fun CourseDto.toDomain() = Course(
    approvalStatus = ApprovalStatus,
    category = Category.toDomain(),
    description = Description,
    estimatedDuration = EstimatedDuration,
    id = Id,
    instructor = Instructor.toDomain(),
    isFree = IsFree,
    isPublished = IsPublished,
    level = Level,
    price = Price,
    rating = Rating,
    thumbnail = Thumbnail,
    title = Title,
    totalLessons = TotalLessons,
    totalReviews = TotalReviews,
    totalStudents = TotalStudents
)