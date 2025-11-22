package com.example.khoitriso.domain.models


data class BookDetail(
    val approvalStatus: Int,
    val author: Author,
    val category: Category,
    val chapters: List<Chapter>,
    val coverImage: String,
    val createdAt: String,
    val description: String,
    val ebookFile: String,
    val edition: String,
    val id: Int,
    val isOwned: Boolean,
    val isbn: String,
    val language: String,
    val price: Int,
    val publicationYear: Int,
    val rating: Float,
    val reviewNotes: Any,
    val staticPagePath: String,
    val title: String,
    val totalReviews: Int,
    val updatedAt: String
)