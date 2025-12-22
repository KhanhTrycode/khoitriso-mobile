package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.AssignmentSubmission

data class AssignmentSubmissionDto(
    val Id: Int,
    val AssignmentId: Int,
    val UserId: Int,
    val UserFullName: String?,
    val UserAvatar: String?,
    val UserName: String?,
    val AttemptNumber: Int,
    val StartedAt: String,
    val CompletedAt: String?,
    val Score: Double?,
    val MaxScore: Double?,
    val IsCompleted: Boolean,
    val TimeSpent: String?,
    val Feedback: String?,
    val IsGraded: Boolean,
    val GradedAt: String?,
    val Answers: List<QuestionAnswerDto>?
)

fun AssignmentSubmissionDto.toDomain() = AssignmentSubmission(
    id = Id,
    assignmentId = AssignmentId,
    userId = UserId,
    userFullName = UserFullName,
    userAvatar = UserAvatar,
    userName = UserName,
    attemptNumber = AttemptNumber,
    startedAt = StartedAt,
    completedAt = CompletedAt,
    score = Score,
    maxScore = MaxScore,
    isCompleted = IsCompleted,
    timeSpent = TimeSpent,
    feedback = Feedback,
    isGraded = IsGraded,
    gradedAt = GradedAt,
    answers = Answers?.map { it.toDomain() } ?: emptyList()
)

data class QuestionAnswerDto(
    val Id: Int,
    val AttemptId: Int,
    val QuestionId: Int,
    val OptionId: Int?,
    val AnswerText: String?,
    val IsCorrect: Boolean,
    val PointsEarned: Double?,
    val MaxPoints: Double?,
    val AnsweredAt: String,
    val Feedback: String?
)

fun QuestionAnswerDto.toDomain() = com.example.khoitriso.domain.models.QuestionAnswer(
    id = Id,
    attemptId = AttemptId,
    questionId = QuestionId,
    optionId = OptionId,
    answerText = AnswerText,
    isCorrect = IsCorrect,
    pointsEarned = PointsEarned,
    maxPoints = MaxPoints,
    answeredAt = AnsweredAt,
    feedback = Feedback
)

data class AssignmentResultDto(
    val AssignmentId: Int,
    val AssignmentTitle: String,
    val TotalQuestions: Int,
    val TotalAttempts: Int,
    val MaxAttempts: Int,
    val BestScore: Double?,
    val AverageScore: Double?,
    val MaxScore: Double?,
    val IsPassed: Boolean,
    val PassingScore: Double?,
    val LastAttemptAt: String?,
    val Attempts: List<AssignmentSubmissionDto>?
)

fun AssignmentResultDto.toDomain() = com.example.khoitriso.domain.models.AssignmentResult(
    assignmentId = AssignmentId,
    assignmentTitle = AssignmentTitle,
    totalQuestions = TotalQuestions,
    totalAttempts = TotalAttempts,
    maxAttempts = MaxAttempts,
    bestScore = BestScore,
    averageScore = AverageScore,
    maxScore = MaxScore,
    isPassed = IsPassed,
    passingScore = PassingScore,
    lastAttemptAt = LastAttemptAt,
    attempts = Attempts?.map { it.toDomain() } ?: emptyList()
)

