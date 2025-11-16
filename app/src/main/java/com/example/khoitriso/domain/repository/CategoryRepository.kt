package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Category

interface CategoryRepository {
    suspend fun getCategory(): Result<List<Category>>
}
