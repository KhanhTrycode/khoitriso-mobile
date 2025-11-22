package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Course

interface CourseRepository {
    suspend fun getCourse(): Result<List<Course>>
    suspend fun getCourseById(id: Int): Result<Course>
    suspend fun getMyCourse(): Result<List<Course>>
    suspend fun SearchCourse(): Result<List<Course>>
}