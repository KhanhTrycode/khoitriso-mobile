package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.AssignmentPreview

data class AssignmentPreviewDto(
    val Id: Int,
    val Title: String,
    val Description: String,
    val DueDate: String?
)

fun AssignmentPreviewDto.toDomain() = AssignmentPreview(
    id = Id,
    title = Title,
    description = Description,
    dueDate = DueDate
)

