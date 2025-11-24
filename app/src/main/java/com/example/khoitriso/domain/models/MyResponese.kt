package com.example.khoitriso.domain.models

data class MyResponese<T> (
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
)

data class MyCart<T> (
    val items: List<T>,
    val total: Int,
)

