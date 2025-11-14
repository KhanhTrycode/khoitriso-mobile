package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.AdminApi
import com.example.khoitriso.domain.models.Book
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val adminApi: AdminApi
) {
}