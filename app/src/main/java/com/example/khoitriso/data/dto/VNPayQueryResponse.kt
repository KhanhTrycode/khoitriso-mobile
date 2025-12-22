package com.example.khoitriso.data.dto

data class VNPayQueryResponse(
    val OrderCode: String,
    val TransactionStatus: String,
    val ResponseCode: String,
    val Message: String? = null
)

