package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.BooksApi
import com.example.khoitriso.data.dto.BookDto
import com.example.khoitriso.data.dto.MyBookDto
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.Chapter
import com.example.khoitriso.domain.models.MyBook
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.BookRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BooksApi,
    private val gson: Gson
) : BookRepository {
    override suspend fun getBooks(getPagingRequest: PagingRequest?):
            Result<MyResponese<Book>> { // Sửa kiểu trả về cho khớp
        return try {
            val response = if (getPagingRequest != null){
                 bookApi.getBooks(
                    page = getPagingRequest.page,
                    pageSize = getPagingRequest.pageSize,
                    search = getPagingRequest.search,
                    approvalStatus = getPagingRequest.approvalStatus,
                    authorId = getPagingRequest.authorId,
                    sortBy = getPagingRequest.sortBy,
                    sortOrder = getPagingRequest.sortOrder
                )
            } else bookApi.getBooks()

            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain(BookDto::toDomain))
                } else {
                    Result.failure(Exception("Response body or Result is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookById(id: Int): Result<BookDetail> {
        return try{
            val response = bookApi.getBookById(id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception("Response body or Result is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyBooks(): Result<List<MyBook>> {
        return try {
            val response = bookApi.getMyBooks(page = 1, pageSize = 100)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    // Parse using Gson
                    Result.success(body.Items?.map { it.toDomain() }?: emptyList())
                } else {
                    Result.failure(Exception("Response body or Result is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportBookToWord(bookId: Int, includeExplanation: Boolean): Result<ByteArray> {
        return try {
            val response = bookApi.exportBookToWord(bookId, includeExplanation)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body.bytes())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchBook(string: String): Result<List<Book>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChaptersOfBook(id: Int): Result<List<Chapter>> {
        return try {
            val response = bookApi.getChapterOfBook(bookId = id)
            if (response.isSuccessful) {
                val body = response.body()?.Result
                if (body != null) {
                    Result.success(body.map { it.toDomain() })
                } else {
                    Result.failure(Exception("Response body or Result is null"))
                }
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}