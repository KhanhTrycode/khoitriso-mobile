package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.AddToCartRequest
import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.CartDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CartApi {

    @GET("cart")
    suspend fun getCart(): Response<ApiRespone<CartDto>>

    @POST("cart")
    suspend fun addToCart(
        @Body request: AddToCartRequest
    ): Response<ApiRespone<CartDto>>

    @DELETE("cart/{id}")
    suspend fun removeFromCart(
        @Path("id") cartId: Int
    ): Response<ApiRespone<Boolean>>

    @DELETE("cart/clear")
    suspend fun clearCart(): Response<ApiRespone<Boolean>>
}