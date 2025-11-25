package com.example.khoitriso.data.dto

data class CartDto(
    val CartItems: List<CartItemDto>,
    val TotalItems: Int,
    val TotalAmount: Double
)

data class CartItemDto(
    val Id: Int,
    val ItemId: Int,
    val ItemType: Int, // 0: Book, 1: Course, 2: LearningPath
    val Price: Double,
    val AddedAt: String?,
    val CoverImage: String?,
    val Title: String?,
    val Item: Any? // Full item data (CourseDto, BookDto, etc.)
)

data class AddToCartRequest(
    val ItemId: Int,
    val ItemType: Int // 0: Book, 1: Course, 2: LearningPath
)

