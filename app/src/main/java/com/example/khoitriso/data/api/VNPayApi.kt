package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.VNPayPaymentResponse
import com.example.khoitriso.data.dto.VNPayQueryResponse
import com.example.khoitriso.data.request.VNPayPaymentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VNPayApi {
    @POST("vnpay/create-payment")
    suspend fun createPaymentUrl(
        @Body request: VNPayPaymentRequest
    ): Response<ApiRespone<VNPayPaymentResponse>>

    @GET("vnpay/query-transaction")
    suspend fun queryTransaction(
        @Query("orderCode") orderCode: String
    ): Response<ApiRespone<VNPayQueryResponse>>
}

