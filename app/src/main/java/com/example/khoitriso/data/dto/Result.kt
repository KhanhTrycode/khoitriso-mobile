package com.example.khoitriso.data.dto

data class Result<T>(
    val Items: List<T>,
    val CartItems: List<T>,
    val Page: Int,
    val PageSize: Int,
    val Total: Int,
    val TotalPages: Int
)
