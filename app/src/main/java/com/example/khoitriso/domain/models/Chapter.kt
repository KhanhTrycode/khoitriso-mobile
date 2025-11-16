package com.example.khoitriso.domain.models


import android.os.Parcelable

data class Chapter(
    val bookId: Int,
    val createdAt: String,
    val description: String,
    val id: Int,
    val orderIndex: Int,
    val questionCount: Int,
    val questions: List<Any>,
    val title: String,
    val updatedAt: String
)