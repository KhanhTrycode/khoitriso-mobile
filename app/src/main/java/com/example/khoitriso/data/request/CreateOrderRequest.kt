package com.example.khoitriso.data.request

data class CreateOrderRequest(
    val CartItemIds: List<Int>,
    val CouponCode: String? = null,
    val PaymentMethod: String = "VNPAY",
    val PaymentGateway: String = "VNPAY",
    val BillingAddress: String? = null,
    val OrderNotes: String? = null
)

