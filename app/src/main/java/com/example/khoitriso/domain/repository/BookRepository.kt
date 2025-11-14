package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Book

interface BookRepository {
    suspend fun getBooks(): Result<List<Book>>
    suspend fun getBookById(id: Int): Book?

    //    suspend fun getQuestionsOfBook(id: Int):
//    suspend fun getQuestionById(id: Int)
//    suspend fun getActivationCode(id: Int)
//    suspend fun validateActivationCode(code: String)
    suspend fun getMyBook(): List<Book>
//    suspend fun getChaptersOfBook(id: Int)
}