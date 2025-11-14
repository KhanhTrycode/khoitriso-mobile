package com.example.khoitriso.domain.models



data class BookDetail(
    val ApprovalStatus: Int,
    val Author: Author,
    val AuthorId: Int,
    val Category: Category,
    val CategoryId: Int,
    val Chapters: List<Chapter>,
    val CoverImage: String,
    val CreatedAt: String,
    val Description: String,
    val EbookFile: String,
    val Edition: String,
    val Id: Int,
    val IsActive: Boolean,
    val IsOwned: Boolean,
    val Isbn: String,
    val Language: String,
    val Price: Int,
    val PublicationYear: Int,
    val Rating: Int,
    val ReviewNotes: Any,
    val StaticPagePath: String,
    val Title: String,
    val TotalReviews: Int,
    val UpdatedAt: String
)