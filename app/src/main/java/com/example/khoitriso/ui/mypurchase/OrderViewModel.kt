package com.example.khoitriso.ui.mypurchase

import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.usecase.order.OrderUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderUsecase: OrderUsecase
): BaseViewModel() {
    private val _orders = MutableStateFlow<UiState<MyResponese<Order>>>(UiState.Loading)
    val orders : StateFlow<UiState<MyResponese<Order>>> = _orders

    init {
        loadOrders()
    }

    fun loadOrders() {
        loadDataWithPage(
            stateFlow = _orders,
            mockData = MockData.mockOrders,
            apiCall = {
                orderUsecase.getOrder()
            }
        )
    }

}