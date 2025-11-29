package com.example.khoitriso.ui.cart

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.CartDto
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.repository.CartRepository
import com.example.khoitriso.domain.usecase.order.CartUsecase
import com.example.khoitriso.test.MockData.mockCart
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUsecase: CartUsecase
) : BaseViewModel() {

    private val _carts: MutableStateFlow<UiState<Carts>> = MutableStateFlow(UiState.Loading)
    val carts: StateFlow<UiState<Carts>> =_carts

    init {
        loadCart()
    }

    fun loadCart() {
        loadData(
            stateFlow = _carts,
            mockData = mockCart,
            apiCall = {
                cartUsecase.getCart()
            }
        )
    }

    fun removeItem(itemId: Int) {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isRemoving = itemId, error = null)
//            cartRepository.removeFromCart(itemId).fold(
//                onSuccess = {
//                    loadCart() // Reload cart after removal
//                },
//                onFailure = { exception ->
//                    _uiState.value = _uiState.value.copy(
//                        isRemoving = null,
//                        error = "Không thể xóa sản phẩm. Vui lòng thử lại sau."
//                    )
//                }
//            )
//        }
    }

    fun clearCart() {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isClearing = true, error = null)
//            cartRepository.clearCart().fold(
//                onSuccess = {
//                    _uiState.value = _uiState.value.copy(
//                        cart = null,
//                        isClearing = false
//                    )
//                },
//                onFailure = { exception ->
//                    _uiState.value = _uiState.value.copy(
//                        isClearing = false,
//                        error = "Không thể xóa giỏ hàng. Vui lòng thử lại sau."
//                    )
//                }
//            )
//        }
    }

    fun clearError() {
//        _uiState.value = _uiState.value.copy(error = null)
    }
}

