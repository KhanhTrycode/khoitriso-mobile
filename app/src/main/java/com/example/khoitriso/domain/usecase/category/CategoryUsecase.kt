package com.example.khoitriso.domain.usecase.category

import android.util.Log
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.repository.CategoryRepository

data class CategoryUsecase(
    val getCategory: GetCategory
)
class GetCategory(private val repo: CategoryRepository) {
    suspend operator fun invoke(): Result<List<Category>> {
        return repo.getCategory()
    }
}