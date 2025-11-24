package com.example.khoitriso.data.dto.forum

data class ForumAnswerDto(
    val Id: String,
    val QuestionId: String,
    val Content: String,
    val UserId: Int,
    val UserName: String,
    val UserAvatar: String? = null,
    val IsAccepted: Boolean = false,
    val IsDeleted: Boolean = false,
    val VoteCount: Int = 0,
    val CommentCount: Int = 0,
    val CreatedAt: String,
    val UpdatedAt: String? = null,
    val Attachments: List<ForumAttachmentDto>? = null
)
