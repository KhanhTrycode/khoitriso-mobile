package com.example.khoitriso.domain.usecase.user

import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.repository.AuthRepository
import com.example.khoitriso.utils.debug

data class UserUsecase(
    val getProfile: GetProfile,
    val uploadAvatar: UploadProfile
)

class UploadProfile {

}

class GetProfile(private val repo: UserRepository) {
    suspend operator fun invoke(idToken: String): Result<Authorization> {
        val result = repo.authGoogleSDK(idToken)
        debug("AuthGoogleSDK: $result", "AuthGoogleSDK")
        return result

    }

}