package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.*

interface ForumRepository {
    // Questions
    suspend fun getQuestions(
        search: String? = null,
        categoryId: String? = null,
        tag: String? = null,
        isSolved: Boolean? = null,
        isPinned: Boolean? = null,
        page: Int = 1,
        pageSize: Int = 20,
        sortBy: String? = null,
        desc: Boolean? = null
    ): Result<ForumQuestionsResult>

    suspend fun getQuestionById(id: String): Result<ForumQuestion>
    suspend fun createQuestion(request: CreateQuestionRequest): Result<ForumQuestion>
    suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): Result<ForumQuestion>
    suspend fun deleteQuestion(id: String): Result<Unit>

    // Answers
    suspend fun getAnswers(questionId: String): Result<List<ForumAnswer>>
    suspend fun createAnswer(questionId: String, request: CreateAnswerRequest): Result<ForumAnswer>
    suspend fun updateAnswer(id: String, request: UpdateAnswerRequest): Result<ForumAnswer>
    suspend fun deleteAnswer(id: String): Result<Unit>
    suspend fun acceptAnswer(id: String): Result<Unit>
    suspend fun unacceptAnswer(id: String): Result<Unit>

    // Comments
    suspend fun getComments(parentType: Int, parentId: String): Result<List<ForumComment>>
    suspend fun createComment(request: CreateCommentRequest): Result<ForumComment>

    // Votes
    suspend fun vote(request: ForumVoteRequest): Result<Int> // Returns total vote count
    suspend fun getVotes(targetType: Int, targetId: String): Result<Int>
    suspend fun getUserVote(targetType: Int, targetId: String, userId: Int): Result<Int?> // -1, 0, 1

    // Bookmarks
    suspend fun addBookmark(questionId: String, userId: Int): Result<Unit>
    suspend fun removeBookmark(questionId: String, userId: Int): Result<Unit>
    suspend fun isBookmarked(questionId: String, userId: Int): Result<Boolean>
    suspend fun getBookmarks(userId: Int, page: Int = 1, pageSize: Int = 20): Result<ForumBookmarksResult>

    // Categories & Tags
    suspend fun getCategories(): Result<List<ForumCategory>>
    suspend fun getTags(limit: Int = 20): Result<List<ForumTag>>
    suspend fun getStats(): Result<ForumStats>
}

data class ForumQuestionsResult(
    val items: List<ForumQuestion>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)

data class ForumBookmarksResult(
    val items: List<ForumBookmarkItem>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)

data class ForumBookmarkItem(
    val questionId: String,
    val userId: Int,
    val question: ForumQuestion? = null,
    val createdAt: String
)

data class CreateQuestionRequest(
    val title: String,
    val content: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String? = null,
    val tags: List<String>? = null,
    val categoryId: String? = null,
    val categoryName: String? = null
)

data class UpdateQuestionRequest(
    val title: String? = null,
    val content: String? = null,
    val tags: List<String>? = null,
    val categoryId: String? = null,
    val categoryName: String? = null,
    val isPinned: Boolean? = null,
    val isClosed: Boolean? = null
)

data class CreateAnswerRequest(
    val content: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String? = null
)

data class UpdateAnswerRequest(
    val content: String? = null
)

data class CreateCommentRequest(
    val parentId: String,
    val parentType: Int, // 1 = Question, 2 = Answer
    val content: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String? = null
)

data class ForumVoteRequest(
    val targetId: String,
    val targetType: Int, // 1 = Question, 2 = Answer, 3 = Comment
    val userId: Int,
    val voteType: Int // -1 = downvote, 1 = upvote
)
