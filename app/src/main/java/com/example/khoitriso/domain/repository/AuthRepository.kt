package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.models.User

interface AuthRepository {
    suspend fun authGoogleSDK(idToken: String): Result<Authorization?>

    suspend fun refreshToken(refreshToken: String, accessToken: String): Result<Authorization?>

    suspend fun getMe(): Result<User?>
}