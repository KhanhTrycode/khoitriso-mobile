package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.ReviewDto
import com.example.khoitriso.data.dto.ReviewPagedDto
import com.example.khoitriso.data.dto.request.CreateReviewRequest
import com.example.khoitriso.data.dto.request.UpdateReviewRequest
import retrofit2.Response
import retrofit2.http.*

interface ReviewsApi {
    @GET("reviews")
    suspend fun getReviews(
        @Query("itemType") itemType: Int,
        @Query("itemId") itemId: Int,
        @Query("rating") rating: Int? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null
    ): Response<ApiRespone<ReviewPagedDto>>

    @POST("reviews")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): Response<ApiRespone<ReviewDto>>

    @PUT("reviews/{id}")
    suspend fun updateReview(
        @Path("id") id: Int,
        @Body request: UpdateReviewRequest
    ): Response<ApiRespone<ReviewDto>>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(
        @Path("id") id: Int
    ): Response<ApiRespone<Unit>>

    @POST("reviews/{id}/helpful")
    suspend fun markHelpful(
        @Path("id") id: Int
    ): Response<ApiRespone<ReviewDto>>
}