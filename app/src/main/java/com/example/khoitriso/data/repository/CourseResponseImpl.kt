package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.CourseApi
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.repository.CourseRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class CourseResponseImpl @Inject constructor(
    private val courseApi: CourseApi,
    private val gson: Gson
) : CourseRepository {
    override suspend fun getCourse(getPagingRequest: PagingRequest?): Result<List<Course>> {
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
                Result.success(response.body()?.Result?.Items?.map { it.toDomain() } ?: emptyList())
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
                    val items = body.Items?.map { it.toDomain() }?: emptyList()
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
}

