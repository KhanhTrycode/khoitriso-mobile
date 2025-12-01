package com.example.khoitriso.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.order.CartUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.test.MockData.mockCartItems
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.ItemType
import com.example.khoitriso.utils.UiEvent
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val bookUseCase: BookUsecase,
    private val cartUsecase: CartUsecase,
) : BaseViewModel() {

    private val _book = MutableStateFlow<UiState<BookDetail>>(UiState.Loading)
    val book: StateFlow<UiState<BookDetail>> = _book
    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()
    init {
        getBookById()
    }

    private fun getBookById() {

        val bookId = savedStateHandle.get<Int>("bookId") ?: -1
        loadData(
            _book, MockData.mockBookDetail1,
            apiCall = {
                bookUseCase.getBookById(bookId)
            }
        )
    }
    fun addToCart(itemId: Int) {
        viewModelScope.launch {
            val currentBook = (_book.value as? UiState.Success)?.data
            val result = if (_isTestMode) {
                Result.success(true)
            } else {
                cartUsecase.addToCart(itemId = itemId, itemType = ItemType.Book)
            }
            result.fold(
                onSuccess = {
                    // Chỉ cập nhật mock data khi ở chế độ test
                    if (_isTestMode && currentBook != null) {
                        val newCartItem = CartItem(
                            id = mockCartItems.size + 1,
                            itemId = itemId,
                            itemType = ItemType.Book,
                            price = currentBook.price,
                            coverImage = currentBook.coverImage,
                            title = currentBook.title
                        )
                        mockCartItems.add(newCartItem)
                        MockData.mockCart.cartItems = mockCartItems
                    }
                    // Gửi sự kiện thành công lên UI
                    _events.send(UiEvent.ShowSnackbar("Đã thêm vào giỏ hàng thành công!"))
                },
                onFailure = { error ->
                    // Gửi sự kiện thất bại lên UI
                    _events.send(UiEvent.ShowSnackbar("Lỗi: ${error.message}"))
                }
            )
        }
    }

    fun buyNow(){

    }
}


