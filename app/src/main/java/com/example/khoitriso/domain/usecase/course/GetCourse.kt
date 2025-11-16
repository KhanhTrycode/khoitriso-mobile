package com.example.khoitriso.domain.usecase.course

import android.util.Log
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.repository.CourseRepository

class GetCourse(private val repo: CourseRepository) {
    suspend operator fun invoke(): List<Course> {
        val result = repo.getCourse()
        Log.d("Usecase", "invokeGetCourse: $result")
        return result.getOrElse{
            return emptyList()
        }
    }

}