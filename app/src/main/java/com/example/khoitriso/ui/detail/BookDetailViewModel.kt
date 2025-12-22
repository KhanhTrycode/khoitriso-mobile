package com.example.khoitriso.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.order.CartUsecase
import com.example.khoitriso.domain.usecase.wishlist.WishlistUsecase
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
    private val wishlistUsecase: WishlistUsecase,
) : BaseViewModel() {

    private val _book = MutableStateFlow<UiState<BookDetail>>(UiState.Loading)
    val book: StateFlow<UiState<BookDetail>> = _book
    
    private val _isInWishlist = MutableStateFlow<Boolean?>(null)
    val isInWishlist: StateFlow<Boolean?> = _isInWishlist
    
    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()
    
    init {
        getBookById()
    }

    private fun getBookById() {
        val bookId = savedStateHandle.get<Int>("bookId") ?: -1
        loadData(
            _book,
            apiCall = {
                bookUseCase.getBookById(bookId)
            },
            onSuccess = {
                checkWishlistStatus(bookId)
            }
        )
    }
    
    fun checkWishlistStatus(bookId: Int) {
        viewModelScope.launch {
            wishlistUsecase.isInWishlist(bookId, ItemType.Book).fold(
                onSuccess = { isInWishlist ->
                    _isInWishlist.value = isInWishlist
                },
                onFailure = { }
            )
        }
    }
    
    fun toggleWishlist(bookId: Int) {
        viewModelScope.launch {
            val currentStatus = _isInWishlist.value ?: false
            val result = if (currentStatus) {
                wishlistUsecase.removeItemFromWishlist(bookId, ItemType.Book)
            } else {
                wishlistUsecase.addToWishlist(bookId, ItemType.Book)
            }
            result.fold(
                onSuccess = {
                    // Reload status từ server sau khi thêm/xóa thành công
                    checkWishlistStatus(bookId)
                    _events.send(UiEvent.ShowSnackbar(
                        if (!currentStatus) "Đã thêm vào yêu thích!" else "Đã xóa khỏi yêu thích!"
                    ))
                },
                onFailure = { error ->
                    val message = when {
                        error.message?.contains("409") == true || 
                        error.message?.contains("đã có") == true ||
                        error.message?.contains("already exists") == true -> {
                            _isInWishlist.value = true
                            "Item đã có trong danh sách yêu thích"
                        }
                        else -> "Lỗi: ${error.message}"
                    }
                    _events.send(UiEvent.ShowSnackbar(message))
                }
            )
        }
    }
    fun addToCart(itemId: Int) {
        viewModelScope.launch {
            cartUsecase.addToCart(itemId = itemId, itemType = ItemType.Book).fold(
                onSuccess = {
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


