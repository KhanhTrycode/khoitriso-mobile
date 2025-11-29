package com.example.khoitriso.domain.request

data class GetOrderRequest(
    val status: Int,
    val search: String? = null,
    val page: Int = 1,
    val pageSize: Int = 20
)