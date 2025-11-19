package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Coupon

data class CouponDto(
    val ApplicableItemIds: List<Int>,
    val ApplicableItemTypes: List<Int>,
    val Code: String,
    val Description: String,
    val DiscountType: Int,
    val DiscountTypeName: String,
    val DiscountValue: Int,
    val Id: Int,
    val IsActive: Boolean,
    val MaxDiscountAmount: Int,
    val MinOrderAmount: Int,
    val Name: String,
    val UpdatedAt: String,
    val UsageLimit: Int,
    val UsedCount: Int,
    val ValidFrom: String,
    val ValidTo: String
)

fun CouponDto.toDomain() = Coupon(
    applicableItemIds = ApplicableItemIds,
    applicableItemTypes = ApplicableItemTypes,
    code = Code,
    description = Description,
    discountType = DiscountType,
    discountTypeName = DiscountTypeName,
    discountValue = DiscountValue,
    id = Id,
    isActive = IsActive,
    maxDiscountAmount = MaxDiscountAmount,
    minOrderAmount = MinOrderAmount,
    name = Name,
    usageLimit = UsageLimit,
    usedCount = UsedCount,
    validFrom = ValidFrom,
    validTo = ValidTo
)