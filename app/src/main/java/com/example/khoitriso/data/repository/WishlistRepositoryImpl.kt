package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.WishlistApi
import com.example.khoitriso.data.dto.BookDto
import com.example.khoitriso.data.dto.CourseDto
import com.example.khoitriso.data.dto.WishlistDto
import com.example.khoitriso.data.dto.WishlistItemDto
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.data.request.AddToWishlistRequestDto
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.WishlistItem
import com.example.khoitriso.domain.repository.WishlistRepository
import com.google.gson.Gson
import javax.inject.Inject

class WishlistRepositoryImpl @Inject constructor(
    private val wishlistApi: WishlistApi,
    private val gson: Gson
) : WishlistRepository {

    override suspend fun getWishlist(
        page: Int,
        pageSize: Int,
        itemType: Int?
    ): Result<List<WishlistItem>> {
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
                    // API trả về ApiRespone<PagedWishlistResultDto> với Result.Data
                    val wishlistItems = result.Data.map { it.toDomain(gson) }
                    Result.success(wishlistItems)
                } else {
                    Result.failure(Exception("Response body or Result is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Error ${response.code()}: ${errorBody ?: response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}", e))
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
                
                // Check MessageCode để xác định thành công
                if (body?.MessageCode?.contains("SUCCESS", ignoreCase = true) == true) {
                    val result = body.Result
                    
                    if (result != null) {
                        // Response trả về WishlistItemDto
                        val wishlistItem = WishlistItem(
                            id = result.Id,
                            itemId = result.ItemId,
                            itemType = result.ItemType,
                            addedAt = result.AddedAt,
                            item = result.Item?.let { 
                                when (result.ItemType) {
                                    0 -> gson.fromJson(it, BookDto::class.java)?.toDomain()
                                    1 -> gson.fromJson(it, CourseDto::class.java)?.toDomain()
                                    else -> null
                                }
                            }
                        )
                        Result.success(
                            com.example.khoitriso.domain.models.Wishlist(
                                items = listOf(wishlistItem),
                                totalItems = 1
                            )
                        )
                    } else {
                        // Thành công nhưng không có result - vẫn trả về success với wishlist rỗng
                        // Vì backend đã thêm thành công (dựa vào MessageCode)
                        Result.success(
                            com.example.khoitriso.domain.models.Wishlist(
                                items = emptyList(),
                                totalItems = 0
                            )
                        )
                    }
                } else {
                    Result.failure(Exception(body?.Message ?: "Không thể thêm vào yêu thích"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Error ${response.code()}: ${errorBody ?: response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}", e))
        }
    }

    override suspend fun removeFromWishlist(wishlistId: Int): Result<Unit> {
        return try {
            val response = wishlistApi.removeFromWishlist(wishlistId)

            if (response.isSuccessful) {
                val body = response.body()
                
                // Check MessageCode để xác định thành công
                if (body?.MessageCode?.contains("SUCCESS", ignoreCase = true) == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.Message ?: "Không thể xóa khỏi yêu thích"))
                }
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

