package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Wishlist
import com.example.khoitriso.domain.models.WishlistItem

interface WishlistRepository {
    suspend fun getWishlist(
        page: Int = 1,
        pageSize: Int = 20,
        itemType: Int? = null
    ): Result<List<WishlistItem>>

    suspend fun addToWishlist(
        itemId: Int,
        itemType: Int
    ): Result<Wishlist>

    suspend fun removeFromWishlist(wishlistId: Int): Result<Unit>

    suspend fun removeItemFromWishlist(
        itemId: Int,
        itemType: Int
    ): Result<Unit>

    suspend fun isInWishlist(
        itemId: Int,
        itemType: Int
    ): Result<Boolean>

    suspend fun clearWishlist(): Result<Unit>
}

