package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.BooksApi
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BooksApi

) : BookRepository {
    override suspend fun getBooks(): Result<List<Book>> {
        return try {
            val response = bookApi.getBooks()
            if (response.isSuccessful) {
                Result.success(response.body()?.Result?.Items?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookById(id: Int): Book? {
        TODO("Not yet implemented")
    }

    override suspend fun getMyBook(): List<Book> {
        TODO("Not yet implemented")
    }
}