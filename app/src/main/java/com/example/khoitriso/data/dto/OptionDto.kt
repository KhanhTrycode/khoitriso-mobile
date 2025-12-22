package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Option

data class OptionDto(
    val CreatedAt: String,
    val Id: Int,
    val OptionText: String?, // Có thể null
    val OrderIndex: Int,
    val PointsValue: Int,
    val QuestionId: Int,
)

fun OptionDto.toDomain() = Option(
    id = Id,
    optionText = OptionText ?: "", // Xử lý null bằng empty string
    orderIndex = OrderIndex,
    pointsValue = PointsValue,
    questionId = QuestionId
)