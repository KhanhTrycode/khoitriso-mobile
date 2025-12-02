package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Wishlist
import com.example.khoitriso.domain.models.WishlistItem

data class WishlistItemDto(
    val Id: Int,
    val ItemId: Int,
    val ItemType: Int, // 0: Book, 1: Course, 2: LearningPath
    val AddedAt: String,
    val Item: Any? = null // Full item data (CourseDto, BookDto, etc.)
)

data class WishlistDto(
    val Items: List<WishlistItemDto>,
    val TotalItems: Int
)

fun WishlistItemDto.toDomain() = WishlistItem(
    id = Id,
    itemId = ItemId,
    itemType = ItemType,
    addedAt = AddedAt,
    item = Item
)

fun WishlistDto.toDomain() = Wishlist(
    items = Items.map { it.toDomain() },
    totalItems = TotalItems
)

