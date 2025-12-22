package com.example.khoitriso.domain.models

data class Review(
    val id: Int,
    val userId: Int,
    val username: String?,
    val fullName: String?,
    val avatar: String?,
    val itemType: Int,
    val itemId: Int,
    val rating: Int,
    val reviewTitle: String?,
    val reviewContent: String?,
    val isVerifiedPurchase: Boolean,
    val helpfulCount: Int,
    val isApproved: Boolean,
    val createdAt: String
)

