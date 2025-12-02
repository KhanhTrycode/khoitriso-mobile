package com.example.khoitriso.domain.models

data class LessonDiscussion(
    val id: String,
    val lessonId: Int,
    val content: String,
    val videoTimestamp: Int, // Thời điểm trong video (giây)
    val userId: Int,
    val userName: String,
    val userAvatar: String,
    val replyCount: Int = 0,
    val voteCount: Int = 0,
    val isPinned: Boolean = false,
    val isResolved: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
    val replies: List<LessonDiscussionReply> = emptyList()
)

data class LessonDiscussionReply(
    val id: String,
    val discussionId: String,
    val content: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String,
    val voteCount: Int = 0,
    val isAccepted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

