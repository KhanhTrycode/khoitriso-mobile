package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.WishlistApi
import com.example.khoitriso.data.dto.WishlistItemDto
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.data.request.AddToWishlistRequestDto
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.WishlistItem
import com.example.khoitriso.domain.repository.WishlistRepository
import javax.inject.Inject

class WishlistRepositoryImpl @Inject constructor(
    private val wishlistApi: WishlistApi
) : WishlistRepository {

    override suspend fun getWishlist(
        page: Int,
        pageSize: Int,
        itemType: Int?
    ): Result<MyResponese<WishlistItem>> {
        return try {
            val response = wishlistApi.getWishlist(
                page = page,
                pageSize = pageSize,
                itemType = itemType
            )

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(
                        result.toDomain(WishlistItemDto::toDomain)
                    )
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToWishlist(
        itemId: Int,
        itemType: Int
    ): Result<com.example.khoitriso.domain.models.Wishlist> {
        return try {
            val request = AddToWishlistRequestDto(ItemId = itemId, ItemType = itemType)
            val response = wishlistApi.addToWishlist(request)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    // Response trả về WishlistItemDto, wrap vào Wishlist
                    Result.success(
                        com.example.khoitriso.domain.models.Wishlist(
                            items = listOf(result.toDomain()),
                            totalItems = 1
                        )
                    )
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromWishlist(wishlistId: Int): Result<Unit> {
        return try {
            val response = wishlistApi.removeFromWishlist(wishlistId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeItemFromWishlist(
        itemId: Int,
        itemType: Int
    ): Result<Unit> {
        return try {
            val response = wishlistApi.removeItemFromWishlist(itemId, itemType)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isInWishlist(
        itemId: Int,
        itemType: Int
    ): Result<Boolean> {
        return try {
            val response = wishlistApi.isInWishlist(itemId, itemType)

            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result

                if (result != null) {
                    Result.success(result.IsInWishlist)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearWishlist(): Result<Unit> {
        return try {
            val response = wishlistApi.clearWishlist()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

