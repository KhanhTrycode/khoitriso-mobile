package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.auth.GoogleAuthRequest
import com.example.khoitriso.data.dto.auth.ResultAuth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PublicApi {

    @POST("Auth/google/sdk")
    suspend fun authGoogleSDK(
        @Body IdToken: GoogleAuthRequest
    ): Response<ApiRespone<ResultAuth>>
}