package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.User

data class UserDTO(
    val AuthProvider: String,
    val CreatedAt: String,
    val Email: String,
    val EmailVerified: Boolean,
    val FullName: String,
    val Id: Int,
    val IsActive: Boolean,
    val LastActiveAt: Any,
    val LastLogin: String,
    val Role: Int,
    val Username: String,
    val Avatar: String?
)

fun UserDTO.toDomain(): User = User(
    id = Id,
    fullName = FullName,
    email = Email,
    avatar = Avatar ?: "",
    authProvider = "",
    role = 1
)
