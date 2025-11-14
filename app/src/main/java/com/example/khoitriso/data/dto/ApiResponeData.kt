package com.example.khoitriso.data.dto


data class ApiResponeData<T>(
    val Error: Any,
    val Message: String,
    val MessageCode: String,
    val Result: Result<T>?
)