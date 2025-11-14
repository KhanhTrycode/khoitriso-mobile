package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.domain.models.Book
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApi {
    @GET("books")
    suspend fun getBooks(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int =20,
        @Query("search") search: String?= null,
        @Query("approvalStatus") approvalStatus: Int? = null,
        @Query("authorId") authorId: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
    ): Response<ApiResponeData<Book>>
}