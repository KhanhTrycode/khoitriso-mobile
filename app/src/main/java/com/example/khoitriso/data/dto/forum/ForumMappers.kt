package com.example.khoitriso.data.dto.forum

import com.example.khoitriso.domain.models.*

fun ForumQuestionDto.toDomain(): ForumQuestion {
    return ForumQuestion(
        id = Id,
        title = Title,
        content = Content,
        userId = UserId,
        userName = UserName,
        userAvatar = UserAvatar?: "",
        categoryId = CategoryId?: "Unknown",
        categoryName = CategoryName?: "Unknown",
        tags = Tags ?: emptyList(),
        isSolved = IsSolved,
        isPinned = IsPinned,
        isClosed = IsClosed,
        isDeleted = IsDeleted,
        viewCount = ViewCount,
        voteCount = VoteCount,
        answerCount = AnswerCount,
        acceptedAnswerId = AcceptedAnswerId?: "Unknown",
        createdAt = CreatedAt,
        updatedAt = UpdatedAt?: "",
        lastActivityAt = LastActivityAt?: "",
        attachments = Attachments?.map { it.toDomain() } ?: emptyList()
    )
}

fun ForumAnswerDto.toDomain(): ForumAnswer {
    return ForumAnswer(
        id = Id,
        questionId = QuestionId,
        content = Content,
        userId = UserId,
        userName = UserName,
        userAvatar = UserAvatar?: "",
        isAccepted = IsAccepted,
        isDeleted = IsDeleted,
        voteCount = VoteCount,
        commentCount = CommentCount,
        createdAt = CreatedAt,
        updatedAt = UpdatedAt?: "",
        attachments = Attachments?.map { it.toDomain() } ?: emptyList()
    )
}

fun ForumCommentDto.toDomain(): ForumComment {
    return ForumComment(
        id = Id,
        parentId = ParentId,
        parentType = ParentType,
        content = Content,
        userId = UserId,
        userName = UserName,
        userAvatar = UserAvatar?: "",
        createdAt = CreatedAt,
        updatedAt = UpdatedAt?: ""
    )
}

fun ForumCategoryDto.toDomain(): ForumCategory {
    return ForumCategory(
        id = Id,
        name = Name,
        description = Description?: "",
        color = Color?: "",
        icon = Icon?: "",
        isActive = IsActive,
        sortOrder = SortOrder
    )
}

fun ForumTagDto.toDomain(): ForumTag {
    return ForumTag(
        id = Id,
        name = Name,
        description = Description?: "",
        color = Color?: "",
        isActive = IsActive
    )
}

fun ForumAttachmentDto.toDomain(): ForumAttachment {
    return ForumAttachment(
        fileName = FileName,
        fileUrl = FileUrl,
        fileSize = FileSize,
        fileType = FileType?: ""
    )
}

fun ForumStatsDto.toDomain(): ForumStats {
    return ForumStats(
        totalQuestions = TotalQuestions,
        totalAnswers = TotalAnswers,
        totalUsers = TotalUsers,
        solvedQuestions = SolvedQuestions
    )
}
