package com.example.khoitriso.domain.usecase.category

import android.util.Log
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.repository.CategoryRepository


class GetCategory(private val repo: CategoryRepository) {
    suspend operator fun invoke(): List<Category> {
        val result = repo.getCategory()
        Log.d("Usecase", "invokeGetCategory: $result")

        return result.getOrElse{
            return emptyList()
        }
    }
}