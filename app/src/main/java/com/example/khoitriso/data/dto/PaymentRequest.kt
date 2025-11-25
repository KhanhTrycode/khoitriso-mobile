package com.example.khoitriso.data.dto

data class PaymentRequest(
    val PaymentMethod: String,
    val PaymentGateway: String,
    val TransactionId: String? = null,
    val PaymentNotes: String? = null
)

