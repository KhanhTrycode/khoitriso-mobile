package com.example.khoitriso.domain.usecase.auth

import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.repository.AuthRepository

class RefreshToken(private val repo: AuthRepository) {
    suspend operator fun invoke(tokenManager: TokenManager): Authorization? {
        val result = repo.refreshToken(tokenManager.getRefreshToken() ?: "",tokenManager.getRefreshToken()?: "").getOrNull()

        return result
    }

}