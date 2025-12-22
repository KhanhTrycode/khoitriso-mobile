package com.example.khoitriso.domain.request

data class UpdateReviewRequest(
    val rating: Int,
    val reviewTitle: String?,
    val reviewContent: String?
)

