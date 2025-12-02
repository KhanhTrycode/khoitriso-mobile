package com.example.khoitriso.ui.mypurchase

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.request.VNPayPaymentRequest
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.usecase.order.OrderUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.OrderStatus
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderUsecase: OrderUsecase
): BaseViewModel() {
    private val _orders = MutableStateFlow<UiState<MyResponese<Order>>>(UiState.Loading)
    val orders : StateFlow<UiState<MyResponese<Order>>> = _orders.asStateFlow()

    private val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl.asStateFlow()
    
    private val _paymentError = MutableStateFlow<String?>(null)
    val paymentError: StateFlow<String?> = _paymentError.asStateFlow()

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

    fun continuePayment(order: Order) {
        viewModelScope.launch {
            // Kiểm tra order status - chỉ tiếp tục thanh toán nếu order chưa thanh toán
            if (order.status == OrderStatus.Paid.value || order.status == OrderStatus.Cancelled.value) {
                return@launch
            }

            _paymentError.value = null
            _paymentUrl.value = null

            // Tạo lại VNPay URL cho order đã có
            val vnpayRequest = VNPayPaymentRequest(
                OrderId = order.id,
                OrderDescription = "Thanh toan don hang ${order.orderCode}"
            )
            
            orderUsecase.vnpayCreatePaymentUrl(vnpayRequest).fold(
                onSuccess = { response ->
                    _paymentUrl.value = response.PaymentUrl
                },
                onFailure = { exception ->
                    _paymentError.value = exception.message ?: "Không thể tạo URL thanh toán"
                }
            )
        }
    }
    
    fun clearPaymentUrl() {
        _paymentUrl.value = null
        _paymentError.value = null
    }
}