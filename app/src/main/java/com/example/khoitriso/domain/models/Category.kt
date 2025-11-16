package com.example.khoitriso.domain.models

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String,
    val isActive: Boolean,
    val orderIndex: Int,
    val parent: Category?,
)