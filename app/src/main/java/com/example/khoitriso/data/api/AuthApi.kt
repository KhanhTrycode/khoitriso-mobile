package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.auth.RefreshResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body refreshRequest: String
    ): Response<RefreshResponse>
}


