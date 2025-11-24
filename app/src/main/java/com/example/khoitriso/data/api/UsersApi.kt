package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.UserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UsersApi {
    @GET("users/profile")
    suspend fun getProfile() : Response<ApiRespone<UserDTO>>

    @POST("users/upload-avatar")
    suspend fun uploadAvatar(
        @Body AvatarUrl : String
    )
}