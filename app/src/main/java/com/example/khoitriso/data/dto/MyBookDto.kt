package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.MyBook

data class MyBookDto(
    val BookId: Int,
    val Book: BookDto,
    val ActivatedAt: String,
    val TotalChapters: Int,
    val CompletedChapters: Int
)

fun MyBookDto.toDomain() = MyBook(
    bookId = BookId,
    book = Book.toDomain(),
    activatedAt = ActivatedAt,
    totalChapters = TotalChapters,
    completedChapters = CompletedChapters
)