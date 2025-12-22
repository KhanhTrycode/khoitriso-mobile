package com.example.khoitriso.ui.explore

import androidx.lifecycle.SavedStateHandle
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ExploreBookViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val bookUsecase: BookUsecase,
) : BaseViewModel() {

    private val _books = MutableStateFlow<UiState<MyResponese<Book>>>(UiState.Loading)
    val book: StateFlow<UiState<MyResponese<Book>>> = _books

    init {
        getBook()
    }

    private fun getBook() {
        loadDataWithPage(
            _books,
            apiCall = {
                bookUsecase.getBook()
            })
    }

    fun changePage(page: Int) {
        loadDataWithPage(
            _books,
            apiCall = {
                bookUsecase.getBook(
                    PagingRequest(page)
                )
            }
        )
    }

}


