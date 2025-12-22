package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Review
import com.example.khoitriso.domain.request.CreateReviewRequest
import com.example.khoitriso.domain.request.UpdateReviewRequest

interface ReviewRepository {
    suspend fun getReviews(
        itemType: Int,
        itemId: Int,
        rating: Int? = null,
        page: Int = 1,
        pageSize: Int = 20,
        sortBy: String? = null,
        sortOrder: String? = null
    ): Result<Pair<List<Review>, ReviewPagedInfo>>

    suspend fun createReview(request: CreateReviewRequest): Result<Review>
    suspend fun updateReview(id: Int, request: UpdateReviewRequest): Result<Review>
    suspend fun deleteReview(id: Int): Result<Unit>
    suspend fun markHelpful(id: Int): Result<Review>
}

data class ReviewPagedInfo(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val averageRating: Double,
    val ratingDistribution: Map<Int, Int>
)

