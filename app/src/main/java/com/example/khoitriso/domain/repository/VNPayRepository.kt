package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.VNPayPaymentRequest
import com.example.khoitriso.data.dto.VNPayPaymentResponse
import com.example.khoitriso.data.dto.VNPayQueryResponse

interface VNPayRepository {
    suspend fun createPaymentUrl(request: VNPayPaymentRequest): Result<VNPayPaymentResponse>
    suspend fun queryTransaction(orderCode: String): Result<VNPayQueryResponse>
}

