package com.example.khoitriso.domain.usecase.course

import android.util.Log
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.repository.CourseRepository

data class CourseUsecase(
    val getCourse: GetCourse,
    val getCourseById: GetCourseById,
    val getMyCourse: GetMyCourse,
)

class GetCourse(private val repo: CourseRepository) {
    suspend operator fun invoke(): Result<List<Course>> {
        return repo.getCourse()
    }
}

class GetCourseById(private val repo: CourseRepository) {
    suspend operator fun invoke(id: Int): Result<CourseDetail> {
        val result =repo.getCourseById(id)

        return result
    }
}

class GetMyCourse(private val repo: CourseRepository) {
    suspend operator fun invoke(): Result<List<MyCourse>> {
        return repo.getMyCourses()

    }
}