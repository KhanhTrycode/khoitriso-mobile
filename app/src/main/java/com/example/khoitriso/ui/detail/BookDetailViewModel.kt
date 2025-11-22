package com.example.khoitriso.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val bookUseCase: BookUsecase,
) : BaseViewModel() {

    private val _book = MutableStateFlow<UiState<BookDetail>>(UiState.Loading)
    val book: StateFlow<UiState<BookDetail>> = _book

    init {
        getBookById()
    }

    private fun getBookById() {

        val bookId = savedStateHandle.get<Int>("bookId") ?: -1
        loadData(
            _book, MockData.bookDetails[1],
            apiCall = {
                bookUseCase.getBookById(bookId)
            }
        )
    }
}


