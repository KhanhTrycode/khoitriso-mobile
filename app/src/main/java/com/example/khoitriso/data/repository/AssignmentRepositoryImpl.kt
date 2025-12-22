package com.example.khoitriso.data.repository

import android.content.Context
import com.example.khoitriso.data.api.AssignmentApi
import com.example.khoitriso.data.dto.request.AssignmentSubmissionRequest as DtoSubmissionRequest
import com.example.khoitriso.data.dto.request.QuestionAnswerRequest as DtoQuestionAnswerRequest
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.AssignmentPreview
import com.example.khoitriso.domain.models.AssignmentSubmission
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.domain.repository.AssignmentRepository
import com.example.khoitriso.domain.request.AssignmentSubmissionRequest
import com.example.khoitriso.utils.ErrorMessageHelper
import com.example.khoitriso.utils.ErrorType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AssignmentRepositoryImpl @Inject constructor(
    private val assignmentApi: AssignmentApi,
    @ApplicationContext private val context: Context
) : AssignmentRepository {
    
    override suspend fun getAssignments(
        lessonId: Int?,
        isPublished: Boolean?,
        page: Int,
        pageSize: Int
    ): Result<List<AssignmentPreview>> {
        return try {
            val response = assignmentApi.getAssignments(lessonId, isPublished, page, pageSize)
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
    
    override suspend fun getAssignmentById(id: Int): Result<Assignment> {
        return try {
            val response = assignmentApi.getAssignmentById(id)
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
    
    override suspend fun getAssignmentQuestions(id: Int): Result<List<Question>> {
        return try {
            val response = assignmentApi.getAssignmentQuestions(id)
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
    
    override suspend fun getAssignmentAnswers(id: Int): Result<List<Question>> {
        return try {
            val response = assignmentApi.getAssignmentAnswers(id)
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
    
    override suspend fun submitAssignment(
        id: Int,
        submission: AssignmentSubmissionRequest
    ): Result<AssignmentSubmission> {
        return try {
            val dtoRequest = DtoSubmissionRequest(
                Answers = submission.answers.map { answer ->
                    DtoQuestionAnswerRequest(
                        QuestionId = answer.questionId,
                        OptionId = answer.optionId,
                        AnswerText = answer.answerText
                    )
                }
            )
            val response = assignmentApi.submitAssignment(id, dtoRequest)
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
    
    override suspend fun getSubmissions(
        id: Int,
        page: Int,
        pageSize: Int
    ): Result<List<AssignmentSubmission>> {
        return try {
            val response = assignmentApi.getSubmissions(id, page, pageSize)
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
    
    override suspend fun getSubmissionById(submissionId: Int): Result<AssignmentSubmission> {
        return try {
            val response = assignmentApi.getSubmissionById(submissionId)
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
}
