package com.example.khoitriso.data.dto.response

data class ActivateCodeResponse(
    val IsValid: Boolean,
    val IsUsed: Boolean,
    val BookTitle:String?,
    val UserByFullName:String?,
)
