package com.example.khoitriso.data.dto

data class VideoProgressDto(
    val IsCompleted: Boolean,
    val LastWatchedAt: String,
    val VideoDuration: Int,
    val VideoPosition: Int,
    val WatchPercentage: Int
)