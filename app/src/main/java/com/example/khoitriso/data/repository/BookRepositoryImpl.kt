package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.BooksApi
import com.example.khoitriso.data.dto.BookDto
import com.example.khoitriso.data.dto.toDetailDomain
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BooksApi

) : BookRepository {
    override suspend fun getBooks(): Result<MyResponese<Book>> { // Sửa kiểu trả về cho khớp
        return try {
            val response = bookApi.getBooks()
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
                Result.success(response.body()?.Result?.toDetailDomain() ?: throw Exception("Book not found"))
                } else {
                throw Exception("Api error: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyBook(): Result<List<Book>> {
        TODO("Not yet implemented")
    }

    override fun searchBook(string: String): Result<List<Book>> {
        TODO("Not yet implemented")
    }
}