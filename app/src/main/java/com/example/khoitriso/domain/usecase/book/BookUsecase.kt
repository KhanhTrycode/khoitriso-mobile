package com.example.khoitriso.domain.usecase.book

import android.util.Log
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.BookRepository
import com.example.khoitriso.domain.request.GetBookRequest
import javax.inject.Inject
import kotlin.getOrElse

data class BookUsecase (
    val getBook: GetBook,
    val getBookById: GetBookById,
    val searckBook: SearchBook
)
class GetBook(private val repo: BookRepository) {
    suspend operator fun invoke(getBookRequest: GetBookRequest? = null): Result<MyResponese<Book>> {
        return repo.getBooks(getBookRequest)
    }

}

class GetBookById(private val repo: BookRepository) {
    suspend operator fun invoke(id: Int): Result<BookDetail> {
        return repo.getBookById(id)
    }
}


class SearchBook(private val repo: BookRepository) {
    suspend operator fun invoke(query: String): List<Book> {
        val result = repo.searchBook(query)
        return result.getOrElse{
            return emptyList()
        }
    }
}


