package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail

/**
 * Data Transfer Object cho một item khóa học trong danh sách.
 * Chứa các thông tin cơ bản để hiển thị.
 */
data class CourseDto(
    val Category: CategoryDto,
    val CategoryId: Int,
    val CreatedAt: String?,
    val Description: String,
    val Id: Int,
    val Instructor: InstructorDto,
    val InstructorId: Int,
    val IsFree: Boolean,
    val Level: Int,
    val Price: Double?,
    val Rating: Float?,
    val Thumbnail: String,
    val Title: String,
    val TotalReviews: Int,
    val TotalStudents: Int,
    val UpdatedAt: String?,
    val EstimatedDuration:Int?,
    val TotalLessons: Int?
)

/**
 * Data Transfer Object cho trang chi tiết một khóa học.
 * Chứa tất cả các thông tin chi tiết từ API response.
 */
data class CourseDetailDto(
    val Id: Int,
    val Title: String,
    val Description: String,
    val Thumbnail: String,
    val InstructorId: Int,
    val Instructor: InstructorDto,
    val CategoryId: Int,
    val Category: CategoryDto,
    val Level: Int,
    val IsFree: Boolean,
    val Price: Double?,
    val Rating: Float?,
    val TotalReviews: Int,
    val TotalStudents: Int,
    val Lessons: List<LessonDto>?,
    val IsEnrolled: Boolean,
    val Requirements: List<String>?,
    val WhatYouWillLearn: List<String>?,
    val CreatedAt: String?,
    val UpdatedAt: String?


)

fun CourseDto.toDomain() = Course(
    category = Category.toDomain(),
    createdAt = CreatedAt?: "",
    description = Description,
    estimatedDuration = EstimatedDuration?: 0,
    id = Id,
    instructor = Instructor.toDomain(),
    isFree = IsFree,
    level = Level,
    price = Price?: 0.0,
    rating = Rating?: 0.0f,
    thumbnail = Thumbnail,
    title = Title,
    totalLessons = TotalLessons?: 0,
    totalReviews = TotalReviews,
    totalStudents = TotalStudents,
)

fun CourseDetailDto.toDomain(): CourseDetail {
    return CourseDetail(
        id = this.Id,
        title = this.Title,
        description = this.Description,
        thumbnail = this.Thumbnail,
        instructor = this.Instructor.toDomain(),
        category = this.Category.toDomain(),
        level = this.Level,
        isFree = this.IsFree,
        price = this.Price ?: 0.0,
        rating = this.Rating ?: 0.0f,
        totalReviews = this.TotalReviews,
        totalStudents = this.TotalStudents,
        lessons = this.Lessons?.map { it.toDomain() } ?: emptyList(),
        isEnrolled = this.IsEnrolled,
        requirements = this.Requirements ?: emptyList(),
        whatYouWillLearn = this.WhatYouWillLearn ?: emptyList(),
        createdAt = this.CreatedAt ?: "",
        updatedAt = this.UpdatedAt ?: ""
    )
}
