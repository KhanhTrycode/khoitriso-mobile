package com.example.khoitriso.domain.models

data class OrderItem(
    val id: Int,
    val itemId: Int,
    val itemName: String,
    val itemType: Int,
    val price: Double,
    val quantity: Int,
    val subTotal: Int
)
