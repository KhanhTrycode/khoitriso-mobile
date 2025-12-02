package com.example.khoitriso.domain.request

import com.example.khoitriso.domain.models.ForumQuestion


data class ForumQuestions(
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

data class UpdateCommentRequest(
    val content: String? = null
)

data class ForumVoteRequest(
    val targetId: String,
    val targetType: Int, // 1 = Question, 2 = Answer, 3 = Comment
    val userId: Int,
    val voteType: Int // -1 = downvote, 1 = upvote
)
