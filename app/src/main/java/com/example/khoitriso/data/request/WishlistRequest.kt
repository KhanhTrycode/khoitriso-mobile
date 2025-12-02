package com.example.khoitriso.data.request

data class AddToWishlistRequestDto(
    val ItemId: Int,
    val ItemType: Int // 0: Book, 1: Course, 2: LearningPath
)

