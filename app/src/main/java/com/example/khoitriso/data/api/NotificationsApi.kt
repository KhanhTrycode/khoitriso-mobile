package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.NotificationDto
import com.example.khoitriso.data.dto.NotificationsResponse
import retrofit2.Response
import retrofit2.http.*

interface NotificationsApi {
    @GET("Notifications")
    suspend fun getUserNotifications(
        @Query("isRead") isRead: Boolean? = null,
        @Query("type") type: Int? = null,
        @Query("priority") priority: Int? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<ApiRespone<NotificationsResponse>>

    @GET("Notifications/{id}")
    suspend fun getNotificationById(@Path("id") id: Int): Response<ApiRespone<NotificationDto>>

    @PUT("Notifications/{id}/mark-read")
    suspend fun markAsRead(@Path("id") id: Int): Response<ApiRespone<Boolean>>

    @PUT("Notifications/mark-all-read")
    suspend fun markAllAsRead(): Response<ApiRespone<Int>> // Returns count of marked notifications
}