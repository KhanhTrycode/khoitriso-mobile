package com.example.khoitriso.domain.models


import android.os.Parcelable

data class Chapter(
    val BookId: Int,
    val CreatedAt: String,
    val Description: String,
    val Id: Int,
    val OrderIndex: Int,
    val QuestionCount: Int,
    val Questions: List<Any>,
    val Title: String,
    val UpdatedAt: String
)