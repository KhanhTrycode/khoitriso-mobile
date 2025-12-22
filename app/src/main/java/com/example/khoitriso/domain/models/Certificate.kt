package com.example.khoitriso.domain.models

data class Certificate(
    val id: Int,
    val userId: Int,
    val itemType: Int,
    val itemId: Int,
    val certificateNumber: String,
    val completionPercentage: Double,
    val finalScore: Double?,
    val issuedAt: String,
    val certificateUrl: String?,
    val isValid: Boolean,
    val userFullName: String?,
    val itemTitle: String?,
    val instructorName: String?
)

