package com.example.khoitriso.ui.cart

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.CartDto
import com.example.khoitriso.domain.repository.CartRepository
import com.example.khoitriso.ui.behavior.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            cartRepository.getCart().fold(
                onSuccess = { cart ->
                    _uiState.value = _uiState.value.copy(
                        cart = cart,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Không thể tải giỏ hàng. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun removeItem(itemId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRemoving = itemId, error = null)
            cartRepository.removeFromCart(itemId).fold(
                onSuccess = {
                    loadCart() // Reload cart after removal
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isRemoving = null,
                        error = "Không thể xóa sản phẩm. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearing = true, error = null)
            cartRepository.clearCart().fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        cart = null,
                        isClearing = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isClearing = false,
                        error = "Không thể xóa giỏ hàng. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CartUiState(
    val cart: CartDto? = null,
    val isLoading: Boolean = false,
    val isRemoving: Int? = null,
    val isClearing: Boolean = false,
    val error: String? = null
)

