package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.OrderDto
import com.example.khoitriso.data.dto.PaymentResultDto
import com.example.khoitriso.data.request.CreateOrderRequest
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.request.GetOrderRequest

interface OrderRepository {
    suspend fun payment(request: CreateOrderRequest): Result<OrderDto>
    suspend fun processFreeOrder(orderId: Int, orderCode: String): Result<PaymentResultDto>
    suspend fun getOrderById(orderId: Int): Result<Order>
    suspend fun getMyOrder(getOrderRequest: GetOrderRequest): Result<MyResponese<Order>>
}

