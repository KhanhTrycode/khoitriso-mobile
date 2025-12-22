package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.AssignmentDto
import com.example.khoitriso.data.dto.CourseDetailDto
import com.example.khoitriso.data.dto.CourseDto
import com.example.khoitriso.data.dto.LessonDto
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.domain.models.Book
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApi {
    @GET("courses")
    suspend fun getCourse(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int =20,
        @Query("search") search: String?= null,
        @Query("approvalStatus") approvalStatus: Int? = null,
        @Query("authorId") authorId: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
        @Query("isFree") isFree: Boolean? = null
    ): Response<ApiResponeData<CourseDto>>

    @GET("courses/{id}")
    suspend fun getCourseById(
        @Path("id") id: Int
    ): Response<ApiRespone<CourseDetailDto>>

    @GET("courses/my-courses")
    suspend fun getMyCourses(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponeData<MyCourseDto>>

    @GET("assignments/{id}")
    suspend fun getAssignmentById(
        @Path("id") id: Int
    ): Response<ApiRespone<AssignmentDto>>

    @POST("assignments/{id}/submit")
    suspend fun submitAssignment(
        @Path("id") id: Int,
        @Body request: com.example.khoitriso.data.dto.request.AssignmentSubmissionRequest
    ): Response<ApiRespone<com.example.khoitriso.data.dto.AssignmentSubmissionDto>>

    @GET("assignments/{id}/results")
    suspend fun getAssignmentResults(
        @Path("id") id: Int
    ): Response<ApiRespone<com.example.khoitriso.data.dto.AssignmentResultDto>>

    @GET("assignments/{id}/attempts")
    suspend fun getUserAttempts(
        @Path("id") id: Int
    ): Response<ApiRespone<List<com.example.khoitriso.data.dto.AssignmentSubmissionDto>>>

    @GET("assignments/{id}/questions")
    suspend fun getAssignmentQuestions(
        @Path("id") id: Int
    ): Response<ApiRespone<List<com.example.khoitriso.data.dto.QuestionDto>>>
}