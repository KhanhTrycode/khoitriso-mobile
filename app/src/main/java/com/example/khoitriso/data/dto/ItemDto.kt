package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Item

data class ItemDto(
    val Id: Int,
    val ItemId: Int,
    val ItemName: String,
    val ItemType: Int,
    val ItemTypeName: String,
    val Price: Int,
    val Quantity: Int,
    val SubTotal: Int
)

fun ItemDto.toDomain() = Item(
    id = Id,
    itemId = ItemId,
    itemName = ItemName,
    itemType = ItemType,
    itemTypeName = ItemTypeName,
    price = Price,
    quantity = Quantity,
    subTotal = SubTotal
)