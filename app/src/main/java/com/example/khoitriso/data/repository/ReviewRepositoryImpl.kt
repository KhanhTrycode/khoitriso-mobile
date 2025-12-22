package com.example.khoitriso.data.repository

import android.content.Context
import com.example.khoitriso.data.api.ReviewsApi
import com.example.khoitriso.data.dto.request.CreateReviewRequest
import com.example.khoitriso.data.dto.request.UpdateReviewRequest
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Review
import com.example.khoitriso.domain.repository.ReviewPagedInfo
import com.example.khoitriso.domain.repository.ReviewRepository
import com.example.khoitriso.utils.ErrorMessageHelper
import com.example.khoitriso.utils.ErrorType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewsApi: ReviewsApi,
    @ApplicationContext private val context: Context
) : ReviewRepository {

    override suspend fun getReviews(
        itemType: Int,
        itemId: Int,
        rating: Int?,
        page: Int,
        pageSize: Int,
        sortBy: String?,
        sortOrder: String?
    ): Result<Pair<List<Review>, ReviewPagedInfo>> {
        return try {
            val response = reviewsApi.getReviews(
                itemType = itemType,
                itemId = itemId,
                rating = rating,
                page = page,
                pageSize = pageSize,
                sortBy = sortBy,
                sortOrder = sortOrder
            )
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    val reviews = body.Data?.map { it.toDomain() } ?: emptyList()
                    val pagedInfo = ReviewPagedInfo(
                        page = body.Page,
                        pageSize = body.PageSize,
                        total = body.Total,
                        averageRating = body.AverageRating,
                        ratingDistribution = body.RatingDistribution?.mapKeys { it.key.toIntOrNull() ?: 0 }?.mapValues { it.value } ?: emptyMap()
                    )
                    Result.success(Pair(reviews, pagedInfo))
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createReview(request: com.example.khoitriso.domain.request.CreateReviewRequest): Result<Review> {
        return try {
            val dtoRequest = CreateReviewRequest(
                ItemType = request.itemType,
                ItemId = request.itemId,
                Rating = request.rating,
                ReviewTitle = request.reviewTitle,
                ReviewContent = request.reviewContent
            )
            val response = reviewsApi.createReview(dtoRequest)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReview(id: Int, request: com.example.khoitriso.domain.request.UpdateReviewRequest): Result<Review> {
        return try {
            val dtoRequest = UpdateReviewRequest(
                Rating = request.rating,
                ReviewTitle = request.reviewTitle,
                ReviewContent = request.reviewContent
            )
            val response = reviewsApi.updateReview(id, dtoRequest)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(id: Int): Result<Unit> {
        return try {
            val response = reviewsApi.deleteReview(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markHelpful(id: Int): Result<Review> {
        return try {
            val response = reviewsApi.markHelpful(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

