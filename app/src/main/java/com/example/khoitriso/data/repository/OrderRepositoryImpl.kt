package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.OrdersApi
import com.example.khoitriso.data.dto.OrderDto
import com.example.khoitriso.data.dto.PaymentResultDto
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.data.request.CreateOrderRequest
import com.example.khoitriso.data.request.PaymentRequest
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.repository.OrderRepository
import com.example.khoitriso.domain.request.GetOrderRequest
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val ordersApi: OrdersApi,
) : OrderRepository {

    override suspend fun createOrder(request: CreateOrderRequest): Result<Order> {
        return try {
            val response = ordersApi.createOrder(request)
            if (response.isSuccessful) {
                val orderDto = response.body()?.Result
                if (orderDto != null) {
                    Result.success(orderDto.toDomain())
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


    override suspend fun getOrderById(orderId: Int): Result<Order> {
        return try {
            val response = ordersApi.getOrderById(orderId)
            if (response.isSuccessful) {
                val result = response.body()?.Result
                if (result != null) {
                    Result.success(result.toDomain())
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

    override suspend fun getMyOrder(getOrderRequest: GetOrderRequest): Result<MyResponese<Order>> {
        return try {
            val response = ordersApi.getOrder(
                status = getOrderRequest.status,
                search = getOrderRequest.search,
                page = getOrderRequest.page,
                pageSize = getOrderRequest.pageSize
            )
            if (response.isSuccessful) {
                // Lấy ra đối tượng ApiResponeData<OrderDto> từ body
                val result = response.body()?.Result
                if (result != null) {
                    Result.success(result.toDomain { it.toDomain() })
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

    override suspend fun payment(orderId: Int, request: PaymentRequest): Result<String>{
        return try {
            val response = ordersApi.processPayment(
                orderId = orderId,
                request = request
            )
            if (response.isSuccessful) {
                // Lấy ra đối tượng ApiResponeData<OrderDto> từ body
                val result = response.body()?.Result
                if (result != null) {
                    Result.success(result.Message)
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
}

