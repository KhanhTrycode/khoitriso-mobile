package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Instructor

data class InstructorDto(
    val Avatar: String?,
    val Bio: String?,
    val Id: Int,
    val Name: String
)

fun InstructorDto.toDomain() = Instructor(
    avatar = Avatar?: "",
    bio = Bio?: "",
    id = Id,
    name = Name

)