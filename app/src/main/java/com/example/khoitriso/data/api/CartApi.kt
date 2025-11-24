package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.ItemDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CartApi {

    @GET("cart")
    suspend fun getCart() :  Response<ApiResponeData<ItemDto>>

    @POST("cart/{id}")
    suspend fun addCartItem(
        @Path("id") itemId: Int
    ): Response<ApiResponeData<ItemDto>>

    @DELETE("cart/{id}")
    suspend fun deleteCartItem(
        @Path("id") itemId: Int
    ): Response<ApiResponeData<ItemDto>>
}