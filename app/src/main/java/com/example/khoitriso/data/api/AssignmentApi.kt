package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.AssignmentDto
import com.example.khoitriso.data.dto.AssignmentPreviewDto
import com.example.khoitriso.data.dto.AssignmentSubmissionDto
import com.example.khoitriso.data.dto.QuestionDto
import com.example.khoitriso.data.dto.request.AssignmentSubmissionRequest
import retrofit2.Response
import retrofit2.http.*

interface AssignmentApi {
    
    @GET("assignments")
    suspend fun getAssignments(
        @Query("lessonId") lessonId: Int? = null,
        @Query("isPublished") isPublished: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiRespone<List<AssignmentPreviewDto>>>
    
    @GET("assignments/{id}")
    suspend fun getAssignmentById(
        @Path("id") id: Int
    ): Response<ApiRespone<AssignmentDto>>
    
    @GET("assignments/{id}/questions")
    suspend fun getAssignmentQuestions(
        @Path("id") id: Int
    ): Response<ApiRespone<List<QuestionDto>>>
    
    @GET("assignments/{id}/answers")
    suspend fun getAssignmentAnswers(
        @Path("id") id: Int
    ): Response<ApiRespone<List<QuestionDto>>>
    
    @POST("assignments/{id}/submit")
    suspend fun submitAssignment(
        @Path("id") id: Int,
        @Body request: AssignmentSubmissionRequest
    ): Response<ApiRespone<AssignmentSubmissionDto>>
    
    @GET("assignments/{id}/submissions")
    suspend fun getSubmissions(
        @Path("id") id: Int,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiRespone<List<AssignmentSubmissionDto>>>
    
    @GET("assignments/submissions/{submissionId}")
    suspend fun getSubmissionById(
        @Path("submissionId") submissionId: Int
    ): Response<ApiRespone<AssignmentSubmissionDto>>
}
