package com.example.khoitriso.data.dto.auth

import com.example.khoitriso.data.dto.UserDTO

data class ResultAuth(
    val User: UserDTO,
    val RefreshToken: String,
    val Token: String,
    val ExpiresAt: String
)