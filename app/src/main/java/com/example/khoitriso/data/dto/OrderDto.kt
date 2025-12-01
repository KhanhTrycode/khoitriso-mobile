package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Order

data class  OrderDto(
    val Currency: String,
    val DiscountAmount: Double,
    val ExchangeRate: Int,
    val FinalAmount: Double,
    val Id: Int,
    val OrderCode: String,
    val OrderNotes: String?,
    val PaidAt: String,
    val PaymentGateway: String,
    val PaymentMethod: String,
    val Status: Int,
    val StatusName: String,
    val TaxAmount: Double,
    val TotalAmount: Double,
    val TransactionId: String,
    val UserId: Int,
    val billingAddress: String?,
    val coupon: CouponDto?,
    val items: List<ItemDto>,
    val CreatedAt: String
)

fun OrderDto.toDomain() = Order(
    currency = Currency,
    discountAmount = DiscountAmount,
    exchangeRate = ExchangeRate,
    finalAmount = FinalAmount,
    id = Id,
    orderCode = OrderCode,
    orderNotes = OrderNotes?: "",
    paidAt = PaidAt,
    paymentGateway = PaymentGateway,
    paymentMethod = PaymentMethod,
    status = Status,
    statusName = StatusName,
    taxAmount = TaxAmount,
    totalAmount = TotalAmount,
    transactionId = TransactionId,
    userId = UserId,
    coupon = coupon?.toDomain(),
    items = items.map { it.toDomain() },
    createdAt = CreatedAt
)