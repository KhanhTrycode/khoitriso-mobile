package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Assignment

data class AssignmentDto(
    val Description: String,
    val DueDate: String,
    val Id: Int,
    val IsPublished: Boolean,
    val LessonId: Int,
    val MaxAttempts: Int,
    val MaxScore: Int,
    val PassingScore: Int,
    val Questions: List<QuestionDto>?,
    val ShowAnswersAfter: Int,
    val ShuffleOptions: Boolean,
    val ShuffleQuestions: Boolean,
    val TimeLimit: Int,
    val Title: String,
    val UserAttempts: List<UserDTO>
)

fun AssignmentDto.toDomain() =Assignment(
    description = Description,
    dueDate = DueDate,
    id = Id,
    isPublished = IsPublished,
    lessonId = LessonId,
    maxAttempts = MaxAttempts,
    maxScore = MaxScore,
    passingScore = PassingScore,
    questions = Questions?.map { it.toDomain() }?: emptyList(),
    showAnswersAfter = ShowAnswersAfter,
    shuffleOptions = ShuffleOptions,
    shuffleQuestions = ShuffleQuestions,
    timeLimit = TimeLimit,
    title = Title,
    userAttempts = UserAttempts.map { it.toDomain() }
)