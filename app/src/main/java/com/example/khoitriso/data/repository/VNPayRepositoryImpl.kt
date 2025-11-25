package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.VNPayApi
import com.example.khoitriso.data.dto.VNPayPaymentRequest
import com.example.khoitriso.data.dto.VNPayPaymentResponse
import com.example.khoitriso.data.dto.VNPayQueryResponse
import com.example.khoitriso.domain.repository.VNPayRepository
import javax.inject.Inject

class VNPayRepositoryImpl @Inject constructor(
    private val vnpayApi: VNPayApi
) : VNPayRepository {

    override suspend fun createPaymentUrl(request: VNPayPaymentRequest): Result<VNPayPaymentResponse> {
        return try {
            val response = vnpayApi.createPaymentUrl(request)
            if (response.isSuccessful) {
                val result = response.body()?.Result
                if (result != null) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun queryTransaction(orderCode: String): Result<VNPayQueryResponse> {
        return try {
            val response = vnpayApi.queryTransaction(orderCode)
            if (response.isSuccessful) {
                val result = response.body()?.Result
                if (result != null) {
                    Result.success(result)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

