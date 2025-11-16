package com.example.khoitriso.domain.models

data class Book(
    val approvalStatus: Int,
    val author: Author,
    val category: Category,
    val coverImage: String,
    val createdAt: String,
    val description: String,
    val edition: String,
    val id: Int,
    val isActive: Boolean,
    val isbn: String,
    val language: String,
    val price: Int,
    val publicationYear: Int,
    val rating: Float,
    val title: String,
    val totalReviews: Int,
    val updatedAt: String
)