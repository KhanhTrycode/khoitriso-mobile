package com.example.khoitriso.domain.usecase.auth

data class AuthUsecase (
    val authGoogleSDK : AuthGoogleSDK,
    val refresh: RefreshToken
)

