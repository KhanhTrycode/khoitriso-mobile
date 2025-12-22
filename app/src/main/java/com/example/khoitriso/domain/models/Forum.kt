package com.example.khoitriso.domain.models

data class ForumQuestion(
    val id: String,
    val title: String,
    val content: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String,
    val categoryId: String,
    val categoryName: String,
    val tags: List<String> = emptyList(),
    val isSolved: Boolean = false,
    val isPinned: Boolean = false,
    val isClosed: Boolean = false,
    val isDeleted: Boolean = false,
    val viewCount: Int = 0,
    val voteCount: Int = 0,
    val answerCount: Int = 0,
    val acceptedAnswerId: String,
    val createdAt: String,
    val updatedAt: String,
    val lastActivityAt: String,
    val attachments: List<ForumAttachment> = emptyList()
)

data class ForumAnswer(
    val id: String,
    val questionId: String,
    val content: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String = "",
    val isAccepted: Boolean = false,
    val isDeleted: Boolean = false,
    val voteCount: Int = 0,
    val commentCount: Int = 0,
    val createdAt: String,
    val updatedAt: String,
    val attachments: List<ForumAttachment> = emptyList()
)

data class ForumComment(
    val id: String,
    val parentId: String,
    val parentType: Int, // 1 = Question, 2 = Answer
    val content: String,
    val userId: Int,
    val userName: String,
    val userAvatar: String,
    val createdAt: String,
    val updatedAt: String
)

data class ForumCategory(
    val id: String,
    val name: String,
    val description: String,

)

data class ForumTag(
    val id: String,
    val name: String,
    val description: String,
    val color: String,
    val isActive: Boolean = true
)

data class ForumAttachment(
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long,
    val fileType: String
)

data class ForumStats(
    val totalQuestions: Int = 0,
    val totalAnswers: Int = 0,
    val totalUsers: Int = 0,
    val solvedQuestions: Int = 0
)
