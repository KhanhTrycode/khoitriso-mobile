package com.example.khoitriso.domain.usecase.order

import com.example.khoitriso.data.dto.OrderDto
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.repository.OrderRepository
import com.example.khoitriso.domain.request.GetOrderRequest

data class OrderUsecase(
    val createOrder: CreateOrder,
    val getOrder: GetOrder,
    val getOrderById: GetOrderById,
    val paymentProgress: PaymentProgress
)

class CreateOrder(private val orderRepository: OrderRepository){
//    suspend operator fun invoke(): Result<OrderDto> {
//        return orderRepository.payment()
//    }
}

class GetOrder(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(getOrdersRequest: GetOrderRequest): Result<MyResponese<Order>> {
        return orderRepository.getMyOrder(getOrdersRequest)
    }
}

class GetOrderById(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(orderId: Int): Result<Order> {
        return orderRepository.getOrderById(orderId)
    }
}

class PaymentProgress(private val orderRepository: OrderRepository) {
//    suspend operator fun invoke(orderId: Int): Result<Order> {
//        return orderRepository.processFreeOrder(orderId)
//    }
}
