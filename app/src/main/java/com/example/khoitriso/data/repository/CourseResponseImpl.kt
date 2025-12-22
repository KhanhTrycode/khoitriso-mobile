package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.CourseApi
import com.example.khoitriso.data.api.LessonsApi
import com.example.khoitriso.data.dto.BookDto
import com.example.khoitriso.data.dto.CourseDto
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.data.dto.request.AssignmentSubmissionRequest as DtoAssignmentSubmissionRequest
import com.example.khoitriso.data.dto.request.QuestionAnswerRequest as DtoQuestionAnswerRequest
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.AssignmentResult
import com.example.khoitriso.domain.models.AssignmentSubmission
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.domain.repository.CourseRepository
import com.example.khoitriso.domain.request.AssignmentSubmissionRequest
import com.example.khoitriso.utils.ErrorMessageHelper
import com.example.khoitriso.utils.ErrorType
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class CourseResponseImpl @Inject constructor(
    private val courseApi: CourseApi,
    private val lessonsApi: LessonsApi,
    private val gson: Gson,
    @ApplicationContext private val context: Context,
) : CourseRepository {
    override suspend fun getCourse(getPagingRequest: PagingRequest): Result<MyResponese<Course>> {
        return try {
            val response = courseApi.getCourse(
                page = getPagingRequest?.page ?: 1,
                pageSize = getPagingRequest?.pageSize ?: 20,
                search = getPagingRequest?.search,
                approvalStatus = getPagingRequest?.approvalStatus,
                authorId = getPagingRequest?.authorId,
                sortBy = getPagingRequest?.sortBy,
                sortOrder = getPagingRequest?.sortOrder
            )
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain(CourseDto::toDomain))
                } else {
                    Result.failure(Exception("Response body or Result is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCourseById(id: Int): Result<CourseDetail> {
        return try {
            val response = courseApi.getCourseById(id)
            if (response.isSuccessful) {
                Result.success(
                    response.body()?.Result?.toDomain() ?: throw Exception
                        (
                        "Course" +
                                " not found"
                    )
                )
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun getMyCourses(): Result<List<MyCourse>> {
        return try {
            val response = courseApi.getMyCourses(page = 1, pageSize = 100)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    val items = body.Items?.map { it.toDomain() } ?: emptyList()
                    Result.success(items)
                } else {
                    Result.failure(Exception("Response body or Result is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun SearchCourse(): Result<List<Course>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAssignmentById(id: Int): Result<Assignment> {
        return try {
            val response = courseApi.getAssignmentById(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLessonById(id: Int): Result<Lesson> {
        return try {
            val response = lessonsApi.getLessonById(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitAssignment(id: Int, request: AssignmentSubmissionRequest): Result<AssignmentSubmission> {
        return try {
            val dtoRequest = DtoAssignmentSubmissionRequest(
                Answers = request.answers.map {
                    DtoQuestionAnswerRequest(
                        QuestionId = it.questionId,
                        OptionId = it.optionId,
                        AnswerText = it.answerText
                    )
                }
            )
            val response = courseApi.submitAssignment(id, dtoRequest)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAssignmentResults(id: Int): Result<AssignmentResult> {
        return try {
            val response = courseApi.getAssignmentResults(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserAttempts(id: Int): Result<List<AssignmentSubmission>> {
        return try {
            val response = courseApi.getUserAttempts(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.map { it.toDomain() })
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAssignmentQuestions(id: Int): Result<List<Question>> {
        return try {
            val response = courseApi.getAssignmentQuestions(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.map { it.toDomain() })
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markLessonComplete(lessonId: Int): Result<Unit> {
        return try {
            val request = com.example.khoitriso.data.api.LessonProgressRequest(
                WatchTime = 0,
                IsCompleted = true,
                VideoPosition = 0
            )
            val response = lessonsApi.updateLessonProgress(lessonId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLessonProgress(lessonId: Int): Result<com.example.khoitriso.domain.models.VideoProgress> {
        return try {
            val response = lessonsApi.getLessonProgress(lessonId)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveLessonProgress(
        lessonId: Int,
        watchTime: Int,
        videoPosition: Int,
        isCompleted: Boolean
    ): Result<Unit> {
        return try {
            val request = com.example.khoitriso.data.api.LessonProgressRequest(
                WatchTime = watchTime,
                IsCompleted = isCompleted,
                VideoPosition = videoPosition
            )
            val response = lessonsApi.updateLessonProgress(lessonId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
