package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.NotificationsApi
import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Notification
import com.example.khoitriso.domain.repository.NotificationRepository
import com.example.khoitriso.domain.repository.NotificationsResult
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationsApi: NotificationsApi
) : NotificationRepository {

    override suspend fun getUserNotifications(
        isRead: Boolean?,
        type: Int?,
        priority: Int?,
        page: Int,
        pageSize: Int
    ): Result<NotificationsResult> {
        return try {
            val response = notificationsApi.getUserNotifications(
                isRead = isRead,
                type = type,
                priority = priority,
                page = page,
                pageSize = pageSize
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                
                if (result != null) {
                    val items = (result.Data ?: emptyList()).map { it.toDomain() }
                    
                    Result.success(
                        NotificationsResult(
                            items = items,
                            total = result.Total ?: 0,
                            unreadCount = result.UnreadCount ?: 0,
                            page = result.Page ?: page,
                            pageSize = result.PageSize ?: pageSize
                        )
                    )
                } else {
                    Result.failure(Exception("Response data is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNotificationById(id: Int): Result<Notification> {
        return try {
            val response = notificationsApi.getNotificationById(id)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Notification not found"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(id: Int): Result<Boolean> {
        return try {
            val response = notificationsApi.markAsRead(id)
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                Result.success(result == true)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAllAsRead(): Result<Int> {
        return try {
            val response = notificationsApi.markAllAsRead()
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                Result.success(result ?: 0)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
