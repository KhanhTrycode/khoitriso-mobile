package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.CartApi
import com.example.khoitriso.data.dto.AddToCartRequest
import com.example.khoitriso.data.dto.CartDto
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.CartRepository
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi
) : CartRepository {

    override suspend fun getCart(): Result<Carts> {
        return try {
            val response = cartApi.getCart()
            if (response.isSuccessful) {
                val cartDto = response.body()?.Result
                if (cartDto != null) {
                    Result.success(cartDto.toDomain())
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

    override suspend fun addToCart(itemId: Int, itemType: Int): Result<Boolean> {
        return try {
            val request = AddToCartRequest(ItemId = itemId, ItemType = itemType)
            val response = cartApi.addToCart(request)
            if (response.isSuccessful) {
                val cartDto = response.body()?.Result
                if (cartDto != null) {
                    Result.success(true)
                } else {
                    Result.success(false)
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(cartId: Int): Result<Boolean> {
        return try {
            val response = cartApi.removeFromCart(cartId)
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

