package com.example.khoitriso.data.dto.forum

data class ForumAttachmentDto(
    val FileName: String,
    val FileUrl: String,
    val FileSize: Long,
    val FileType: String? = null
)
