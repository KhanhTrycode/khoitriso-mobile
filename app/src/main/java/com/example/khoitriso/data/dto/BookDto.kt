package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Book

data class BookDto(
    val ApprovalStatus: Int,
    val Author: AuthorDto,
    val AuthorId: Int,
    val Category: CategoryDto,
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
    val Rating: Float?,
    val Title: String,
    val TotalReviews: Int,
    val UpdatedAt: String
)

fun BookDto.toDomain() : Book = Book(
    approvalStatus = ApprovalStatus,
    author = Author.toDomain(),
    category = Category.toDomain(),
    coverImage = CoverImage,
    createdAt = CreatedAt,
    description = Description,
    edition = Edition,
    id = Id,
    isActive = IsActive,
    isbn = Isbn,
    language = Language,
    price = Price,
    publicationYear = PublicationYear,
    rating = Rating?: 0f,
    title = Title,
    totalReviews = TotalReviews,
    updatedAt = UpdatedAt
)