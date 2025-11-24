package com.example.khoitriso.domain.usecase.auth

import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.repository.AuthRepository
import com.example.khoitriso.utils.debug

data class AuthUsecase (
    val authGoogleSDK : AuthGoogleSDK,
    val refresh: RefreshToken
)

class AuthGoogleSDK(private val repo: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<Authorization> {
        val result = repo.authGoogleSDK(idToken)
        debug("AuthGoogleSDK: $result", "AuthGoogleSDK")
        return result

    }

}

class RefreshToken(private val repo: AuthRepository) {
    suspend operator fun invoke(tokenManager: TokenManager): Authorization? {
        val result = repo.refreshToken(tokenManager.getRefreshToken() ?: "",tokenManager.getRefreshToken()?: "").getOrNull()

        return result
    }

}