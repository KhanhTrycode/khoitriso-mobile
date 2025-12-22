package com.example.khoitriso.data.repository

import android.content.Context
import com.example.khoitriso.data.api.CartApi
import com.example.khoitriso.data.dto.AddToCartRequest
import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.CartDto
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.CartRepository
import com.example.khoitriso.utils.ErrorMessageHelper
import com.example.khoitriso.utils.ErrorType
import com.example.khoitriso.utils.MessageCode
import com.example.khoitriso.utils.MessageCodeHelper
import com.example.khoitriso.utils.debug
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi,
    @ApplicationContext private val context: Context,
) : CartRepository {

    override suspend fun getCart(): Result<Carts> {
        return try {
            val response = cartApi.getCart()
            if (response.isSuccessful) {
                val cartDto = response.body()?.Result
                if (cartDto != null) {
                    Result.success(cartDto.toDomain())
                } else {
                    Result.failure(Exception(
                        ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)
                    ))
                }
            } else {
                Result.failure(Exception(
                    ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())
                ))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToCart(itemId: Int, itemType: Int): Result<Int> {
        return try {
            val request = AddToCartRequest(ItemId = itemId, ItemType = itemType)
            val response = cartApi.addToCart(request)
            if (response.isSuccessful) {
                val cartDto = response.body()?.Result?.Id
                if (cartDto != null) {
                    Result.success(cartDto)
                } else {
                    Result.failure(Exception(
                        ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)
                    ))
                }
            } else {
                val errorBodyString = response.errorBody()?.string()
                val errorBody = Gson().fromJson(errorBodyString, ApiRespone::class.java)
                val messageCode = MessageCode.fromString(errorBody.MessageCode)
                val errorMessage = if (messageCode != MessageCode.UNKNOWN) {
                    MessageCodeHelper.getMessage(context, messageCode)
                } else {
                    ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())
                }
                Result.failure(Exception(errorMessage))
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
                Result.failure(Exception(
                    ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())
                ))
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
                Result.failure(Exception(
                    ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())
                ))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

