package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.LessonDiscussionDto
import com.example.khoitriso.data.request.discussion.CreateLessonDiscussionReplyRequestDto
import com.example.khoitriso.data.request.discussion.CreateLessonDiscussionRequestDto
import com.example.khoitriso.data.request.discussion.UpdateLessonDiscussionRequestDto
import retrofit2.Response
import retrofit2.http.*

interface DiscussionsApi {
    // Lesson Discussions
    @GET("lessons/{lessonId}/discussions")
    suspend fun getLessonDiscussions(
        @Path("lessonId") lessonId: Int,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy") sortBy: String? = null, // "createdAt", "voteCount", "videoTimestamp"
        @Query("desc") desc: Boolean? = true,
        @Query("isResolved") isResolved: Boolean? = null,
        @Query("isPinned") isPinned: Boolean? = null
    ): Response<ApiResponeData<LessonDiscussionDto>>

    @GET("lessons/{lessonId}/discussions/{discussionId}")
    suspend fun getLessonDiscussionById(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String
    ): Response<ApiRespone<LessonDiscussionDto>>

    @POST("lessons/{lessonId}/discussions")
    suspend fun createLessonDiscussion(
        @Path("lessonId") lessonId: Int,
        @Body request: CreateLessonDiscussionRequestDto
    ): Response<ApiRespone<LessonDiscussionDto>>

    @PUT("lessons/{lessonId}/discussions/{discussionId}")
    suspend fun updateLessonDiscussion(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String,
        @Body request: UpdateLessonDiscussionRequestDto
    ): Response<ApiRespone<LessonDiscussionDto>>

    @DELETE("lessons/{lessonId}/discussions/{discussionId}")
    suspend fun deleteLessonDiscussion(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String
    ): Response<ApiRespone<Unit>>

    @POST("lessons/{lessonId}/discussions/{discussionId}/pin")
    suspend fun pinLessonDiscussion(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String
    ): Response<ApiRespone<Unit>>

    @POST("lessons/{lessonId}/discussions/{discussionId}/unpin")
    suspend fun unpinLessonDiscussion(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String
    ): Response<ApiRespone<Unit>>

    @POST("lessons/{lessonId}/discussions/{discussionId}/resolve")
    suspend fun resolveLessonDiscussion(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String
    ): Response<ApiRespone<Unit>>

    @POST("lessons/{lessonId}/discussions/{discussionId}/unresolve")
    suspend fun unresolveLessonDiscussion(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String
    ): Response<ApiRespone<Unit>>

    // Replies
    @GET("lessons/{lessonId}/discussions/{discussionId}/replies")
    suspend fun getLessonDiscussionReplies(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy") sortBy: String? = null, // "createdAt", "voteCount"
        @Query("desc") desc: Boolean? = true
    ): Response<ApiResponeData<LessonDiscussionDto>>

    @POST("lessons/{lessonId}/discussions/{discussionId}/replies")
    suspend fun createLessonDiscussionReply(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String,
        @Body request: CreateLessonDiscussionReplyRequestDto
    ): Response<ApiRespone<LessonDiscussionDto>>

    @PUT("lessons/{lessonId}/discussions/{discussionId}/replies/{replyId}")
    suspend fun updateLessonDiscussionReply(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String,
        @Path("replyId") replyId: String,
        @Body request: CreateLessonDiscussionReplyRequestDto
    ): Response<ApiRespone<LessonDiscussionDto>>

    @DELETE("lessons/{lessonId}/discussions/{discussionId}/replies/{replyId}")
    suspend fun deleteLessonDiscussionReply(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String,
        @Path("replyId") replyId: String
    ): Response<ApiRespone<Unit>>

    @POST("lessons/{lessonId}/discussions/{discussionId}/replies/{replyId}/accept")
    suspend fun acceptLessonDiscussionReply(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String,
        @Path("replyId") replyId: String
    ): Response<ApiRespone<Unit>>

    // Votes
    @POST("lessons/{lessonId}/discussions/{discussionId}/votes")
    suspend fun voteLessonDiscussion(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String,
        @Query("voteType") voteType: Int // 1 = upvote, -1 = downvote, 0 = remove vote
    ): Response<ApiRespone<Int>> // Returns new vote count

    @POST("lessons/{lessonId}/discussions/{discussionId}/replies/{replyId}/votes")
    suspend fun voteLessonDiscussionReply(
        @Path("lessonId") lessonId: Int,
        @Path("discussionId") discussionId: String,
        @Path("replyId") replyId: String,
        @Query("voteType") voteType: Int // 1 = upvote, -1 = downvote, 0 = remove vote
    ): Response<ApiRespone<Int>> // Returns new vote count
}
