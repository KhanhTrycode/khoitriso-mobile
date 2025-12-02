package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.WishlistItemDto
import com.example.khoitriso.data.request.AddToWishlistRequestDto
import retrofit2.Response
import retrofit2.http.*

interface WishlistApi {
    @GET("wishlist")
    suspend fun getWishlist(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("itemType") itemType: Int? = null // Filter by item type: 0 = Book, 1 = Course, 2 = LearningPath
    ): Response<ApiResponeData<WishlistItemDto>>

    @POST("wishlist")
    suspend fun addToWishlist(
        @Body request: AddToWishlistRequestDto
    ): Response<ApiRespone<WishlistItemDto>>

    @DELETE("wishlist/{id}")
    suspend fun removeFromWishlist(
        @Path("id") wishlistId: Int
    ): Response<ApiRespone<Unit>>

    @DELETE("wishlist/item/{itemId}")
    suspend fun removeItemFromWishlist(
        @Path("itemId") itemId: Int,
        @Query("itemType") itemType: Int
    ): Response<ApiRespone<Unit>>

    @GET("wishlist/check")
    suspend fun isInWishlist(
        @Query("itemId") itemId: Int,
        @Query("itemType") itemType: Int
    ): Response<ApiRespone<WishlistCheckResponse>>

    @DELETE("wishlist/clear")
    suspend fun clearWishlist(): Response<ApiRespone<Unit>>
}

data class WishlistCheckResponse(
    val IsInWishlist: Boolean,
    val WishlistId: Int?
)
