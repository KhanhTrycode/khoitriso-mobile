package com.example.khoitriso.data.dto.forum

data class ForumCommentDto(
    val Id: String,
    val ParentId: String,
    val ParentType: Int, // 1 = Question, 2 = Answer
    val Content: String,
    val UserId: Int,
    val UserName: String,
    val UserAvatar: String? = null,
    val CreatedAt: String,
    val UpdatedAt: String? = null
)
