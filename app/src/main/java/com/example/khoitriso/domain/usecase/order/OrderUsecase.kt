package com.example.khoitriso.domain.usecase.order

import com.example.khoitriso.data.dto.VNPayPaymentResponse
import com.example.khoitriso.data.dto.VNPayQueryResponse
import com.example.khoitriso.data.request.CreateOrderRequest
import com.example.khoitriso.data.request.PaymentRequest
import com.example.khoitriso.data.request.VNPayPaymentRequest
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.repository.OrderRepository
import com.example.khoitriso.domain.repository.VNPayRepository
import com.example.khoitriso.domain.request.GetOrderRequest
import com.example.khoitriso.utils.OrderStatus

data class OrderUsecase(
    val createOrder: CreateOrder,
    val getOrder: GetOrder,
    val getOrderById: GetOrderById,
    val paymentFreeOrder: PaymentFreeOrder,
    val paymentProgress: PaymentProgress,
    val vnpayCreatePaymentUrl: VNPayCreatePaymentUrl,
    val queryTransaction: QueryTransaction
)

class QueryTransaction(private val vnPayRepository: VNPayRepository) {
    suspend operator fun invoke(orderCode: String): Result<VNPayQueryResponse> {
        return vnPayRepository.queryTransaction(orderCode)
    }
}

class VNPayCreatePaymentUrl(private val vnPayRepository: VNPayRepository) {
    suspend operator fun invoke(request: VNPayPaymentRequest): Result<VNPayPaymentResponse> {
        return vnPayRepository.createPaymentUrl(request)
    }
}

class CreateOrder(private val orderRepository: OrderRepository){
    suspend operator fun invoke(createOrderRequest: CreateOrderRequest): Result<Order> {
        return orderRepository.createOrder(createOrderRequest)
    }
}

class GetOrder(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(getOrdersRequest: GetOrderRequest? = null):
            Result<MyResponese<Order>> {
        if(getOrdersRequest == null) return orderRepository.getMyOrder(GetOrderRequest(status = OrderStatus.Pending.value))
        return orderRepository.getMyOrder(getOrdersRequest)
    }
}

class GetOrderById(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(orderId: Int): Result<Order> {
        return orderRepository.getOrderById(orderId)
    }
}

class PaymentFreeOrder(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(cartItemIds: List<Int>): Result<Order> {
        val createOrderRequest = CreateOrderRequest(
            CartItemIds = cartItemIds,
            PaymentMethod = "FREE",
            PaymentGateway = "FREE"
        )
        return orderRepository.createOrder(createOrderRequest)
    }
}

class PaymentProgress(private val orderRepository: OrderRepository){
    suspend operator fun invoke(orderId: Int,request: PaymentRequest): Result<String> {
        return orderRepository.payment(orderId,request)
    }
}

