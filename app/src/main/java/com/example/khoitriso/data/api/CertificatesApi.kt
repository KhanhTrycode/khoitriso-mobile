package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.CertificateDto
import com.example.khoitriso.data.dto.request.CreateCertificateRequest
import retrofit2.Response
import retrofit2.http.*

interface CertificatesApi {
    @GET("certificates")
    suspend fun getMyCertificates(
        @Query("itemType") itemType: Int? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponeData<CertificateDto>>

    @GET("certificates/{id}")
    suspend fun getCertificateById(
        @Path("id") id: Int
    ): Response<ApiRespone<CertificateDto>>

    @POST("certificates")
    suspend fun generateCertificate(
        @Body request: CreateCertificateRequest
    ): Response<ApiRespone<CertificateDto>>

    @GET("certificates/{id}/download")
    suspend fun getCertificateDownloadUrl(
        @Path("id") id: Int
    ): Response<ApiRespone<String>>

    @GET("certificates/verify/{number}")
    suspend fun verifyCertificate(
        @Path("number") number: String
    ): Response<ApiRespone<CertificateDto>>
}