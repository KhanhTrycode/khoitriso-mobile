package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Question

data class QuestionDto(
    val ContextId: Int,
    val ContextType: Int,
    val CreatedAt: String,
    val DefaultPoints: Double,
    val DifficultyLevel: Int,
    val Id: Int,
    val IsActive: Boolean,
    val Options: List<OptionDto>,
    val OrderIndex: Int,
    val QuestionContent: String,
    val QuestionType: Int,
    val UpdatedAt: String,
)

fun QuestionDto.toDomain() = Question(
    contextId = ContextId,
    contextType = ContextType,
    createdAt = CreatedAt,
    defaultPoints = DefaultPoints,
    difficultyLevel = DifficultyLevel,
    isActive = IsActive,
    options = Options.map { it.toDomain() },
    orderIndex = OrderIndex,
    questionContent = QuestionContent,
    questionType = QuestionType,
    updatedAt = UpdatedAt,
    id = Id
)
