package com.example.khoitriso.domain.usecase.auth

import android.util.Log
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.repository.AuthRepository

class AuthGoogleSDK(private val repo: AuthRepository) {
    suspend operator fun invoke(idToken: String,tokenManager: TokenManager): Authorization? {
        val result = repo.authGoogleSDK(idToken).getOrNull()
        tokenManager.saveTokens(result?.accessToken ?: "", result?.refresh ?: "")
        return result
    }

}