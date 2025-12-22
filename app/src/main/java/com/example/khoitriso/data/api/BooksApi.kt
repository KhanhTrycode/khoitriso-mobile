package com.example.khoitriso.data.api

import com.example.khoitriso.data.dto.ApiRespone
import com.example.khoitriso.data.dto.ApiResponeData
import com.example.khoitriso.data.dto.BookDetailDto
import com.example.khoitriso.data.dto.BookDto
import com.example.khoitriso.data.dto.BookQuestionDto
import com.example.khoitriso.data.dto.ChapterDto
import com.example.khoitriso.data.dto.MyBookDto
import com.example.khoitriso.data.dto.response.ActivateCodeResponse
import com.example.khoitriso.domain.models.BookDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApi {
    @GET("books")
    suspend fun getBooks(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int =20,
        @Query("search") search: String?= null,
        @Query("approvalStatus") approvalStatus: Int? = 2,
        @Query("authorId") authorId: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
    ): Response<ApiResponeData<BookDto>>

    @GET("books/{id}")
    suspend fun getBookById(
        @Path("id") bookId: Int
    ) : Response<ApiRespone<BookDetailDto>>

    @GET("books/my-books")
    suspend fun getMyBooks(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponeData<MyBookDto>>

    @GET("books/{id}/export-word/my-purchase")
    suspend fun exportBookToWord(
        @Path("id") bookId: Int,
        @Query("includeExplanation") includeExplanation: Boolean = false
    ): Response<okhttp3.ResponseBody> // Returns file download

    @GET("books/{id}/chapters")
    suspend fun getChapterOfBook(
        @Path("id") bookId: Int
    ) : Response<ApiRespone<List<ChapterDto>>>

    @GET("books/activation-codes/{code}/validate")
    suspend fun activeBook(
        @Path("code") code: String
    ): Response<ApiRespone<ActivateCodeResponse>>

    /**
     * Get book question by ID with solutions
     * Endpoint: GET /api/solutions?questionId={id}
     */
    @GET("solutions")
    suspend fun getBookQuestionById(
        @Query("questionId") questionId: Int
    ): Response<ApiRespone<BookQuestionDto>>
}