package com.example.khoitriso.data.dto.forum

data class ForumCategoryDto(
    val Id: String,
    val Name: String,
    val Description: String? = null,
    val Color: String? = null,
    val Icon: String? = null,
    val IsActive: Boolean = true,
    val SortOrder: Int = 0
)
