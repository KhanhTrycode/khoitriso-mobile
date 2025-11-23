package com.example.khoitriso.data.dto.forum

data class ForumQuestionDto(
    val Id: String,
    val Title: String,
    val Content: String,
    val UserId: Int,
    val UserName: String,
    val UserAvatar: String? = null,
    val CategoryId: String? = null,
    val CategoryName: String? = null,
    val Tags: List<String>? = null,
    val IsSolved: Boolean = false,
    val IsPinned: Boolean = false,
    val IsClosed: Boolean = false,
    val IsDeleted: Boolean = false,
    val ViewCount: Int = 0,
    val VoteCount: Int = 0,
    val AnswerCount: Int = 0,
    val AcceptedAnswerId: String? = null,
    val CreatedAt: String,
    val UpdatedAt: String? = null,
    val LastActivityAt: String? = null,
    val Attachments: List<ForumAttachmentDto>? = null
)
