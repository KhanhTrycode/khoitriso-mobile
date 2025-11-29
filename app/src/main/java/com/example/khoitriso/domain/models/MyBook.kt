package com.example.khoitriso.domain.models


data class MyBook(
    val bookId: Int,
    val book: Book,
    val activatedAt: String,
    val totalChapters: Int,
    val completedChapters: Int
)