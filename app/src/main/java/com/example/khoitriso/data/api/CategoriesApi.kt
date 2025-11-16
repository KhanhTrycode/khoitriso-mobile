package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.CategoryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoriesApi {
    @GET("categories")
    suspend fun getCategories (
        @Query("parentId") parentId: Int? = null,
        @Query("includeInactive") includeInactive: Boolean? = null,
        @Query("includeCount") includeCount: Boolean? = null,

    ) : Response<ApiRespone<List<CategoryDto>>>
}