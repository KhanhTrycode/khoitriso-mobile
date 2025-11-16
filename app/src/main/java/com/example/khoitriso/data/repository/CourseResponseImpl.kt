package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.CourseApi
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.repository.CourseRepository
import javax.inject.Inject


class CourseResponseImpl @Inject constructor(
    private val courseApi: CourseApi,
) : CourseRepository {
    override suspend fun getCourse(): Result<List<Course>> {
        return try {
            val response = courseApi.getCourse()
            if (response.isSuccessful){
                Result.success(response.body()?.Result?.Items?.map { it.toDomain() } ?: emptyList())
            }else{
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCourseById(id: Int): Course {
        TODO("Not yet implemented")
    }

    override suspend fun getMyCourse(): List<Course> {
        TODO("Not yet implemented")
    }

}