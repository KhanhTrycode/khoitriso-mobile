package com.example.khoitriso.domain.models

import com.example.khoitriso.data.dto.OptionDto

data class Question(
    val contextId: Int,
    val contextType: Int,
    val createdAt: String,
    val defaultPoints: Double,
    val difficultyLevel: Int,
    val id: Int,
    val isActive: Boolean,
    val options: List<Option>,
    val orderIndex: Int,
    val questionContent: String,
    val questionType: Int,
    val updatedAt: String,
)
