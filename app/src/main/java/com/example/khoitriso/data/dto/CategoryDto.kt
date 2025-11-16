package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Category

data class CategoryDto(

    val Description: String?,
    val Icon: String?,
    val Id: Int,
    val IsActive: Boolean?,
    val Name: String,
    val OrderIndex: Int?,
    val Parent: CategoryDto?,


)

fun CategoryDto.toDomain(): Category = Category(
    id = Id,
    name = Name,
    description = Description?: "Unknown",
    icon = Icon?: "Unknown",
    isActive = IsActive?: true,
    orderIndex = OrderIndex?: 0,
    parent = Parent?.toDomain(),
)