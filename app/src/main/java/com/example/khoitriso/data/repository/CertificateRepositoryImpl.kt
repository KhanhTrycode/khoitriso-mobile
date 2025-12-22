package com.example.khoitriso.data.repository

import android.content.Context
import com.example.khoitriso.data.api.CertificatesApi
import com.example.khoitriso.data.dto.request.CreateCertificateRequest
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Certificate
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.CertificateRepository
import com.example.khoitriso.utils.ErrorMessageHelper
import com.example.khoitriso.utils.ErrorType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CertificateRepositoryImpl @Inject constructor(
    private val certificatesApi: CertificatesApi,
    @ApplicationContext private val context: Context
) : CertificateRepository {

    override suspend fun getMyCertificates(
        itemType: Int?,
        page: Int,
        pageSize: Int
    ): Result<MyResponese<Certificate>> {
        return try {
            val response = certificatesApi.getMyCertificates(itemType, page, pageSize)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    val items = body.Items?.map { it.toDomain() } ?: emptyList()
                    Result.success(
                        MyResponese(
                            items = items,
                            page = body.Page ?: page,
                            pageSize = body.PageSize ?: pageSize,
                            total = body.Total ?: 0,
                            totalPages = body.TotalPages ?: 0
                        )
                    )
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCertificateById(id: Int): Result<Certificate> {
        return try {
            val response = certificatesApi.getCertificateById(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateCertificate(itemType: Int, itemId: Int): Result<Certificate> {
        return try {
            val request = CreateCertificateRequest(ItemType = itemType, ItemId = itemId)
            val response = certificatesApi.generateCertificate(request)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCertificateDownloadUrl(id: Int): Result<String> {
        return try {
            val response = certificatesApi.getCertificateDownloadUrl(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyCertificate(number: String): Result<Certificate> {
        return try {
            val response = certificatesApi.verifyCertificate(number)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.RESPONSE_BODY_NULL)))
                }
            } else {
                Result.failure(Exception(ErrorMessageHelper.getErrorMessage(context, ErrorType.API_ERROR, response.code())))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

