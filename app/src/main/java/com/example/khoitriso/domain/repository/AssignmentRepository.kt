package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.AssignmentPreview
import com.example.khoitriso.domain.models.AssignmentSubmission
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.domain.request.AssignmentSubmissionRequest

interface AssignmentRepository {
    
    suspend fun getAssignments(
        lessonId: Int? = null,
        isPublished: Boolean? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<AssignmentPreview>>
    
    suspend fun getAssignmentById(id: Int): Result<Assignment>
    
    suspend fun getAssignmentQuestions(id: Int): Result<List<Question>>
    
    suspend fun getAssignmentAnswers(id: Int): Result<List<Question>>
    
    suspend fun submitAssignment(
        id: Int,
        submission: AssignmentSubmissionRequest
    ): Result<AssignmentSubmission>
    
    suspend fun getSubmissions(
        id: Int,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<AssignmentSubmission>>
    
    suspend fun getSubmissionById(submissionId: Int): Result<AssignmentSubmission>
}
