package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.CreateOrderRequest
import com.example.khoitriso.data.dto.OrderDto
import com.example.khoitriso.data.dto.PaymentRequest
import com.example.khoitriso.data.dto.PaymentResultDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrdersApi {
    @POST("orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Response<ApiRespone<OrderDto>>

    @POST("orders/{id}/payment")
    suspend fun processPayment(
        @Path("id") orderId: Int,
        @Body request: PaymentRequest
    ): Response<ApiRespone<PaymentResultDto>>
}