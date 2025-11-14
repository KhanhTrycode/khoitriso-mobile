package com.example.khoitriso.data.dto.users

import com.example.khoitriso.domain.models.User

data class UserDTO(
    val AuthProvider: String,
    val Avatar: String,
    val Email: String,
    val FullName: String,
    val Id: Int,
    val Role: Int
)

fun UserDTO.toDomain(): User = User(
    id = Id,
    fullName = FullName,
    email = Email,
    avatar = Avatar ?: "",
    authProvider = "",
    role = 1
)