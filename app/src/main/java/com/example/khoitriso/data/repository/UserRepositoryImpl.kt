package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.UsersApi
import com.example.khoitriso.data.dto.auth.GoogleAuthRequest
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi
): UserRepository {
    override suspend fun getProfile(): Result<User> {
        val response = usersApi.getProfile()
        return try {
            if (response.isSuccessful) {
                Result.success(response.body()?.Result?.toDomain() ?: throw Exception("Response body is" +
                        " null"))
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(): Result<String> {
        TODO("Not yet implemented")
    }

}