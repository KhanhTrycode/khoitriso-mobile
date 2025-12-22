package com.example.khoitriso.domain.usecase.assignment

import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.AssignmentPreview
import com.example.khoitriso.domain.models.AssignmentSubmission
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.domain.repository.AssignmentRepository
import com.example.khoitriso.domain.request.AssignmentSubmissionRequest

data class AssignmentUsecase(
    val getAssignments: GetAssignments,
    val getAssignmentById: GetAssignmentById,
    val getAssignmentQuestions: GetAssignmentQuestions,
    val getAssignmentAnswers: GetAssignmentAnswers,
    val submitAssignment: SubmitAssignment,
    val getSubmissions: GetSubmissions,
    val getSubmissionById: GetSubmissionById
)

class GetAssignments(private val repo: AssignmentRepository) {
    suspend operator fun invoke(
        lessonId: Int? = null,
        isPublished: Boolean? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<AssignmentPreview>> {
        return repo.getAssignments(lessonId, isPublished, page, pageSize)
    }
}

class GetAssignmentById(private val repo: AssignmentRepository) {
    suspend operator fun invoke(id: Int): Result<Assignment> {
        return repo.getAssignmentById(id)
    }
}

class GetAssignmentQuestions(private val repo: AssignmentRepository) {
    suspend operator fun invoke(id: Int): Result<List<Question>> {
        return repo.getAssignmentQuestions(id)
    }
}

class GetAssignmentAnswers(private val repo: AssignmentRepository) {
    suspend operator fun invoke(id: Int): Result<List<Question>> {
        return repo.getAssignmentAnswers(id)
    }
}

class SubmitAssignment(private val repo: AssignmentRepository) {
    suspend operator fun invoke(
        id: Int,
        submission: AssignmentSubmissionRequest
    ): Result<AssignmentSubmission> {
        return repo.submitAssignment(id, submission)
    }
}

class GetSubmissions(private val repo: AssignmentRepository) {
    suspend operator fun invoke(
        id: Int,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<AssignmentSubmission>> {
        return repo.getSubmissions(id, page, pageSize)
    }
}

class GetSubmissionById(private val repo: AssignmentRepository) {
    suspend operator fun invoke(submissionId: Int): Result<AssignmentSubmission> {
        return repo.getSubmissionById(submissionId)
    }
}
