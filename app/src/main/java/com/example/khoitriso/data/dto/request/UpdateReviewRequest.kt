package com.example.khoitriso.data.dto.request

data class UpdateReviewRequest(
    val Rating: Int,
    val ReviewTitle: String?,
    val ReviewContent: String?
)

