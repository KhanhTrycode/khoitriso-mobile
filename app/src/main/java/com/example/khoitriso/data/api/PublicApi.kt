package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.auth.ResultAuth
import com.example.khoitriso.data.dto.request.GoogleAuthRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PublicApi {

    @POST("Auth/google/sdk")
    suspend fun authGoogleSDK(
        @Body IdToken: GoogleAuthRequest
    ): Response<ApiRespone<ResultAuth>>
}