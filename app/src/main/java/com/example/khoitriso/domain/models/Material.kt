package com.example.khoitriso.domain.models

data class Material (
    val downloadCount: Int,
    val fileName: String,
    val filePath: String,
    val fileSize: Int,
    val fileType: String,
    val fileUrl: String,
    val id: Int,
    val lessonId: Int,
    val title: String,
    val updatedAt: Any
)