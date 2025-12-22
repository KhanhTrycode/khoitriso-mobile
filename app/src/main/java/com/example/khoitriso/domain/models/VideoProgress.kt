package com.example.khoitriso.domain.models

data class VideoProgress(
    val isCompleted: Boolean,
    val lastWatchedAt: String,
    val videoDuration: Int,
    val videoPosition: Int,
    val watchPercentage: Int
)
