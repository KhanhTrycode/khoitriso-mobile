package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.MyResponese

interface BookRepository {
    suspend fun getBooks(): Result<MyResponese<Book>>
    suspend fun getBookById(id: Int): Result<BookDetail>

    //    suspend fun getQuestionsOfBook(id: Int):
//    suspend fun getQuestionById(id: Int)
//    suspend fun getActivationCode(id: Int)
//    suspend fun validateActivationCode(code: String)
    suspend fun getMyBook(): Result<List<Book>>
    fun searchBook(string: String) : Result<List<Book>>
//    suspend fun getChaptersOfBook(id: Int)
}