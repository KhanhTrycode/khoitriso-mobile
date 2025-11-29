package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.MyCourse

interface CourseRepository {
    suspend fun getCourse(getPagingRequest: PagingRequest? = null): Result<List<Course>>
    suspend fun getCourseById(id: Int): Result<CourseDetail>
    suspend fun getMyCourses(): Result<List<MyCourse>>
    suspend fun SearchCourse(): Result<List<Course>>
}