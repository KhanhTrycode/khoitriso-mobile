package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Review

data class ReviewDto(
    val Id: Int,
    val UserId: Int,
    val Username: String?,
    val FullName: String?,
    val Avatar: String?,
    val ItemType: Int,
    val ItemId: Int,
    val Rating: Int,
    val ReviewTitle: String?,
    val ReviewContent: String?,
    val IsVerifiedPurchase: Boolean,
    val HelpfulCount: Int,
    val IsApproved: Boolean,
    val CreatedAt: String
)

fun ReviewDto.toDomain() = Review(
    id = Id,
    userId = UserId,
    username = Username,
    fullName = FullName,
    avatar = Avatar,
    itemType = ItemType,
    itemId = ItemId,
    rating = Rating,
    reviewTitle = ReviewTitle,
    reviewContent = ReviewContent,
    isVerifiedPurchase = IsVerifiedPurchase,
    helpfulCount = HelpfulCount,
    isApproved = IsApproved,
    createdAt = CreatedAt
)

data class ReviewPagedDto(
    val Page: Int,
    val PageSize: Int,
    val Total: Int,
    val AverageRating: Double,
    val RatingDistribution: Map<String, Int>?,
    val Data: List<ReviewDto>?
)

