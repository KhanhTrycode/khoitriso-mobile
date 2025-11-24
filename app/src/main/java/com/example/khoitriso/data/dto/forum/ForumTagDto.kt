package com.example.khoitriso.data.dto.forum

data class ForumTagDto(
    val Id: String,
    val Name: String,
    val Description: String? = null,
    val Color: String? = null,
    val IsActive: Boolean = true
)
