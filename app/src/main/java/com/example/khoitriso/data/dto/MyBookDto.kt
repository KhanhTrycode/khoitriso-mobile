package com.example.khoitriso.data.dto

data class MyBookDto(
    val BookId: Int,
    val Book: BookDto,
    val ActivatedAt: String,
    val TotalChapters: Int,
    val CompletedChapters: Int
)

