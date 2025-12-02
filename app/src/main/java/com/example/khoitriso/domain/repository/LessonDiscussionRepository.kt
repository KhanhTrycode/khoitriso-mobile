package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.LessonDiscussion
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.request.CreateLessonDiscussionReplyRequest
import com.example.khoitriso.domain.request.CreateLessonDiscussionRequest
import com.example.khoitriso.domain.request.UpdateLessonDiscussionRequest

interface LessonDiscussionRepository {
    // Discussions
    suspend fun getLessonDiscussions(
        lessonId: Int,
        page: Int = 1,
        pageSize: Int = 20,
        sortBy: String? = null,
        desc: Boolean? = true,
        isResolved: Boolean? = null,
        isPinned: Boolean? = null
    ): Result<MyResponese<LessonDiscussion>>

    suspend fun getLessonDiscussionById(
        lessonId: Int,
        discussionId: String
    ): Result<LessonDiscussion>

    suspend fun createLessonDiscussion(
        lessonId: Int,
        request: CreateLessonDiscussionRequest
    ): Result<LessonDiscussion>

    suspend fun updateLessonDiscussion(
        lessonId: Int,
        discussionId: String,
        request: UpdateLessonDiscussionRequest
    ): Result<LessonDiscussion>

    suspend fun deleteLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit>

    suspend fun pinLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit>

    suspend fun unpinLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit>

    suspend fun resolveLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit>

    suspend fun unresolveLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit>

    // Replies
    suspend fun getLessonDiscussionReplies(
        lessonId: Int,
        discussionId: String,
        page: Int = 1,
        pageSize: Int = 20,
        sortBy: String? = null,
        desc: Boolean? = true
    ): Result<MyResponese<LessonDiscussion>>

    suspend fun createLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        request: CreateLessonDiscussionReplyRequest
    ): Result<LessonDiscussion>

    suspend fun updateLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        replyId: String,
        request: CreateLessonDiscussionReplyRequest
    ): Result<LessonDiscussion>

    suspend fun deleteLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        replyId: String
    ): Result<Unit>

    suspend fun acceptLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        replyId: String
    ): Result<Unit>

    // Votes
    suspend fun voteLessonDiscussion(
        lessonId: Int,
        discussionId: String,
        voteType: Int
    ): Result<Int>

    suspend fun voteLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        replyId: String,
        voteType: Int
    ): Result<Int>
}

