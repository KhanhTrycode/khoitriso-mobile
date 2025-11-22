package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Chapter

data class ChapterDto (
    val BookId: Int,
    val CreatedAt: String,
    val Description: String,
    val Id: Int,
    val OrderIndex: Int,
    val QuestionCount: Int,
    val Questions: List<Any>,
    val Title: String,
    val UpdatedAt: String
)

fun ChapterDto.toDomain() : Chapter = Chapter(
    bookId = BookId,
    createdAt = CreatedAt,
    description = Description,
    id = Id,
    orderIndex = OrderIndex,
    questionCount = QuestionCount,
    questions = Questions,
    title = Title,
    updatedAt = UpdatedAt
)