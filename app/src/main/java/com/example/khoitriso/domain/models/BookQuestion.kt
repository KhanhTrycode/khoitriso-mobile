package com.example.khoitriso.domain.models

/**
 * Domain model for Book Question with full details
 */
data class BookQuestion(
    val id: Int,
    val contextType: Int,
    val contextId: Int,
    val questionContent: String,
    val questionType: Int,
    val difficultyLevel: Int,
    val defaultPoints: Double,
    val explanationContent: String?,
    val questionImage: String?,
    val videoUrl: String?,
    val timeLimit: Int?,
    val subjectType: String?,
    val orderIndex: Int,
    val isActive: Boolean,
    val chapterId: Int?,
    val chapterTitle: String?,
    val createdAt: String,
    val updatedAt: String?,
    val options: List<QuestionOption>
)

data class QuestionOption(
    val id: Int,
    val questionId: Int,
    val optionText: String,
    val isCorrect: Boolean,
    val pointsValue: Double,
    val orderIndex: Int,
    val createdAt: String,
    val updatedAt: String?
)
