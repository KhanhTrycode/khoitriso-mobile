package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.AuthApi
import com.example.khoitriso.data.api.UpdateProfileRequest
import com.example.khoitriso.data.api.UsersApi
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.repository.UserRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
    private val authApi: AuthApi
): UserRepository {
    
    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = authApi.getMe()
            if (response.isSuccessful) {
                val userDto = response.body()?.Result
                if (userDto != null) {
                    Result.success(userDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getProfile(): Result<User> {
        val response = usersApi.getProfile()
        return try {
            if (response.isSuccessful) {
                Result.success(response.body()?.Result?.toDomain() ?: throw Exception("Response body is null"))
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(fullName: String, email: String): Result<User> {
        return try {
            val request = UpdateProfileRequest(FullName = fullName, Email = email)
            val response = usersApi.updateProfile(request)
            if (response.isSuccessful) {
                val userDto = response.body()?.Result
                if (userDto != null) {
                    Result.success(userDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(file: MultipartBody.Part): Result<User> {
        return try {
            val response = usersApi.uploadAvatar(file)
            if (response.isSuccessful) {
                val userDto = response.body()?.Result
                if (userDto != null) {
                    Result.success(userDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}