package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.VNPayPaymentResponse
import com.example.khoitriso.data.dto.VNPayQueryResponse
import com.example.khoitriso.data.request.VNPayPaymentRequest

interface VNPayRepository {
    suspend fun createPaymentUrl(request: VNPayPaymentRequest): Result<VNPayPaymentResponse>
    suspend fun queryTransaction(orderCode: String): Result<VNPayQueryResponse>
}

