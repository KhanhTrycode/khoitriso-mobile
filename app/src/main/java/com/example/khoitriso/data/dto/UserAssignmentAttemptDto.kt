package com.example.khoitriso.data.dto

data class UserAssignmentAttemptDto(
    val Id: Int,
    val AssignmentId: Int,
    val UserId: Int,
    val AttemptNumber: Int,
    val StartedAt: String,
    val CompletedAt: String?,
    val Score: Double?,
    val IsCompleted: Boolean,
    val TimeSpent: String?,
    val Answers: List<UserAssignmentAnswerDto>?
)

data class UserAssignmentAnswerDto(
    val Id: Int,
    val AttemptId: Int,
    val QuestionId: Int,
    val SelectedOptionId: Int?,
    val AnswerText: String?,
    val IsCorrect: Boolean,
    val PointsEarned: Double?,
    val AnsweredAt: String
)
