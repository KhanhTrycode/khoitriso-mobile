package com.example.khoitriso.domain.models

data class AssignmentSubmission(
    val id: Int,
    val assignmentId: Int,
    val userId: Int,
    val userFullName: String?,
    val userAvatar: String?,
    val userName: String?,
    val attemptNumber: Int,
    val startedAt: String,
    val completedAt: String?,
    val score: Double?,
    val maxScore: Double?,
    val isCompleted: Boolean,
    val timeSpent: String?,
    val feedback: String?,
    val isGraded: Boolean,
    val gradedAt: String?,
    val answers: List<QuestionAnswer>
)

data class QuestionAnswer(
    val id: Int,
    val attemptId: Int,
    val questionId: Int,
    val optionId: Int?,
    val answerText: String?,
    val isCorrect: Boolean,
    val pointsEarned: Double?,
    val maxPoints: Double?,
    val answeredAt: String,
    val feedback: String?
)

data class AssignmentResult(
    val assignmentId: Int,
    val assignmentTitle: String,
    val totalQuestions: Int,
    val totalAttempts: Int,
    val maxAttempts: Int,
    val bestScore: Double?,
    val averageScore: Double?,
    val maxScore: Double?,
    val isPassed: Boolean,
    val passingScore: Double?,
    val lastAttemptAt: String?,
    val attempts: List<AssignmentSubmission>
)

