package com.example.khoitriso.domain.models

data class Instructor(
    val avatar: String,
    val bio: String = "Test",
    val id: Int,
    val name: String
)