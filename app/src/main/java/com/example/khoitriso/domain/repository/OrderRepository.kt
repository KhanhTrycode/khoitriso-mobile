package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.request.CreateOrderRequest
import com.example.khoitriso.data.request.PaymentRequest
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.request.GetOrderRequest

interface OrderRepository {
    suspend fun createOrder(request: CreateOrderRequest): Result<Order>
    suspend fun getOrderById(orderId: Int): Result<Order>
    suspend fun getMyOrder(getOrderRequest: GetOrderRequest): Result<MyResponese<Order>>
    suspend fun payment(orderId: Int, request: PaymentRequest): Result<String>
}

