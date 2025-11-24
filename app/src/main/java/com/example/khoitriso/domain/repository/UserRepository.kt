package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.User

interface UserRepository {
    suspend fun getProfile() : Result<User>
    suspend fun uploadAvatar() : Result<String>
}