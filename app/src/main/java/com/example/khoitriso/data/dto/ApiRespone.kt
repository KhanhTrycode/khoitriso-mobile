package com.example.khoitriso.data.dto


data class ApiRespone<T>(
    val Error: Any,
    val Message: String,
    val MessageCode: String,
    val Result: T?
)

