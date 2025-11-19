package com.example.khoitriso.domain.models

data class Item(
    val id: Int,
    val itemId: Int,
    val itemName: String,
    val itemType: Int,
    val itemTypeName: String,
    val price: Int,
    val quantity: Int,
    val subTotal: Int
)
