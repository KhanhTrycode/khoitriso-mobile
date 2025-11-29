package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.UserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.khoitriso.data.dto.auth.ResultAuth
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AuthApi {
    @POST("Auth/refresh")
    suspend fun refreshToken(
        @Query("refreshToken") refreshToken: String,
        @Header("Authorization") accessToken: String, // <--- header Authorization
    ): Response<ApiRespone<ResultAuth>>

    @GET(
        "Auth/me"
    )
    suspend fun getMe() : Response<ApiRespone<UserDTO>>
}


