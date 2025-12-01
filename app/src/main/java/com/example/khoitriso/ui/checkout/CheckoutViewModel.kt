package com.example.khoitriso.ui.checkout

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.request.CreateOrderRequest
import com.example.khoitriso.data.request.VNPayPaymentRequest
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.repository.CartRepository
import com.example.khoitriso.domain.repository.OrderRepository
import com.example.khoitriso.domain.repository.VNPayRepository
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

// Data class quản lý trạng thái riêng của màn hình Checkout (không bao gồm dữ liệu Cart)
data class CheckoutState(
    val couponCode: String = "",
    val discountAmount: Double = 0.0,
    val isProcessing: Boolean = false,
    val paymentUrl: String? = null,
    val orderCode: String? = null,
    val error: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val vnpayRepository: VNPayRepository
) : BaseViewModel() {

    // 1. State quản lý dữ liệu Giỏ hàng
    private val _cart = MutableStateFlow<UiState<Carts>>(UiState.Loading)
    val cart: StateFlow<UiState<Carts>> = _cart.asStateFlow()

    // 2. State quản lý hành động Checkout (Input, Loading, Result)
    private val _checkoutState = MutableStateFlow(CheckoutState())
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        // Mock data loading
        _cart.value = UiState.Success(MockData.mockCart)

        /* // Real API implementation
        viewModelScope.launch {
            _cart.value = UiState.Loading
            cartRepository.getCart().fold(
                onSuccess = { _cart.value = UiState.Success(it) },
                onFailure = { _cart.value = UiState.Error(it.message ?: "Lỗi tải giỏ hàng") }
            )
        }
        */
    }

    fun setCouponCode(code: String) {
        _checkoutState.update { it.copy(couponCode = code) }
    }

    fun checkout(onPaymentUrlReady: (String) -> Unit) {
        val currentCartState = _cart.value
        if (currentCartState !is UiState.Success) return

        val cartData = currentCartState.data

        viewModelScope.launch {
            _checkoutState.update { it.copy(isProcessing = true, error = null) }

            // Step 1: Prepare Data
            val cartItemIds = cartData.cartItems.map { it.id } // Lưu ý: cartItems chữ thường (Domain model)
            val discount = _checkoutState.value.discountAmount
            val finalTotal = max(0.0, cartData.totalPrice - discount)

            val orderRequest = CreateOrderRequest(
                CartItemIds = cartItemIds,
                CouponCode = _checkoutState.value.couponCode.ifEmpty { null },
                PaymentMethod = if (finalTotal <= 0) "FREE" else "VNPAY",
                PaymentGateway = if (finalTotal <= 0) "FREE" else "VNPAY"
            )

            // Step 2: Create Order
            orderRepository.payment(orderRequest).fold(
                onSuccess = { order ->
                    // Check Logic thanh toán
                    val orderTotal = if (order.FinalAmount > 0) order.FinalAmount else order.TotalAmount

                    if (finalTotal <= 0 || orderTotal <= 0) {
                        // Case: Miễn phí -> Xử lý luôn
                        processFreeOrder(order.Id, order.OrderCode)
                    } else {
                        // Case: Có phí -> Tạo URL VNPay
                        createVNPayUrl(order.Id, order.OrderCode, onPaymentUrlReady)
                    }
                },
                onFailure = { exception ->
                    _checkoutState.update {
                        it.copy(isProcessing = false, error = exception.message ?: "Lỗi tạo đơn hàng")
                    }
                }
            )
        }
    }

    private suspend fun processFreeOrder(orderId: Int, orderCode: String) {
        orderRepository.processFreeOrder(orderId, orderCode).fold(
            onSuccess = {
                _checkoutState.update {
                    it.copy(isProcessing = false, orderCode = it.orderCode)
                }
            },
            onFailure = { exception ->
                _checkoutState.update {
                    it.copy(isProcessing = false, error = exception.message)
                }
            }
        )
    }

    private suspend fun createVNPayUrl(orderId: Int, orderCode: String, onPaymentUrlReady: (String) -> Unit) {
        val vnpayRequest = VNPayPaymentRequest(
            OrderId = orderId,
            OrderDescription = "Thanh toan don hang $orderCode"
        )
        vnpayRepository.createPaymentUrl(vnpayRequest).fold(
            onSuccess = { response ->
                _checkoutState.update {
                    it.copy(
                        isProcessing = false,
                        paymentUrl = response.PaymentUrl,
                        orderCode = orderCode
                    )
                }
                onPaymentUrlReady(response.PaymentUrl)
            },
            onFailure = { exception ->
                _checkoutState.update {
                    it.copy(isProcessing = false, error = exception.message)
                }
            }
        )
    }

    fun clearError() {
        _checkoutState.update { it.copy(error = null) }
    }
}