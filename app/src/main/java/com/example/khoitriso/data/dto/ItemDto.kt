package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.OrderItem

data class ItemDto(
    val Id: Int,
    val ItemId: Int,
    val ItemName: String,
    val ItemType: Int,
    val ItemTypeName: String,
    val Price: Double,
    val Quantity: Int,
    val SubTotal: Int
)

fun ItemDto.toDomain() = OrderItem(
    id = Id,
    itemId = ItemId,
    itemName = ItemName,
    itemType = ItemType,
    price = Price,
    quantity = Quantity,
    subTotal = SubTotal
)