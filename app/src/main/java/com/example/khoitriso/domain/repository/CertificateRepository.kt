package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Certificate
import com.example.khoitriso.domain.models.MyResponese

interface CertificateRepository {
    suspend fun getMyCertificates(
        itemType: Int? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<MyResponese<Certificate>>

    suspend fun getCertificateById(id: Int): Result<Certificate>
    suspend fun generateCertificate(itemType: Int, itemId: Int): Result<Certificate>
    suspend fun getCertificateDownloadUrl(id: Int): Result<String>
    suspend fun verifyCertificate(number: String): Result<Certificate>
}

