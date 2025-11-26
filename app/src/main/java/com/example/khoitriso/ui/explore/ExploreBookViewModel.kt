package com.example.khoitriso.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.BookRepository
import com.example.khoitriso.domain.request.GetBookRequest
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreBookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val savedStateHandle: SavedStateHandle,
    private val bookUsecase: BookUsecase,
) : BaseViewModel() {

    private val _books = MutableStateFlow<UiState<MyResponese<Book>>>(UiState.Loading)
    val book: StateFlow<UiState<MyResponese<Book>>> = _books

    init {
        getBook()
    }

    private fun getBook() {
        loadData(
            _books, MyResponese<Book>(
                items = MockData.mockBooks,
                page = 1,
                pageSize = MockData.mockBooks.size / 2,
                total = MockData.mockBooks.size,
                totalPages = 10,
            ),
            apiCall = {
                bookUsecase.getBook()
            })
    }

    fun changePage(page: Int) {
        loadData(
            _books, MyResponese<Book>(
                items = MockData.mockBooks,
                page = page,
                pageSize = MockData.mockBooks.size / 2,
                total = MockData.mockBooks.size,
                totalPages = 10
            ),
            apiCall = {
                bookUsecase.getBook(
                    GetBookRequest(page)
                )
            }
        )
    }

}


