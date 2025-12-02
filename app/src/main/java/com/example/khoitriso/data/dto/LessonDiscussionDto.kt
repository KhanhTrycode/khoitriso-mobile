package com.example.khoitriso.data.dto

import com.example.khoitriso.data.dto.discussion.LessonDiscussionReplyDto
import com.example.khoitriso.data.dto.discussion.toDomain
import com.example.khoitriso.domain.models.LessonDiscussion

data class LessonDiscussionDto(
    val Id: String,
    val LessonId: Int,
    val Content: String,
    val VideoTimestamp: Int,
    val UserId: Int,
    val UserName: String,
    val UserAvatar: String,
    val ReplyCount: Int = 0,
    val VoteCount: Int = 0,
    val IsPinned: Boolean = false,
    val IsResolved: Boolean = false,
    val CreatedAt: String,
    val UpdatedAt: String,
    val Replies: List<LessonDiscussionReplyDto>? = null
)

fun LessonDiscussionDto.toDomain() = LessonDiscussion(
    id = Id,
    lessonId = LessonId,
    content = Content,
    videoTimestamp = VideoTimestamp,
    userId = UserId,
    userName = UserName,
    userAvatar = UserAvatar,
    replyCount = ReplyCount,
    voteCount = VoteCount,
    isPinned = IsPinned,
    isResolved = IsResolved,
    createdAt = CreatedAt,
    updatedAt = UpdatedAt,
    replies = Replies?.map { it.toDomain() } ?: emptyList()
)

