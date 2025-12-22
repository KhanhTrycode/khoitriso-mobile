package com.example.khoitriso.domain.usecase.course

import android.util.Log
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.domain.repository.CourseRepository

data class CourseUsecase(
    val getCourse: GetCourse,
    val getCourseById: GetCourseById,
    val getMyCourse: GetMyCourse,
    val getAssignmentById: GetAssignmentById,
    val getLessonById: GetLessonById,
    val getAssignmentQuestions: GetAssignmentQuestions,
    val markLessonComplete: MarkLessonComplete
)

class GetAssignmentById(private val repo: CourseRepository) {
    suspend operator fun invoke(id: Int): Result<Assignment> {
        return repo.getAssignmentById(id)
    }
}

class GetLessonById(private val repo: CourseRepository) {
    suspend operator fun invoke(id: Int): Result<Lesson> {
        return repo.getLessonById(id)
    }
}

class GetCourse(private val repo: CourseRepository) {
    suspend operator fun invoke(getPagingRequest: PagingRequest = PagingRequest()):
            Result<MyResponese<Course>> {
        return repo.getCourse(getPagingRequest)
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

class GetAssignmentQuestions(private val repo: CourseRepository) {
    suspend operator fun invoke(id: Int): Result<List<Question>> {
        return repo.getAssignmentQuestions(id)
    }
}

class MarkLessonComplete(private val repo: CourseRepository) {
    suspend operator fun invoke(lessonId: Int): Result<Unit> {
        return repo.markLessonComplete(lessonId)
    }
}