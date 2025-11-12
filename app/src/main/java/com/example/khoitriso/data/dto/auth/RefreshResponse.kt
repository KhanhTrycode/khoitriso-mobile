package com.example.khoitriso.data.dto.auth

data class RefreshResponse(
    val code: Int,
    val message: String,
    val result: Result
)