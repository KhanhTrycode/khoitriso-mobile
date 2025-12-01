package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.models.MyResponese

interface CourseRepository {
    suspend fun getCourse(getPagingRequest: PagingRequest): Result<MyResponese<Course>>
    suspend fun getCourseById(id: Int): Result<CourseDetail>
    suspend fun getMyCourses(): Result<List<MyCourse>>
    suspend fun SearchCourse(): Result<List<Course>>
    suspend fun getAssignmentById(id: Int): Result<Assignment>
}