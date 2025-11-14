package com.example.khoitriso.domain.models

data class User (
    val authProvider: String,
    val avatar: String,
    val email: String,
    val fullName: String,
    val id: Int,
    val role: Int
)