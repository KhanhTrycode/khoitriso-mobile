package com.example.khoitriso.data.dto.request

data class AssignmentSubmissionRequest(
    val Answers: List<QuestionAnswerRequest>
)

data class QuestionAnswerRequest(
    val QuestionId: Int,
    val OptionId: Int?,
    val AnswerText: String?
)

