package com.example.khoitriso.data.dto.discussion

import com.example.khoitriso.domain.models.LessonDiscussionReply

data class LessonDiscussionReplyDto(
    val Id: String,
    val DiscussionId: String,
    val Content: String,
    val UserId: Int,
    val UserName: String,
    val UserAvatar: String,
    val VoteCount: Int = 0,
    val IsAccepted: Boolean = false,
    val CreatedAt: String,
    val UpdatedAt: String
)

fun LessonDiscussionReplyDto.toDomain() = LessonDiscussionReply(
    id = Id,
    discussionId = DiscussionId,
    content = Content,
    userId = UserId,
    userName = UserName,
    userAvatar = UserAvatar,
    voteCount = VoteCount,
    isAccepted = IsAccepted,
    createdAt = CreatedAt,
    updatedAt = UpdatedAt
)

