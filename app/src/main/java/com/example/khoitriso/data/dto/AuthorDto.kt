package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Author

data class AuthorDto(
    val Avatar: String?,
    val FullName: String,
    val Id: Int
)

fun AuthorDto.toDomain() = Author(
    avatar = Avatar?: "Unknown",
    fullName = FullName,
    id = Id

)