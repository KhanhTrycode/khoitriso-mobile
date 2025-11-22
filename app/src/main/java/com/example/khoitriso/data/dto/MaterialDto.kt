package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Material

data class MaterialDto(
    val CreatedAt: String,
    val DownloadCount: Int,
    val FileName: String,
    val FilePath: String,
    val FileSize: Int,
    val FileType: String,
    val FileUrl: String,
    val Id: Int,
    val LessonId: Int,
    val Title: String,
    val UpdatedAt: Any
)

fun MaterialDto.toDomain() = Material(
    downloadCount = DownloadCount,
    fileName = FileName,
    filePath = FilePath,
    fileSize = FileSize,
    fileType = FileType,
    fileUrl = FileUrl,
    id = Id,
    lessonId = LessonId,
    title = Title,
    updatedAt = UpdatedAt
)