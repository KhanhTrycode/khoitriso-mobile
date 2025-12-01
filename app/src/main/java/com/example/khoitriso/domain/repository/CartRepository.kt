package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.models.MyResponese

interface CartRepository {
    suspend fun getCart(): Result<Carts>
    suspend fun addToCart(itemId: Int, itemType: Int): Result<Boolean>
    suspend fun removeFromCart(cartId: Int): Result<Boolean>
    suspend fun clearCart(): Result<Boolean>
}

