package com.example.khoitriso.data.dto

import com.example.khoitriso.data.dto.discussion.LessonDiscussionReplyDto
import com.example.khoitriso.data.dto.discussion.toDomain
import com.example.khoitriso.domain.models.LessonDiscussion
import com.google.gson.annotations.SerializedName

data class UserInfoDto(
    @SerializedName("FullName") val FullName: String?,
    @SerializedName("Avatar") val Avatar: String?
)

data class LessonDiscussionDto(
    val Id: String?,
    val LessonId: Int?,
    val Content: String?,
    val VideoTimestamp: Int?,
    val UserId: Int?,
    val User: UserInfoDto?,
    val ReplyCount: Int?,
    val VoteCount: Int?,
    val IsPinned: Boolean?,
    val IsResolved: Boolean?,
    val CreatedAt: String?,
    val UpdatedAt: String?,
    val Replies: List<LessonDiscussionReplyDto>?
)

fun LessonDiscussionDto.toDomain() = LessonDiscussion(
    id = Id ?: "",
    lessonId = LessonId ?: 0,
    content = Content ?: "",
    videoTimestamp = VideoTimestamp ?: 0,
    userId = UserId ?: 0,
    userName = User?.FullName ?: "Unknown",
    userAvatar = User?.Avatar ?: "",
    replyCount = ReplyCount ?: 0,
    voteCount = VoteCount ?: 0,
    isPinned = IsPinned ?: false,
    isResolved = IsResolved ?: false,
    createdAt = CreatedAt ?: "",
    updatedAt = UpdatedAt ?: "",
    replies = Replies?.map { it.toDomain() } ?: emptyList()
)

