package com.example.khoitriso.domain.request

data class CreateReviewRequest(
    val itemType: Int,
    val itemId: Int,
    val rating: Int,
    val reviewTitle: String?,
    val reviewContent: String?
)

