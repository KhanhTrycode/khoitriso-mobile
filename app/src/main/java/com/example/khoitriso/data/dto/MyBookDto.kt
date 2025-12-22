package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Author
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.models.MyBook

data class MyBookDto(
    val Id: Int,
    val Book: BookInfoDto,
    val ActivatedAt: String,
    val IsActive: Boolean
)

// BookInfoDto chứa TotalChapters (từ backend BookInfoDto)
data class BookInfoDto(
    val Id: Int,
    val Title: String,
    val Description: String?,
    val CoverImage: String,
    val Thumbnail: String?,
    val Price: Double,
    val IsFree: Boolean,
    val CategoryId: Int?,
    val Category: CategoryDto?,
    val AuthorId: Int?,
    val Author: AuthorDto?,
    val TotalChapters: Int
)

fun MyBookDto.toDomain() = MyBook(
    bookId = Id,
    book = Book.toDomain(),
    activatedAt = ActivatedAt,
    totalChapters = Book.TotalChapters,
    completedChapters = 0 // Backend không trả về CompletedChapters, để mặc định 0
)

fun BookInfoDto.toDomain() = Book(
    approvalStatus = 0, // Không có trong BookInfoDto từ backend
    author = Author?.toDomain() ?: Author("", "", -1),
    category = Category?.toDomain() ?: Category(0, ""),
    coverImage = CoverImage,
    createdAt = "", // Không có trong BookInfoDto
    description = Description ?: "",
    edition = "", // Không có trong BookInfoDto
    id = Id,
    language = "", // Không có trong BookInfoDto
    price = Price,
    publicationYear = 0, // Không có trong BookInfoDto
    rating = 0f, // Không có trong BookInfoDto
    title = Title,
    totalReviews = 0, // Không có trong BookInfoDto
    updatedAt = "" // Không có trong BookInfoDto
)