package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.OrderDto
import com.example.khoitriso.data.dto.PaymentResultDto
import com.example.khoitriso.data.request.CreateOrderRequest
import com.example.khoitriso.data.request.PaymentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("orders/my-orders")
    suspend fun getOrder(
        @Query("status") status: Int,
        @Query("search") search: String? = null,
        @Query("page") page: Int =1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiResponeData<OrderDto>>

    @GET("orders/{id}")
    suspend fun getOrderById(
        @Path("id") orderId: Int
    ): Response<ApiRespone<OrderDto>>

}