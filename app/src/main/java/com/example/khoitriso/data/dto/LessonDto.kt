package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Lesson

data class LessonDto(
    val Assignments: List<AssignmentDto>,
    val ContentText: Int,
    val CourseId: Int,
    val Description: String,
    val Id: Int,
    val IsFree: Boolean,
    val IsPublished: Boolean,
    val LessonOrder: Int,
    val Materials: List<MaterialDto>?,
    val Title: String,
    val UserProgress: Any,
    val VideoDuration: Int,
    val VideoUrl: String,
)

fun LessonDto.toDomain() = Lesson(
    contentText = ContentText,
    courseId = CourseId,
    description = Description,
    id = Id,
    isFree = IsFree,
    isPublished = IsPublished,
    lessonOrder = LessonOrder,
    materials = Materials?.map { it.toDomain() } ?: emptyList(),
    title = Title,
    userProgress = UserProgress,
    videoDuration = VideoDuration,
    videoUrl = VideoUrl
)