package com.example.khoitriso.domain.models

data class CartItem(
    val id: Int,
    val itemId: Int,
    val itemType: Int, // 0: Book, 1: Course, 2: LearningPath
    val price: Double,
    val coverImage: String,
    val title: String,
)

data class Carts(
    var cartItems: List<CartItem>,
    val totalItems: Int,
    val totalPrice: Double
)
