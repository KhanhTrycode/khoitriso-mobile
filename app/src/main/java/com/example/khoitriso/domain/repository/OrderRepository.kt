package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.CreateOrderRequest
import com.example.khoitriso.data.dto.OrderDto
import com.example.khoitriso.data.dto.PaymentRequest
import com.example.khoitriso.data.dto.PaymentResultDto

interface OrderRepository {
    suspend fun createOrder(request: CreateOrderRequest): Result<OrderDto>
    suspend fun processFreeOrder(orderId: Int, orderCode: String): Result<PaymentResultDto>
}

