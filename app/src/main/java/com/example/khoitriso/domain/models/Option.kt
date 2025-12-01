package com.example.khoitriso.domain.models


data class Option(
    val id: Int,
    val optionText: String,
    val orderIndex: Int,
    val pointsValue: Int,
    val questionId: Int,
)