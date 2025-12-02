package com.example.khoitriso.data.dto.forum

data class CreateQuestionRequestDto(
    val Title: String,
    val Content: String,
    val UserId: Int,
    val UserName: String,
    val UserAvatar: String? = null,
    val Tags: List<String>? = null,
    val CategoryId: String? = null,
    val CategoryName: String? = null
)

data class UpdateQuestionRequestDto(
    val Title: String? = null,
    val Content: String? = null,
    val UserName: String? = null,
    val UserAvatar: String? = null,
    val Tags: List<String>? = null,
    val CategoryId: String? = null,
    val CategoryName: String? = null,
    val IsPinned: Boolean? = null,
    val IsClosed: Boolean? = null
)

data class CreateAnswerRequestDto(
    val Content: String,
    val UserId: Int,
    val UserName: String,
    val UserAvatar: String? = null
)

data class UpdateAnswerRequestDto(
    val Content: String? = null,
    val UserName: String? = null,
    val UserAvatar: String? = null,
    val Attachments: List<ForumAttachmentDto>? = null
)

data class CreateCommentRequestDto(
    val ParentId: String,
    val ParentType: Int, // 1 = Question, 2 = Answer
    val Content: String,
    val UserId: Int,
    val UserName: String,
    val UserAvatar: String? = null
)

data class UpdateCommentRequestDto(
    val Content: String? = null
)

data class ForumVoteRequestDto(
    val TargetId: String,
    val TargetType: Int, // 1 = Question, 2 = Answer, 3 = Comment
    val UserId: Int,
    val VoteType: Int // -1 = downvote, 1 = upvote
)

data class ForumVoteResponse(
    val Total: Int
)

data class ForumUserVoteResponse(
    val VoteType: Int? // -1, 0, 1
)

data class ForumBookmarkRequestDto(
    val QuestionId: String,
    val UserId: Int
)
data class ForumBookmarkedResponse(
    val IsBookmarked: Boolean
)

data class ForumQuestionsResponse(
    val Items: List<ForumQuestionDto>? = null,
    val Total: Int = 0,
    val Page: Int = 1,
    val PageSize: Int = 20,
    val TotalPages: Int = 1
)

data class ForumBookmarksResponse(
    val Items: List<ForumBookmarkDto>? = null,
    val Total: Int = 0,
    val Page: Int = 1,
    val PageSize: Int = 20,
    val TotalPages: Int = 1
)

data class ForumBookmarkDto(
    val QuestionId: String,
    val UserId: Int,
    val Question: ForumQuestionDto? = null,
    val CreatedAt: String
)

data class ForumStatsDto(
    val TotalQuestions: Int = 0,
    val TotalAnswers: Int = 0,
    val TotalUsers: Int = 0,
    val SolvedQuestions: Int = 0
)

