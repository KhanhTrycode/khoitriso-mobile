package com.example.khoitriso.domain.usecase.auth

import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.repository.AuthRepository
import com.example.khoitriso.utils.debug
import kotlinx.coroutines.flow.MutableStateFlow


data class AuthUsecase(
    val authGoogleSDK: AuthGoogleSDK,
    val refresh: RefreshToken,
    val getMe: GetMe,
    val loadCurrentUserInfo: LoadCurrentUserInfo,
)

class LoadCurrentUserInfo(
    private val repo: AuthRepository,
    private val userManager: UserManager
) {
    suspend operator fun invoke(): Result<User> {
        // Try to get user info from UserManager first (local cache)
        val savedUser = userManager.getCurrentUser()
        if (savedUser != null) {
            // Đã có user info trong local, dùng luôn
            return Result.success(savedUser)
        } else {
            // Chưa có user info trong local, gọi API /auth/me để lấy
            return GetMe(repo).invoke()
        }
    }

    suspend fun saveUser(user: User) {
        userManager.saveUser(user)
    }
}


class AuthGoogleSDK(private val repo: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<Authorization> {
        val result = repo.authGoogleSDK(idToken)
        return result

    }

}

class RefreshToken(private val repo: AuthRepository) {
    suspend operator fun invoke(tokenManager: TokenManager): Authorization? {
        val result = repo.refreshToken(
            tokenManager.getRefreshToken() ?: "",
            tokenManager.getRefreshToken() ?: ""
        ).getOrNull()

        return result
    }

}

class GetMe(private val repo: AuthRepository) {
    suspend operator fun invoke(): Result<User> {
        val result = repo.getMe()
        return result


    }
}