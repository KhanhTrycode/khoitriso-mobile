package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.OrdersApi
import com.example.khoitriso.data.dto.CreateOrderRequest
import com.example.khoitriso.data.dto.OrderDto
import com.example.khoitriso.data.dto.PaymentRequest
import com.example.khoitriso.data.dto.PaymentResultDto
import com.example.khoitriso.domain.repository.OrderRepository
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val ordersApi: OrdersApi
) : OrderRepository {

    override suspend fun createOrder(request: CreateOrderRequest): Result<OrderDto> {
        return try {
            val response = ordersApi.createOrder(request)
            if (response.isSuccessful) {
                val orderDto = response.body()?.Result
                if (orderDto != null) {
                    Result.success(orderDto)
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

    override suspend fun processFreeOrder(orderId: Int, orderCode: String): Result<PaymentResultDto> {
        return try {
            val request = PaymentRequest(
                PaymentMethod = "FREE",
                PaymentGateway = "FREE",
                TransactionId = "FREE-$orderCode-${System.currentTimeMillis()}",
                PaymentNotes = "Đơn hàng miễn phí - Tự động hoàn thành"
            )
            val response = ordersApi.processPayment(orderId, request)
            if (response.isSuccessful) {
                val result = response.body()?.Result
                if (result != null) {
                    Result.success(result)
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

