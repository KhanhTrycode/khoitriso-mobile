package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.CartDto

interface CartRepository {
    suspend fun getCart(): Result<CartDto>
    suspend fun addToCart(itemId: Int, itemType: Int): Result<CartDto>
    suspend fun removeFromCart(itemId: Int): Result<Boolean>
    suspend fun clearCart(): Result<Boolean>
}

