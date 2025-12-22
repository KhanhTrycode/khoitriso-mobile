package com.example.khoitriso.domain.request

data class AssignmentSubmissionRequest(
    val answers: List<QuestionAnswerRequest>
)

data class QuestionAnswerRequest(
    val questionId: Int,
    val optionId: Int?,
    val answerText: String?
)

