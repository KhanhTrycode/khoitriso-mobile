package com.example.khoitriso.domain.models

data class Order(
    val currency: String,
    val discountAmount: Double,
    val exchangeRate: Int,
    val finalAmount: Double,
    val id: Int,
    val orderCode: String,
    val orderNotes: String,
    val paidAt: String,
    val paymentGateway: String,
    val paymentMethod: String,
    val status: Int,
    val statusName: String,
    val taxAmount: Double,
    val totalAmount: Double,
    val transactionId: String,
    val userId: Int,
    val coupon: Coupon?,
    val items: List<OrderItem>,
    val createdAt: String
)
