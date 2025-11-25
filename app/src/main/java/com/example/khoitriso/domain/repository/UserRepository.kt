package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.User
import okhttp3.MultipartBody

interface UserRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(fullName: String, email: String): Result<User>
    suspend fun uploadAvatar(file: MultipartBody.Part): Result<User>
}