package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.ForumApi
import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.forum.*
import com.example.khoitriso.domain.models.*
import com.example.khoitriso.domain.repository.*
import javax.inject.Inject

class ForumRepositoryImpl @Inject constructor(
    private val forumApi: ForumApi
) : ForumRepository {

    override suspend fun getQuestions(
        search: String?,
        categoryId: String?,
        tag: String?,
        isSolved: Boolean?,
        isPinned: Boolean?,
        page: Int,
        pageSize: Int,
        sortBy: String?,
        desc: Boolean?
    ): Result<ForumQuestionsResult> {
        return try {
            val response = forumApi.getQuestions(
                search = search,
                categoryId = categoryId,
                tag = tag,
                isSolved = isSolved,
                isPinned = isPinned,
                page = page,
                pageSize = pageSize,
                sortBy = sortBy,
                desc = desc
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                
                if (result != null) {
                    val items = (result.Items ?: emptyList()).map { it.toDomain() }
                    val total = result.Total ?: 0
                    val pageSize = result.PageSize ?: 20
                    val totalPages = (total + pageSize - 1) / pageSize // Ceiling division
                    
                    Result.success(
                        ForumQuestionsResult(
                            items = items,
                            total = total,
                            page = result.Page ?: page,
                            pageSize = pageSize,
                            totalPages = totalPages
                        )
                    )
                } else {
                    Result.failure(Exception("Response data is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuestionById(id: String): Result<ForumQuestion> {
        return try {
            val response = forumApi.getQuestionById(id)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Question not found"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createQuestion(request: CreateQuestionRequest): Result<ForumQuestion> {
        return try {
            val dto = CreateQuestionRequest(
                Title = request.title,
                Content = request.content,
                UserId = request.userId,
                UserName = request.userName,
                UserAvatar = request.userAvatar,
                Tags = request.tags,
                CategoryId = request.categoryId,
                CategoryName = request.categoryName
            )
            val response = forumApi.createQuestion(dto)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Failed to create question"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): Result<ForumQuestion> {
        return try {
            val dto = UpdateQuestionRequest(
                Title = request.title,
                Content = request.content,
                Tags = request.tags,
                CategoryId = request.categoryId,
                CategoryName = request.categoryName,
                IsPinned = request.isPinned,
                IsClosed = request.isClosed
            )
            val response = forumApi.updateQuestion(id, dto)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Failed to update question"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteQuestion(id: String): Result<Unit> {
        return try {
            val response = forumApi.deleteQuestion(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAnswers(questionId: String): Result<List<ForumAnswer>> {
        return try {
            val response = forumApi.getAnswers(questionId)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    val answers = result.map { it.toDomain() }
                    Result.success(answers)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAnswer(questionId: String, request: CreateAnswerRequest): Result<ForumAnswer> {
        return try {
            val dto = CreateAnswerRequest(
                Content = request.content,
                UserId = request.userId,
                UserName = request.userName,
                UserAvatar = request.userAvatar
            )
            val response = forumApi.createAnswer(questionId, dto)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Failed to create answer"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAnswer(id: String, request: UpdateAnswerRequest): Result<ForumAnswer> {
        return try {
            val dto = UpdateAnswerRequest(Content = request.content)
            val response = forumApi.updateAnswer(id, dto)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Failed to update answer"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAnswer(id: String): Result<Unit> {
        return try {
            val response = forumApi.deleteAnswer(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptAnswer(id: String): Result<Unit> {
        return try {
            val response = forumApi.acceptAnswer(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unacceptAnswer(id: String): Result<Unit> {
        return try {
            val response = forumApi.unacceptAnswer(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getComments(parentType: Int, parentId: String): Result<List<ForumComment>> {
        return try {
            val response = forumApi.getComments(parentType, parentId)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    val comments = result.map { it.toDomain() }
                    Result.success(comments)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createComment(request: CreateCommentRequest): Result<ForumComment> {
        return try {
            val dto = CreateCommentRequest(
                ParentId = request.parentId,
                ParentType = request.parentType,
                Content = request.content,
                UserId = request.userId,
                UserName = request.userName,
                UserAvatar = request.userAvatar
            )
            val response = forumApi.createComment(dto)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Failed to create comment"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun vote(request: ForumVoteRequest): Result<Int> {
        return try {
            val dto = ForumVoteRequest(
                TargetId = request.targetId,
                TargetType = request.targetType,
                UserId = request.userId,
                VoteType = request.voteType
            )
            val response = forumApi.vote(dto)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.Total ?: 0)
                } else {
                    Result.failure(Exception("Failed to vote"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVotes(targetType: Int, targetId: String): Result<Int> {
        return try {
            val response = forumApi.getVotes(targetType, targetId)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.Total ?: 0)
                } else {
                    Result.success(0)
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserVote(targetType: Int, targetId: String, userId: Int): Result<Int?> {
        return try {
            val response = forumApi.getUserVote(targetType, targetId, userId)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                Result.success(result?.VoteType)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.success(null)
        }
    }

    override suspend fun addBookmark(questionId: String, userId: Int): Result<Unit> {
        return try {
            val dto = ForumBookmarkRequest(QuestionId = questionId, UserId = userId)
            val response = forumApi.addBookmark(dto)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeBookmark(questionId: String, userId: Int): Result<Unit> {
        return try {
            val response = forumApi.removeBookmark(questionId, userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isBookmarked(questionId: String, userId: Int): Result<Boolean> {
        return try {
            val response = forumApi.isBookmarked(questionId, userId)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                Result.success(result?.IsBookmarked ?: false)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.success(false)
        }
    }

    override suspend fun getBookmarks(userId: Int, page: Int, pageSize: Int): Result<ForumBookmarksResult> {
        return try {
            val response = forumApi.getBookmarks(userId, page, pageSize)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    val items = (result.Items ?: emptyList()).map {
                        ForumBookmarkItem(
                            questionId = it.QuestionId,
                            userId = it.UserId,
                            question = it.Question?.toDomain(),
                            createdAt = it.CreatedAt
                        )
                    }
                    val total = result.Total ?: 0
                    val totalPages = (total + pageSize - 1) / pageSize
                    
                    Result.success(
                        ForumBookmarksResult(
                            items = items,
                            total = total,
                            page = result.Page ?: page,
                            pageSize = pageSize,
                            totalPages = totalPages
                        )
                    )
                } else {
                    Result.failure(Exception("Response data is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(): Result<List<ForumCategory>> {
        return try {
            val response = forumApi.getCategories()
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    val categories = result.map { it.toDomain() }
                    Result.success(categories)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTags(limit: Int): Result<List<ForumTag>> {
        return try {
            val response = forumApi.getTags(limit)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    val tags = result.map { it.toDomain() }
                    Result.success(tags)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStats(): Result<ForumStats> {
        return try {
            val response = forumApi.getStats()
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Failed to get stats"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
