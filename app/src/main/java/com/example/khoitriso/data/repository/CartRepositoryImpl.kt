package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.CartApi
import com.example.khoitriso.data.dto.AddToCartRequest
import com.example.khoitriso.data.dto.CartDto
import com.example.khoitriso.domain.repository.CartRepository
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi
) : CartRepository {

    override suspend fun getCart(): Result<CartDto> {
        return try {
            val response = cartApi.getCart()
            if (response.isSuccessful) {
                val cartDto = response.body()?.Result
                if (cartDto != null) {
                    Result.success(cartDto)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToCart(itemId: Int, itemType: Int): Result<CartDto> {
        return try {
            val request = AddToCartRequest(ItemId = itemId, ItemType = itemType)
            val response = cartApi.addToCart(request)
            if (response.isSuccessful) {
                val cartDto = response.body()?.Result
                if (cartDto != null) {
                    Result.success(cartDto)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(itemId: Int): Result<Boolean> {
        return try {
            val response = cartApi.removeFromCart(itemId)
            if (response.isSuccessful) {
                Result.success(response.body()?.Result == true)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCart(): Result<Boolean> {
        return try {
            val response = cartApi.clearCart()
            if (response.isSuccessful) {
                Result.success(response.body()?.Result == true)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

