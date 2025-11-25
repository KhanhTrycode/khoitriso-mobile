package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.UserDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UsersApi {
    @GET("users/profile")
    suspend fun getProfile() : Response<ApiRespone<UserDTO>>

    @PUT("user/update-profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ApiRespone<UserDTO>>

    @Multipart
    @POST("user/upload-avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): Response<ApiRespone<UserDTO>>
}

data class UpdateProfileRequest(
    val FullName: String,
    val Email: String
)