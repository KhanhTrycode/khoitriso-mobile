package com.example.khoitriso.domain.request

data class CreateLessonDiscussionRequest(
    val content: String,
    val videoTimestamp: Int
)

data class CreateLessonDiscussionReplyRequest(
    val content: String
)

data class UpdateLessonDiscussionRequest(
    val content: String
)

