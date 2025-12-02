package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Lesson

data class LessonDto(
    val Assignments: List<AssignmentDto>?,
    val ContentText: String?,
    val CourseId: Int?,
    val Description: String,
    val Id: Int,
    val IsFree: Boolean,
    val IsPublished: Boolean,
    val LessonOrder: Int,
    val Materials: List<MaterialDto>?,
    val Title: String,
    val VideoDuration: Int,
    val VideoUrl: String,

)

fun LessonDto.toDomain() = Lesson(
    assignments = Assignments?.map { it.toDomain()}?: emptyList(),
    contentText = ContentText?: "",
    courseId = CourseId?: -1,
    description = Description,
    id = Id,
    isFree = IsFree,
    isPublished = IsPublished,
    lessonOrder = LessonOrder,
    materials = Materials?.map { it.toDomain() } ?: emptyList(),
    title = Title,
    videoDuration = VideoDuration,
    videoUrl = VideoUrl
)