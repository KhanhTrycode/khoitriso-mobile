package com.example.khoitriso.domain.usecase.book

import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.repository.BookRepository

class GetBook(private val repo: BookRepository) {
    suspend operator fun invoke(): List<Book>? {
        val result = repo.getBooks()
        return result.getOrNull()
    }

}
