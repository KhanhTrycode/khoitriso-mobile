package com.example.khoitriso.domain.models

data class Coupon(
    val applicableItemIds: List<Int>,
    val applicableItemTypes: List<Int>,
    val code: String,
    val description: String,
    val discountType: Int,
    val discountTypeName: String,
    val discountValue: Int,
    val id: Int,
    val isActive: Boolean,
    val maxDiscountAmount: Int,
    val minOrderAmount: Int,
    val name: String,
    val usageLimit: Int,
    val usedCount: Int,
    val validFrom: String,
    val validTo: String
)
