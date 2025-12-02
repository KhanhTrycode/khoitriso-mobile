package com.example.khoitriso.domain.usecase.user

import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.repository.UserRepository
import okhttp3.MultipartBody

data class UserUsecase(
    val getCurrentUser: GetCurrentUser,
    val getProfile: GetProfile,
    val updateProfile: UpdateProfile,
    val uploadAvatar: UploadAvatar
)

class GetCurrentUser(private val repo: UserRepository) {
    suspend operator fun invoke(): Result<User> {
        return repo.getCurrentUser()
    }
}

class GetProfile(private val repo: UserRepository) {
    suspend operator fun invoke(): Result<User> {
        return repo.getProfile()
    }
}

class UpdateProfile(private val repo: UserRepository) {
    suspend operator fun invoke(fullName: String, email: String): Result<User> {
        return repo.updateProfile(fullName, email)
    }
}

class UploadAvatar(private val repo: UserRepository) {
    suspend operator fun invoke(file: MultipartBody.Part): Result<User> {
        return repo.uploadAvatar(file)
    }
}