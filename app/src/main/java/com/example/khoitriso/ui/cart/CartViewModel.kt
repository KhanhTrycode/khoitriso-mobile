package com.example.khoitriso.ui.cart

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.CartDto
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.usecase.order.CartUsecase
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
            apiCall = {
                cartUsecase.getCart()
            }
        )
    }

    fun removeItem(itemId: Int) {
        viewModelScope.launch {
            cartUsecase.removeFromCart(itemId)
            loadCart()

        }
    }

    fun clearCart() {
        viewModelScope.launch {
            _carts.value = UiState.Loading
             cartUsecase.clearCart()
            loadCart()
        }
    }

    fun clearError() {
//        _uiState.value = _uiState.value.copy(error = null)
    }
}

