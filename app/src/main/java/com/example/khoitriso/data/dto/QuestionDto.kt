package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Question

data class QuestionDto(
    val Id: Int?
)

fun QuestionDto.toDomain() = Question(
    id = Id?: 1
)