package com.example.khoitriso.domain.models

data class Order(
    val currency: String,
    val discountAmount: Int,
    val exchangeRate: Int,
    val finalAmount: Int,
    val id: Int,
    val orderCode: String,
    val orderNotes: String,
    val paidAt: String,
    val paymentGateway: String,
    val paymentMethod: String,
    val status: Int,
    val statusName: String,
    val taxAmount: Int,
    val totalAmount: Int,
    val transactionId: String,
    val userId: Int,
    val coupon: Coupon?,
    val items: List<Item>,
    val createdAt: String
)
