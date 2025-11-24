package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.forum.*
import retrofit2.Response
import retrofit2.http.*

interface ForumApi {
    // Questions
    @GET("forum/questions")
    suspend fun getQuestions(
        @Query("search") search: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("tag") tag: String? = null,
        @Query("isSolved") isSolved: Boolean? = null,
        @Query("isPinned") isPinned: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy") sortBy: String? = null,
        @Query("desc") desc: Boolean? = null
    ): Response<ApiResponeData<ForumQuestionDto>>

    @GET("forum/questions/{id}")
    suspend fun getQuestionById(@Path("id") id: String): Response<ApiRespone<ForumQuestionDto>>

    @POST("forum/questions")
    suspend fun createQuestion(@Body request: CreateQuestionRequestDto): Response<ApiRespone<ForumQuestionDto>>

    @PUT("forum/questions/{id}")
    suspend fun updateQuestion(
        @Path("id") id: String,
        @Body request: UpdateQuestionRequestDto
    ): Response<ApiRespone<ForumQuestionDto>>

    @DELETE("forum/questions/{id}")
    suspend fun deleteQuestion(@Path("id") id: String): Response<ApiRespone<Unit>>

    // Answers
    @GET("forum/questions/{questionId}/answers")
    suspend fun getAnswers(@Path("questionId") questionId: String): Response<ApiRespone<List<ForumAnswerDto>>>

    @POST("forum/questions/{questionId}/answers")
    suspend fun createAnswer(
        @Path("questionId") questionId: String,
        @Body request: CreateAnswerRequestDto
    ): Response<ApiRespone<ForumAnswerDto>>

    @PUT("forum/answers/{id}")
    suspend fun updateAnswer(
        @Path("id") id: String,
        @Body request: UpdateAnswerRequestDto
    ): Response<ApiRespone<ForumAnswerDto>>

    @DELETE("forum/answers/{id}")
    suspend fun deleteAnswer(@Path("id") id: String): Response<ApiRespone<Unit>>

    @POST("forum/answers/{id}/accept")
    suspend fun acceptAnswer(@Path("id") id: String): Response<ApiRespone<Unit>>

    @POST("forum/answers/{id}/unaccept")
    suspend fun unacceptAnswer(@Path("id") id: String): Response<ApiRespone<Unit>>

    // Comments
    @GET("forum/{parentType}/{parentId}/comments")
    suspend fun getComments(
        @Path("parentType") parentType: Int, // 1 = Question, 2 = Answer
        @Path("parentId") parentId: String
    ): Response<ApiRespone<List<ForumCommentDto>>>

    @POST("forum/comments")
    suspend fun createComment(@Body request: CreateCommentRequestDto): Response<ApiRespone<ForumCommentDto>>

    // Votes
    @POST("forum/votes")
    suspend fun vote(@Body request: ForumVoteRequestDto): Response<ApiRespone<ForumVoteResponse>>

    @GET("forum/{targetType}/{targetId}/votes")
    suspend fun getVotes(
        @Path("targetType") targetType: Int,
        @Path("targetId") targetId: String
    ): Response<ApiRespone<ForumVoteResponse>>

    @GET("forum/{targetType}/{targetId}/user-vote")
    suspend fun getUserVote(
        @Path("targetType") targetType: Int,
        @Path("targetId") targetId: String,
        @Query("userId") userId: Int
    ): Response<ApiRespone<ForumUserVoteResponse>>

    // Bookmarks
    @POST("forum/bookmarks")
    suspend fun addBookmark(@Body request: ForumBookmarkRequestDto): Response<ApiRespone<Unit>>

    @DELETE("forum/bookmarks/{questionId}")
    suspend fun removeBookmark(
        @Path("questionId") questionId: String,
        @Query("userId") userId: Int
    ): Response<ApiRespone<Unit>>

    @GET("forum/questions/{questionId}/bookmarked")
    suspend fun isBookmarked(
        @Path("questionId") questionId: String,
        @Query("userId") userId: Int
    ): Response<ApiRespone<ForumBookmarkedResponse>>

    @GET("forum/bookmarks")
    suspend fun getBookmarks(
        @Query("userId") userId: Int,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiRespone<ForumBookmarksResponse>>

    // Categories
    @GET("forum/categories")
    suspend fun getCategories(): Response<ApiRespone<List<ForumCategoryDto>>>

    // Tags
    @GET("forum/tags")
    suspend fun getTags(@Query("limit") limit: Int = 20): Response<ApiRespone<List<ForumTagDto>>>

    // Stats
    @GET("forum/stats")
    suspend fun getStats(): Response<ApiRespone<ForumStatsDto>>
}
