package com.example.khoitriso.data.repository

import android.util.Log
import com.example.khoitriso.data.api.CategoriesApi
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(private val api: CategoriesApi) : CategoryRepository {
    override suspend fun getCategory(): Result<List<Category>> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful){
                Result.success(response.body()?.Result?.map { it.toDomain() } ?: emptyList())
            }else{
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}