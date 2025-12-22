package com.example.khoitriso.data.dto

import androidx.compose.foundation.pager.PageSize
import com.example.khoitriso.domain.models.MyCart
import com.example.khoitriso.domain.models.MyResponese

data class ResultRespone<T>(
    val Items: List<T>?,
    val Data: List<T>?, // Alternative field name for some APIs
    val CartItems: List<T>?,
    val Page: Int,
    val PageSize: Int,
    val Total: Int,
    val TotalPages: Int
)

fun <DTO, DOMAIN> ResultRespone<DTO>.toDomain(transform: (DTO) -> DOMAIN): MyResponese<DOMAIN> = MyResponese(
    items = (this.Items ?: this.Data)?.map(transform) ?: emptyList(),
    page = this.Page,
    pageSize = this.PageSize,
    total = this.Total,
    totalPages = this.TotalPages
)

fun <T> ResultRespone<T>.toCartDomain() = MyCart(
    items = CartItems ?: emptyList(),
    total = Total
)