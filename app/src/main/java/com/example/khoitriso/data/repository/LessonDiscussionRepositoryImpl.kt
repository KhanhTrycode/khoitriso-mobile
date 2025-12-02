package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.DiscussionsApi
import com.example.khoitriso.data.dto.LessonDiscussionDto
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.data.request.discussion.CreateLessonDiscussionReplyRequestDto
import com.example.khoitriso.data.request.discussion.CreateLessonDiscussionRequestDto
import com.example.khoitriso.data.request.discussion.UpdateLessonDiscussionRequestDto
import com.example.khoitriso.domain.models.LessonDiscussion
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.LessonDiscussionRepository
import com.example.khoitriso.domain.request.CreateLessonDiscussionReplyRequest
import com.example.khoitriso.domain.request.CreateLessonDiscussionRequest
import com.example.khoitriso.domain.request.UpdateLessonDiscussionRequest
import javax.inject.Inject

class LessonDiscussionRepositoryImpl @Inject constructor(
    private val discussionsApi: DiscussionsApi
) : LessonDiscussionRepository {

    override suspend fun getLessonDiscussions(
        lessonId: Int,
        page: Int,
        pageSize: Int,
        sortBy: String?,
        desc: Boolean?,
        isResolved: Boolean?,
        isPinned: Boolean?
    ): Result<MyResponese<LessonDiscussion>> {
        return try {
            val response = discussionsApi.getLessonDiscussions(
                lessonId = lessonId,
                page = page,
                pageSize = pageSize,
                sortBy = sortBy,
                desc = desc,
                isResolved = isResolved,
                isPinned = isPinned
            )

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result.toDomain(LessonDiscussionDto::toDomain))

                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLessonDiscussionById(
        lessonId: Int,
        discussionId: String
    ): Result<LessonDiscussion> {
        return try {
            val response = discussionsApi.getLessonDiscussionById(lessonId, discussionId)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createLessonDiscussion(
        lessonId: Int,
        request: CreateLessonDiscussionRequest
    ): Result<LessonDiscussion> {
        return try {
            val dto = CreateLessonDiscussionRequestDto(
                Content = request.content,
                VideoTimestamp = request.videoTimestamp
            )

            val response = discussionsApi.createLessonDiscussion(lessonId, dto)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLessonDiscussion(
        lessonId: Int,
        discussionId: String,
        request: UpdateLessonDiscussionRequest
    ): Result<LessonDiscussion> {
        return try {
            val dto = UpdateLessonDiscussionRequestDto(Content = request.content)

            val response = discussionsApi.updateLessonDiscussion(lessonId, discussionId, dto)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit> {
        return try {
            val response = discussionsApi.deleteLessonDiscussion(lessonId, discussionId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pinLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit> {
        return try {
            val response = discussionsApi.pinLessonDiscussion(lessonId, discussionId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unpinLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit> {
        return try {
            val response = discussionsApi.unpinLessonDiscussion(lessonId, discussionId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resolveLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit> {
        return try {
            val response = discussionsApi.resolveLessonDiscussion(lessonId, discussionId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unresolveLessonDiscussion(
        lessonId: Int,
        discussionId: String
    ): Result<Unit> {
        return try {
            val response = discussionsApi.unresolveLessonDiscussion(lessonId, discussionId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLessonDiscussionReplies(
        lessonId: Int,
        discussionId: String,
        page: Int,
        pageSize: Int,
        sortBy: String?,
        desc: Boolean?
    ): Result<MyResponese<LessonDiscussion>> {
        return try {
            val response = discussionsApi.getLessonDiscussionReplies(
                lessonId = lessonId,
                discussionId = discussionId,
                page = page,
                pageSize = pageSize,
                sortBy = sortBy,
                desc = desc
            )

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(
                        result.toDomain(LessonDiscussionDto::toDomain)
                    )
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        request: CreateLessonDiscussionReplyRequest
    ): Result<LessonDiscussion> {
        return try {
            val dto = CreateLessonDiscussionReplyRequestDto(Content = request.content)

            val response = discussionsApi.createLessonDiscussionReply(lessonId, discussionId, dto)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        replyId: String,
        request: CreateLessonDiscussionReplyRequest
    ): Result<LessonDiscussion> {
        return try {
            val dto = CreateLessonDiscussionReplyRequestDto(Content = request.content)

            val response = discussionsApi.updateLessonDiscussionReply(lessonId, discussionId, replyId, dto)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        replyId: String
    ): Result<Unit> {
        return try {
            val response = discussionsApi.deleteLessonDiscussionReply(lessonId, discussionId, replyId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        replyId: String
    ): Result<Unit> {
        return try {
            val response = discussionsApi.acceptLessonDiscussionReply(lessonId, discussionId, replyId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun voteLessonDiscussion(
        lessonId: Int,
        discussionId: String,
        voteType: Int
    ): Result<Int> {
        return try {
            val response = discussionsApi.voteLessonDiscussion(lessonId, discussionId, voteType)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun voteLessonDiscussionReply(
        lessonId: Int,
        discussionId: String,
        replyId: String,
        voteType: Int
    ): Result<Int> {
        return try {
            val response = discussionsApi.voteLessonDiscussionReply(lessonId, discussionId, replyId, voteType)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

