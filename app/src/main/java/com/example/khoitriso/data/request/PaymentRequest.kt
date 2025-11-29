package com.example.khoitriso.data.request

data class PaymentRequest(
    val PaymentMethod: String,
    val PaymentGateway: String,
    val TransactionId: String? = null,
    val PaymentNotes: String? = null
)

