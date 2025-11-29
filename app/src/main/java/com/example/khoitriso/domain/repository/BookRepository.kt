package com.example.khoitriso.domain.repository

import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.Chapter
import com.example.khoitriso.domain.models.MyBook
import com.example.khoitriso.domain.models.MyResponese

interface BookRepository {
    suspend fun getBooks(getPagingRequest: PagingRequest?): Result<MyResponese<Book>>
    suspend fun getBookById(id: Int): Result<BookDetail>

    //    suspend fun getQuestionsOfBook(id: Int):
//    suspend fun getQuestionById(id: Int)
//    suspend fun getActivationCode(id: Int)
//    suspend fun validateActivationCode(code: String)
    suspend fun getMyBooks(): Result<List<MyBook>>
    suspend fun exportBookToWord(bookId: Int, includeExplanation: Boolean): Result<ByteArray>
    fun searchBook(string: String) : Result<List<Book>>
    suspend fun getChaptersOfBook(id: Int): Result<List<Chapter>>
}