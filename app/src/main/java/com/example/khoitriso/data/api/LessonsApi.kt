package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.LessonDto
import com.example.khoitriso.data.dto.VideoProgressDto
import com.example.khoitriso.data.dto.LessonDiscussionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LessonsApi {
    @GET("courses/{id}/lessons")
    suspend fun getLessonByCourse(
        @Path("id") id: Int
    ): Response<ApiRespone<List<LessonDto>>>

    @GET("lessons/{id}")
    suspend fun getLessonById(
        @Path("id") id: Int
    ): Response<ApiRespone<LessonDto>>

    @GET("lessons/{id}/video-progress")
    suspend fun getLessonProgress(
        @Path("id") id: Int
    ): Response<ApiRespone<VideoProgressDto>>

    @POST("lessons/{id}/video-progress")
    suspend fun postLessonProgress(
        @Path("id") id: Int,
        @Body request: VideoProgressDto
    ): Response<ApiRespone<VideoProgressDto>>

    @POST("lessons/{id}/discussions")
    suspend fun postLessonDiscussion(
        @Path("id") id: Int,
        @Body request: LessonDiscussionDto
    ): Response<ApiRespone<VideoProgressDto>>

    @GET("lessons/{id}/discussions")
    suspend fun getLessonDiscussion(
        @Path("id") id: Int,
    ): Response<ApiRespone<VideoProgressDto>>
}