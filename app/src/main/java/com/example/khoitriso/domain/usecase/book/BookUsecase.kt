package com.example.khoitriso.domain.usecase.book

import com.example.khoitriso.domain.repository.BookRepository
import javax.inject.Inject

data class BookUsecase (
    val getBook: GetBook
)