package com.example.khoitriso.domain.usecase.book

import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.Chapter
import com.example.khoitriso.domain.models.MyBook
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.BookRepository
import kotlin.getOrElse

data class BookUsecase (
    val getBook: GetBook,
    val getBookById: GetBookById,
    val getMyBook: GetMyBook,
    val getChapterOfBook: GetChapterOfBook,
)

class GetChapterOfBook(private val repo: BookRepository) {
    suspend operator fun invoke(bookId: Int): Result<List<Chapter>> {
        return repo.getChaptersOfBook(bookId)
    }
}

class GetMyBook(private val repo: BookRepository) {
    suspend operator fun invoke(): Result<List<MyBook>> {
        return repo.getMyBooks()
    }

}


class GetBook(private val repo: BookRepository) {
    suspend operator fun invoke(getPagingRequest: PagingRequest = PagingRequest()): Result<MyResponese<Book>> {
        return repo.getBooks(getPagingRequest)
    }

}

class GetBookById(private val repo: BookRepository) {
    suspend operator fun invoke(id: Int): Result<BookDetail> {
        return repo.getBookById(id)
    }
}


