package com.example.khoitriso.data.dto

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