package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.AssignmentResult
import com.example.khoitriso.domain.models.AssignmentSubmission
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.domain.request.AssignmentSubmissionRequest

interface CourseRepository {
    suspend fun getCourse(getPagingRequest: PagingRequest): Result<MyResponese<Course>>
    suspend fun getCourseById(id: Int): Result<CourseDetail>
    suspend fun getMyCourses(): Result<List<MyCourse>>
    suspend fun SearchCourse(): Result<List<Course>>
    suspend fun getAssignmentById(id: Int): Result<Assignment>
    suspend fun getLessonById(id: Int): Result<Lesson>
    suspend fun submitAssignment(id: Int, request: AssignmentSubmissionRequest): Result<AssignmentSubmission>
    suspend fun getAssignmentResults(id: Int): Result<AssignmentResult>
    suspend fun getUserAttempts(id: Int): Result<List<AssignmentSubmission>>
    suspend fun getAssignmentQuestions(id: Int): Result<List<Question>>
    suspend fun markLessonComplete(lessonId: Int): Result<Unit>
    suspend fun getLessonProgress(lessonId: Int): Result<com.example.khoitriso.domain.models.VideoProgress>
    suspend fun saveLessonProgress(lessonId: Int, watchTime: Int, videoPosition: Int, isCompleted: Boolean): Result<Unit>
}