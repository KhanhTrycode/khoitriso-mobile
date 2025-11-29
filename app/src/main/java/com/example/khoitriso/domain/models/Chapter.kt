package com.example.khoitriso.domain.models



data class Chapter(
    val bookId: Int,
    val createdAt: String,
    val description: String,
    val id: Int,
    val orderIndex: Int,
    val questionCount: Int,
    val questions: List<Question>,
    val title: String,
    val updatedAt: String
)