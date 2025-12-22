package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Certificate

data class CertificateDto(
    val Id: Int,
    val UserId: Int,
    val ItemType: Int,
    val ItemId: Int,
    val CertificateNumber: String,
    val CompletionPercentage: Double,
    val FinalScore: Double?,
    val IssuedAt: String,
    val CertificateUrl: String?,
    val IsValid: Boolean,
    val UserFullName: String?,
    val ItemTitle: String?,
    val InstructorName: String?
)

fun CertificateDto.toDomain() = Certificate(
    id = Id,
    userId = UserId,
    itemType = ItemType,
    itemId = ItemId,
    certificateNumber = CertificateNumber,
    completionPercentage = CompletionPercentage,
    finalScore = FinalScore,
    issuedAt = IssuedAt,
    certificateUrl = CertificateUrl,
    isValid = IsValid,
    userFullName = UserFullName,
    itemTitle = ItemTitle,
    instructorName = InstructorName
)

