package com.example.khoitriso.domain.models

data class Book(
    val ApprovalStatus: Int,
    val Author: Author,
    val AuthorId: Int,
    val Category: Category,
    val CategoryId: Int,
    val CoverImage: String,
    val CreatedAt: String,
    val Description: String,
    val Edition: String,
    val Id: Int,
    val IsActive: Boolean,
    val Isbn: String,
    val Language: String,
    val Price: Int,
    val PublicationYear: Int,
    val Rating: Int,
    val Title: String,
    val TotalReviews: Int,
    val UpdatedAt: String
)