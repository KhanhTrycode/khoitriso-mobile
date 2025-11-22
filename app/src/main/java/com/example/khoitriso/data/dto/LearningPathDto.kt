    package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.LearningPath

data class LearningPathDto(
    val ApprovalStatus: Int,
    val ApprovalStatusName: String,
    val Category: CategoryDto,
    val CategoryId: Int,
    val CourseCount: Int,
    val Courses: List<CourseDto>,
    val CreatedAt: String,
    val Description: String,
    val DifficultyLevel: Int,
    val DifficultyLevelName: String,
    val EnrollmentCount: Int,
    val EstimatedDuration: Int,
    val Id: Int,
    val Instructor: InstructorDto,
    val InstructorId: Int,
    val IsActive: Boolean,
    val IsEnrolled: Boolean,
    val IsPublished: Boolean,
    val Price: Int,
    val QualityScore: Float?,
    val ReviewNotes: String?,
    val Thumbnail: String,
    val Title: String,
    val UpdatedAt: String
)

fun LearningPathDto.toDomain() = LearningPath(
    approvalStatus = ApprovalStatus,
    approvalStatusName = ApprovalStatusName,
    category = Category.toDomain(),
    categoryId = CategoryId,
    courseCount = CourseCount,
    courses = Courses.map { it.toDomain() },
    createdAt = CreatedAt,
    description = Description,
    difficultyLevel = DifficultyLevel,
    difficultyLevelName = DifficultyLevelName,
    enrollmentCount = EnrollmentCount,
    estimatedDuration = EstimatedDuration,
    id = Id,
    instructor = Instructor.toDomain(),
    instructorId = InstructorId,
    isActive = IsActive,
    isEnrolled = IsEnrolled,
    isPublished = IsPublished,
    price = Price,
    qualityScore = QualityScore?: 0f,
    reviewNotes = ReviewNotes?: "",
    thumbnail = Thumbnail,
    title = Title,
    updatedAt = UpdatedAt
)