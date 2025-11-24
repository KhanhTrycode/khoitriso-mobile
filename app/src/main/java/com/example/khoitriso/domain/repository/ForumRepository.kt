package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.request.CreateAnswerRequest
import com.example.khoitriso.domain.request.CreateCommentRequest
import com.example.khoitriso.domain.request.CreateQuestionRequest
import com.example.khoitriso.domain.request.ForumBookmarksResult
import com.example.khoitriso.domain.request.ForumQuestions
import com.example.khoitriso.domain.request.ForumVoteRequest
import com.example.khoitriso.domain.request.UpdateAnswerRequest
import com.example.khoitriso.domain.request.UpdateQuestionRequest

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
    ): Result<MyResponese<ForumQuestion>>

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
    suspend fun getUserVote(targetType: Int, targetId: String, userId: Int): Result<Int> // -1, 0, 1

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
