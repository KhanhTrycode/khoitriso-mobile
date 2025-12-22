package com.example.khoitriso.domain.models


data class Assignment(
    val description: String,
    val dueDate: String,
    val id: Int,
    val isPublished: Boolean,
    val lessonId: Int,
    val maxAttempts: Int,
    val maxScore: Int,
    val passingScore: Int,
    val questions: List<Question>,
    val showAnswersAfter: Int,
    val shuffleOptions: Boolean,
    val shuffleQuestions: Boolean,
    val timeLimit: Int,
    val title: String,
    val attemptCount: Int = 0  // Số lần đã nộp của user hiện tại
)
