package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail

interface CourseRepository {
    suspend fun getCourse(): Result<List<Course>>
    suspend fun getCourseById(id: Int): Result<CourseDetail>
    suspend fun getMyCourse(): Result<List<Course>>
    suspend fun SearchCourse(): Result<List<Course>>
}