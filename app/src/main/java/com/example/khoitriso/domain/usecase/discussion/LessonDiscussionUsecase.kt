package com.example.khoitriso.domain.usecase.discussion

import com.example.khoitriso.domain.models.LessonDiscussion
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.LessonDiscussionRepository
import com.example.khoitriso.domain.request.CreateLessonDiscussionReplyRequest
import com.example.khoitriso.domain.request.CreateLessonDiscussionRequest
import com.example.khoitriso.domain.request.UpdateLessonDiscussionRequest

data class LessonDiscussionUsecase(
    val getLessonDiscussions: GetLessonDiscussions,
    val getLessonDiscussionById: GetLessonDiscussionById,
    val createLessonDiscussion: CreateLessonDiscussion,
    val updateLessonDiscussion: UpdateLessonDiscussion,
    val deleteLessonDiscussion: DeleteLessonDiscussion,
    val pinLessonDiscussion: PinLessonDiscussion,
    val unpinLessonDiscussion: UnpinLessonDiscussion,
    val resolveLessonDiscussion: ResolveLessonDiscussion,
    val unresolveLessonDiscussion: UnresolveLessonDiscussion,
    val getLessonDiscussionReplies: GetLessonDiscussionReplies,
    val createLessonDiscussionReply: CreateLessonDiscussionReply,
    val updateLessonDiscussionReply: UpdateLessonDiscussionReply,
    val deleteLessonDiscussionReply: DeleteLessonDiscussionReply,
    val acceptLessonDiscussionReply: AcceptLessonDiscussionReply,
    val voteLessonDiscussion: VoteLessonDiscussion,
    val voteLessonDiscussionReply: VoteLessonDiscussionReply
)

class GetLessonDiscussions(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        page: Int = 1,
        pageSize: Int = 20,
        sortBy: String? = null,
        desc: Boolean? = true,
        isResolved: Boolean? = null,
        isPinned: Boolean? = null
    ) = repo.getLessonDiscussions(lessonId, page, pageSize, sortBy, desc, isResolved, isPinned)
}

class GetLessonDiscussionById(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(lessonId: Int, discussionId: String) =
        repo.getLessonDiscussionById(lessonId, discussionId)
}

class CreateLessonDiscussion(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        request: CreateLessonDiscussionRequest
    ) = repo.createLessonDiscussion(lessonId, request)
}

class UpdateLessonDiscussion(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        discussionId: String,
        request: UpdateLessonDiscussionRequest
    ) = repo.updateLessonDiscussion(lessonId, discussionId, request)
}

class DeleteLessonDiscussion(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(lessonId: Int, discussionId: String) =
        repo.deleteLessonDiscussion(lessonId, discussionId)
}

class PinLessonDiscussion(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(lessonId: Int, discussionId: String) =
        repo.pinLessonDiscussion(lessonId, discussionId)
}

class UnpinLessonDiscussion(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(lessonId: Int, discussionId: String) =
        repo.unpinLessonDiscussion(lessonId, discussionId)
}

class ResolveLessonDiscussion(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(lessonId: Int, discussionId: String) =
        repo.resolveLessonDiscussion(lessonId, discussionId)
}

class UnresolveLessonDiscussion(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(lessonId: Int, discussionId: String) =
        repo.unresolveLessonDiscussion(lessonId, discussionId)
}

class GetLessonDiscussionReplies(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        discussionId: String,
        page: Int = 1,
        pageSize: Int = 20,
        sortBy: String? = null,
        desc: Boolean? = true
    ) = repo.getLessonDiscussionReplies(lessonId, discussionId, page, pageSize, sortBy, desc)
}

class CreateLessonDiscussionReply(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        discussionId: String,
        request: CreateLessonDiscussionReplyRequest
    ) = repo.createLessonDiscussionReply(lessonId, discussionId, request)
}

class UpdateLessonDiscussionReply(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        discussionId: String,
        replyId: String,
        request: CreateLessonDiscussionReplyRequest
    ) = repo.updateLessonDiscussionReply(lessonId, discussionId, replyId, request)
}

class DeleteLessonDiscussionReply(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        discussionId: String,
        replyId: String
    ) = repo.deleteLessonDiscussionReply(lessonId, discussionId, replyId)
}

class AcceptLessonDiscussionReply(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        discussionId: String,
        replyId: String
    ) = repo.acceptLessonDiscussionReply(lessonId, discussionId, replyId)
}

class VoteLessonDiscussion(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        discussionId: String,
        voteType: Int // 1 = upvote, -1 = downvote, 0 = remove vote
    ) = repo.voteLessonDiscussion(lessonId, discussionId, voteType)
}

class VoteLessonDiscussionReply(private val repo: LessonDiscussionRepository) {
    suspend operator fun invoke(
        lessonId: Int,
        discussionId: String,
        replyId: String,
        voteType: Int // 1 = upvote, -1 = downvote, 0 = remove vote
    ) = repo.voteLessonDiscussionReply(lessonId, discussionId, replyId, voteType)
}

