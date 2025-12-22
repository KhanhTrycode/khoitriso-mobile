package com.example.khoitriso.data.dto.request

data class CreateReviewRequest(
    val ItemType: Int,
    val ItemId: Int,
    val Rating: Int,
    val ReviewTitle: String?,
    val ReviewContent: String?
)

