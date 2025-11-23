package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.AuthApi
import com.example.khoitriso.data.api.PublicApi
import com.example.khoitriso.data.dto.auth.GoogleAuthRequest
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val publicApi: PublicApi,
    private val authApi: AuthApi
) : AuthRepository {


    override suspend fun authGoogleSDK(idToken: String): Result<Authorization?> {
        val response = publicApi.authGoogleSDK(GoogleAuthRequest(idToken))
        return try {
            if (response.isSuccessful) {
                val accessToken = response.body()?.Result?.Token.toString()
                val refreshToken = response.body()?.Result?.RefreshToken.toString()

                Result.success(Authorization(accessToken, refreshToken))
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(
        refreshToken: String,
        accessToken: String
    ): Result<Authorization?> {
        val response = authApi.refreshToken(refreshToken, accessToken)
        return try {
            if (response.isSuccessful) {
                val accessToken = response.body()?.Result?.Token.toString()
                val refreshToken = response.body()?.Result?.RefreshToken.toString()
                Result.success(Authorization(accessToken, refreshToken))
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMe(): Result<User?> {
        val response = authApi.getMe()
        return try {
            if (response.isSuccessful) {
                val userDto = response.body()?.Result ?: return Result.failure(Exception("No user data"))
                Result.success(userDto.toDomain())
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}