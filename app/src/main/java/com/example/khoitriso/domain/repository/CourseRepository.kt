package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Course

interface CourseRepository {
    suspend fun getCourse(): Result<List<Course>>
    suspend fun getCourseById(id: Int): Course
    suspend fun getMyCourse(): List<Course>
}