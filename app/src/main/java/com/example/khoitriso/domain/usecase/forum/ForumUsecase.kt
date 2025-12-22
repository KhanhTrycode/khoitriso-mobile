package com.example.khoitriso.domain.usecase.forum

import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.repository.*
import com.example.khoitriso.domain.request.CreateAnswerRequest
import com.example.khoitriso.domain.request.CreateCommentRequest
import com.example.khoitriso.domain.request.CreateQuestionRequest
import com.example.khoitriso.domain.request.ForumBookmarksResult
import com.example.khoitriso.domain.request.ForumQuestions
import com.example.khoitriso.domain.request.ForumVoteRequest
import com.example.khoitriso.domain.request.UpdateAnswerRequest
import com.example.khoitriso.domain.request.UpdateCommentRequest
import com.example.khoitriso.domain.request.UpdateQuestionRequest

data class ForumUsecase(
    val getQuestions: GetQuestions,
    val getQuestionById: GetQuestionById,
    val createQuestion: CreateQuestion,
    val updateQuestion: UpdateQuestion,
    val deleteQuestion: DeleteQuestion,
    val getAnswers: GetAnswers,
    val createAnswer: CreateAnswer,
    val updateAnswer: UpdateAnswer,
    val deleteAnswer: DeleteAnswer,
    val acceptAnswer: AcceptAnswer,
    val getComments: GetComments,
    val createComment: CreateComment,
    val updateComment: UpdateComment,
    val deleteComment: DeleteComment,
    val vote: Vote,
    val getVotes: GetVotes,
    val getUserVote: GetUserVote,
    val getUserVotes: GetUserVotes,
    val addBookmark: AddBookmark,
    val removeBookmark: RemoveBookmark,
    val isBookmarked: IsBookmarked,
    val getBookmarks: GetBookmarks,
    val getCategories: GetCategories,
    val getTags: GetTags,
    val getStats: GetStats
)

class GetQuestions(private val repo: ForumRepository) {
    suspend operator fun invoke(
        search: String? = null,
        categoryId: String? = null,
        tag: String? = null,
        isSolved: Boolean? = null,
        isPinned: Boolean? = null,
        page: Int = 1,
        pageSize: Int = 20,
        sortBy: String? = null,
        desc: Boolean? = null
    ): Result<MyResponese<ForumQuestion>> {
        return repo.getQuestions(search, categoryId, tag, isSolved, isPinned, page, pageSize, sortBy, desc)
    }
}

class GetQuestionById(private val repo: ForumRepository) {
    suspend operator fun invoke(id: String): Result<ForumQuestion> {
        return repo.getQuestionById(id)
    }
}

class CreateQuestion(private val repo: ForumRepository) {
    suspend operator fun invoke(request: CreateQuestionRequest): Result<ForumQuestion> {
        return repo.createQuestion(request)
    }
}

class UpdateQuestion(private val repo: ForumRepository) {
    suspend operator fun invoke(id: String, request: UpdateQuestionRequest): Result<ForumQuestion> {
        return repo.updateQuestion(id, request)
    }
}

class DeleteQuestion(private val repo: ForumRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repo.deleteQuestion(id)
    }
}

class GetAnswers(private val repo: ForumRepository) {
    suspend operator fun invoke(questionId: String): Result<List<ForumAnswer>> {
        return repo.getAnswers(questionId)
    }
}

class CreateAnswer(private val repo: ForumRepository) {
    suspend operator fun invoke(questionId: String, request: CreateAnswerRequest): Result<ForumAnswer> {
        return repo.createAnswer(questionId, request)
    }
}

class UpdateAnswer(private val repo: ForumRepository) {
    suspend operator fun invoke(id: String, request: UpdateAnswerRequest): Result<ForumAnswer> {
        return repo.updateAnswer(id, request)
    }
}

class DeleteAnswer(private val repo: ForumRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repo.deleteAnswer(id)
    }
}

class AcceptAnswer(private val repo: ForumRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repo.acceptAnswer(id)
    }
}

class GetComments(private val repo: ForumRepository) {
    suspend operator fun invoke(parentType: Int, parentId: String): Result<List<ForumComment>> {
        return repo.getComments(parentType, parentId)
    }
}

class CreateComment(private val repo: ForumRepository) {
    suspend operator fun invoke(request: CreateCommentRequest): Result<ForumComment> {
        return repo.createComment(request)
    }
}

class UpdateComment(private val repo: ForumRepository) {
    suspend operator fun invoke(id: String, request: UpdateCommentRequest): Result<ForumComment> {
        return repo.updateComment(id, request)
    }
}

class DeleteComment(private val repo: ForumRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repo.deleteComment(id)
    }
}

class Vote(private val repo: ForumRepository) {
    suspend operator fun invoke(request: ForumVoteRequest): Result<Int> {
        return repo.vote(request)
    }
}

class GetVotes(private val repo: ForumRepository) {
    suspend operator fun invoke(targetType: Int, targetId: String): Result<Int> {
        return repo.getVotes(targetType, targetId)
    }
}

class GetUserVote(private val repo: ForumRepository) {
    suspend operator fun invoke(targetType: Int, targetId: String, userId: Int): Result<Int> {
        return repo.getUserVote(targetType, targetId, userId)
    }
}

class GetUserVotes(private val repo: ForumRepository) {
    suspend operator fun invoke(userId: Int): Result<Map<String, Int>> {
        return repo.getUserVotes(userId)
    }
}

class AddBookmark(private val repo: ForumRepository) {
    suspend operator fun invoke(questionId: String, userId: Int): Result<Unit> {
        return repo.addBookmark(questionId, userId)
    }
}

class RemoveBookmark(private val repo: ForumRepository) {
    suspend operator fun invoke(questionId: String, userId: Int): Result<Unit> {
        return repo.removeBookmark(questionId, userId)
    }
}

class IsBookmarked(private val repo: ForumRepository) {
    suspend operator fun invoke(questionId: String, userId: Int): Result<Boolean> {
        return repo.isBookmarked(questionId, userId)
    }
}

class GetBookmarks(private val repo: ForumRepository) {
    suspend operator fun invoke(userId: Int, page: Int = 1, pageSize: Int = 20): Result<ForumBookmarksResult> {
        return repo.getBookmarks(userId, page, pageSize)
    }
}

class GetCategories(private val repo: ForumRepository) {
    suspend operator fun invoke(): Result<List<ForumCategory>> {
        return repo.getCategories()
    }
}

class GetTags(private val repo: ForumRepository) {
    suspend operator fun invoke(limit: Int = 20): Result<List<ForumTag>> {
        return repo.getTags(limit)
    }
}

class GetStats(private val repo: ForumRepository) {
    suspend operator fun invoke(): Result<ForumStats> {
        return repo.getStats()
    }
}
