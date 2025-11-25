package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.CourseApi
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.data.dto.toDetailDomain
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.repository.CourseRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class CourseResponseImpl @Inject constructor(
    private val courseApi: CourseApi,
    private val gson: Gson
) : CourseRepository {
    override suspend fun getCourse(): Result<List<Course>> {
        return try {
            val response = courseApi.getCourse()
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
                    response.body()?.Result?.toDetailDomain() ?: throw Exception
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

    override suspend fun getMyCourse(): Result<List<Course>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyCourses(): Result<List<MyCourseDto>> {
        return try {
            val response = courseApi.getMyCourses(page = 1, pageSize = 100)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    // Parse using Gson
                    val items = body.Items
                    val type = object : TypeToken<List<MyCourseDto>>() {}.type
                    val myCourses: List<MyCourseDto> = gson.fromJson(gson.toJson(items), type)
                    Result.success(myCourses)
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

