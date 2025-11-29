package com.example.khoitriso.ui.checkout

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.CartDto
import com.example.khoitriso.data.request.CreateOrderRequest
import com.example.khoitriso.data.request.VNPayPaymentRequest
import com.example.khoitriso.domain.repository.CartRepository
import com.example.khoitriso.domain.repository.OrderRepository
import com.example.khoitriso.domain.repository.VNPayRepository
import com.example.khoitriso.ui.behavior.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val vnpayRepository: VNPayRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
//            cartRepository.getCart().fold(
//                onSuccess = { cart ->
//                    _uiState.value = _uiState.value.copy(
//                        cart = cart,
//                        isLoading = false
//                    )
//                },
//                onFailure = { exception ->
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        error = exception.message ?: "Không thể tải giỏ hàng"
//                    )
//                }
//            )
//        }
    }

    fun checkout(onPaymentUrlReady: (String) -> Unit) {
        viewModelScope.launch {
            val cart = _uiState.value.cart ?: return@launch
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)

            // Step 1: Create order
            val cartItemIds = cart.CartItems.map { it.Id }
            val discount = _uiState.value.discountAmount ?: 0.0
            val finalTotal = maxOf(0.0, cart.TotalAmount - discount)

            val orderRequest = CreateOrderRequest(
                CartItemIds = cartItemIds,
                CouponCode = _uiState.value.couponCode.ifEmpty { null },
                PaymentMethod = if (finalTotal <= 0) "FREE" else "VNPAY",
                PaymentGateway = if (finalTotal <= 0) "FREE" else "VNPAY"
            )

            orderRepository.payment(orderRequest).fold(
                onSuccess = { order ->
                    // Step 2: Check if free order
                    // Use FinalAmount if available, otherwise TotalAmount
                    val orderTotal = order.FinalAmount.takeIf { it > 0 } ?: order.TotalAmount
                    if (finalTotal <= 0 || orderTotal <= 0) {
                        // Process free order
                        orderRepository.processFreeOrder(order.Id, order.OrderCode).fold(
                            onSuccess = {
                                _uiState.value = _uiState.value.copy(
                                    isProcessing = false,
                                    orderCode = it.OrderCode
                                )
                                // Navigate to success
                            },
                            onFailure = { exception ->
                                _uiState.value = _uiState.value.copy(
                                    isProcessing = false,
                                    error = exception.message ?: "Không thể xử lý đơn hàng miễn phí"
                                )
                            }
                        )
                    } else {
                        // Step 3: Create VNPay payment URL
                        val vnpayRequest = VNPayPaymentRequest(
                            OrderId = order.Id,
                            OrderDescription = "Thanh toan don hang ${order.OrderCode}"
                        )
                        vnpayRepository.createPaymentUrl(vnpayRequest).fold(
                            onSuccess = { response ->
                                _uiState.value = _uiState.value.copy(
                                    isProcessing = false,
                                    paymentUrl = response.PaymentUrl,
                                    orderCode = order.OrderCode
                                )
                                onPaymentUrlReady(response.PaymentUrl)
                            },
                            onFailure = { exception ->
                                _uiState.value = _uiState.value.copy(
                                    isProcessing = false,
                                    error = exception.message ?: "Không thể tạo URL thanh toán"
                                )
                            }
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = exception.message ?: "Không thể tạo đơn hàng"
                    )
                }
            )
        }
    }

    fun setCouponCode(code: String) {
        _uiState.value = _uiState.value.copy(couponCode = code)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CheckoutUiState(
    val cart: CartDto? = null,
    val couponCode: String = "",
    val discountAmount: Double? = null,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val paymentUrl: String? = null,
    val orderCode: String? = null,
    val error: String? = null
)

