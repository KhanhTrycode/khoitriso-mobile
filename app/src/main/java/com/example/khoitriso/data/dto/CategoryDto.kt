package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Category

data class CategoryDto(

    val Id: Int,
    val Name: String,


)

fun CategoryDto.toDomain(): Category = Category(
    id = Id,
    name = Name,
)