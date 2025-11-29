package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.models.Carts

data class CartDto(
    val CartItems: List<CartItemDto>,
    val TotalItems: Int,
    val TotalAmount: Double,
)

data class CartItemDto(
    val Id: Int,
    val ItemId: Int,
    val ItemType: Int, // 0: Book, 1: Course, 2: LearningPath
    val Price: Double,
    val AddedAt: String?,
    val CoverImage: String?,
    val Title: String?,
    val Item: Any?, // Full item data (CourseDto, BookDto, etc.)
)

fun CartItemDto.toDomain() = CartItem(
    id = Id,
    itemId = ItemId,
    itemType = ItemType,
    price = Price,
    coverImage = CoverImage ?: "",
    title = Title ?: "Unknown"
)

fun CartDto.toDomain() = Carts(
    cartItems = CartItems.map { it.toDomain() },
    totalItems = TotalItems,
    totalPrice = TotalAmount
)

data class AddToCartRequest(
    val ItemId: Int,
    val ItemType: Int, // 0: Book, 1: Course, 2: LearningPath
)

