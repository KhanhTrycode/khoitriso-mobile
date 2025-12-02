package com.example.khoitriso.domain.models

data class WishlistItem(
    val id: Int,
    val itemId: Int,
    val itemType: Int, // 0: Book, 1: Course, 2: LearningPath
    val addedAt: String,
    val item: Any? = null // Full item data (Course, Book, etc.)
)

data class Wishlist(
    val items: List<WishlistItem>,
    val totalItems: Int
)

