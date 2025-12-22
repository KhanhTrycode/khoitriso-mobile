package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.BookQuestion

/**
 * DTO for Book Question with full details from Solutions API
 * Endpoint: GET /api/solutions?questionId={id}
 */
data class BookQuestionDto(
    val Id: Int,
    val ContextType: Int,
    val ContextId: Int,
    val QuestionContent: String,
    val QuestionType: Int,
    val DifficultyLevel: Int,
    val DefaultPoints: Double,
    val ExplanationContent: String?,
    val QuestionImage: String?,
    val VideoUrl: String?,
    val TimeLimit: Int?,
    val SubjectType: String?,
    val OrderIndex: Int,
    val IsActive: Boolean,
    val ChapterId: Int?,
    val ChapterTitle: String?,
    val CreatedAt: String,
    val UpdatedAt: String?,
    val Options: List<QuestionOptionDto>
)

data class QuestionOptionDto(
    val Id: Int,
    val QuestionId: Int,
    val OptionText: String,
    val IsCorrect: Boolean,
    val PointsValue: Double,
    val OrderIndex: Int,
    val CreatedAt: String,
    val UpdatedAt: String?
)

fun BookQuestionDto.toDomain() = BookQuestion(
    id = Id,
    contextType = ContextType,
    contextId = ContextId,
    questionContent = QuestionContent,
    questionType = QuestionType,
    difficultyLevel = DifficultyLevel,
    defaultPoints = DefaultPoints,
    explanationContent = ExplanationContent,
    questionImage = QuestionImage,
    videoUrl = VideoUrl,
    timeLimit = TimeLimit,
    subjectType = SubjectType,
    orderIndex = OrderIndex,
    isActive = IsActive,
    chapterId = ChapterId,
    chapterTitle = ChapterTitle,
    createdAt = CreatedAt,
    updatedAt = UpdatedAt,
    options = Options.map { it.toDomain() }
)

fun QuestionOptionDto.toDomain() = com.example.khoitriso.domain.models.QuestionOption(
    id = Id,
    questionId = QuestionId,
    optionText = OptionText,
    isCorrect = IsCorrect,
    pointsValue = PointsValue,
    orderIndex = OrderIndex,
    createdAt = CreatedAt,
    updatedAt = UpdatedAt
)
