package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Authorization

interface AuthRepository {
    suspend fun authGoogleSDK(idToken: String): Result<Authorization?>

    suspend fun refreshToken(refreshToken: String, accessToken: String): Result<Authorization?>
}