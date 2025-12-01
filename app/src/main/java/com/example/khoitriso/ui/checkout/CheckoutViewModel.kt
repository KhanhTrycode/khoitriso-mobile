package com.example.khoitriso.ui.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.request.CreateOrderRequest
import com.example.khoitriso.data.request.VNPayPaymentRequest
import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.models.OrderItem
import com.example.khoitriso.domain.usecase.order.CartUsecase
import com.example.khoitriso.domain.usecase.order.OrderUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.ItemBuyNow
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import kotlin.math.max

// Data class quản lý trạng thái riêng của màn hình Checkout
data class CheckoutState(
    val couponCode: String = "",
    val discountAmount: Double = 0.0,
    val isProcessing: Boolean = false,
    val paymentUrl: String? = null,
    val orderCode: String? = null,
    val error: String? = null,
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartUsecase: CartUsecase,
    private val orderUsecase: OrderUsecase,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    // 1. State quản lý dữ liệu Giỏ hàng
    private val _cart = MutableStateFlow<UiState<Carts>>(UiState.Loading)
    val cart: StateFlow<UiState<Carts>> = _cart.asStateFlow()

    // 2. State quản lý hành động Checkout
    private val _checkoutState = MutableStateFlow(CheckoutState())
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    fun loadCart() {
        loadData(
            stateFlow = _cart,
            mockData = MockData.mockCart,
            apiCall = {
                cartUsecase.getCart()
            }
        )
    }

    fun loadSingleItemForCheckout(item: ItemBuyNow) {
        viewModelScope.launch {
            _cart.value = UiState.Loading
            val result = CartItem(
                id = item.itemId,
                itemId = item.itemId,
                itemType = item.itemType,
                price = item.price,
                coverImage = item.coverImage,
                title = item.title
            )
            _cart.value = UiState.Success(
                Carts(
                    listOf(result),
                    totalItems = 1,
                    totalPrice = result.price
                )
            )
        }
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
            val cartItemIds = cartData.cartItems.map { it.id }
            val discount = _checkoutState.value.discountAmount
            val finalTotal = max(0.0, cartData.totalPrice - discount)

            // --- TEST MODE LOGIC START ---
            if (_isTestMode) {
                delay(1500) // Giả lập độ trễ mạng
                val mockOrderCode = "TEST-${System.currentTimeMillis().toString().takeLast(6)}"

                // 1. Tạo danh sách OrderItem từ CartItem
                val newOrderItems = cartData.cartItems.map { cartItem ->
                    OrderItem(
                        id = (1000..9999).random(), // ID giả
                        itemId = cartItem.itemId,
                        itemName = cartItem.title,
                        itemType = cartItem.itemType,
                        price = cartItem.price,
                        quantity = 1, // Giả sử số lượng là 1 nếu model CartItem không có field quantity
                        subTotal = cartItem.price.toInt()
                    )
                }

                // 2. Tạo đối tượng Order mới
                // Lưu ý: Các trường như id, userId điền giá trị giả định
                val newOrder = Order(
                    id = (MockData.mockOrders.maxOfOrNull { it.id } ?: 0) + 1, // Tự tăng ID
                    orderCode = mockOrderCode,
                    status = 2, // Giả sử 2 là Completed
                    statusName = "Completed", // YÊU CẦU: Trạng thái hoàn thành
                    totalAmount = cartData.totalPrice,
                    finalAmount = finalTotal,
                    discountAmount = discount,
                    items = newOrderItems,
                    createdAt = "",
                    // Các trường phụ điền giả
                    currency = "VND",
                    exchangeRate = 1,
                    paidAt = "",
                    paymentGateway = "TestGateway",
                    paymentMethod = "Credit Card",
                    taxAmount = 0.0,
                    transactionId = "TRANS-TEST-${System.currentTimeMillis()}",
                    userId = 1, // ID User đang test
                    coupon = null,
                    orderNotes = ""
                )

                // 3. Thêm vào MockData
                MockData.mockOrders.add(0, newOrder) // Thêm vào đầu danh sách

                // 4. Xử lý phản hồi UI
                if (finalTotal <= 0) {
                    // Case 1: Đơn hàng 0đ
                    _checkoutState.update {
                        it.copy(isProcessing = false, orderCode = mockOrderCode)
                    }

                } else {
                    // Case 2: Đơn hàng có phí (Mock trả URL giả)
                    val mockUrl = "https://google.com" // URL dummy

                    _checkoutState.update {
                        it.copy(
                            isProcessing = false,
                            paymentUrl = mockUrl,
                            orderCode = mockOrderCode
                        )
                    }
                    onPaymentUrlReady(mockUrl)
                }
                return@launch
            }
            // --- TEST MODE LOGIC END ---

            val orderRequest = CreateOrderRequest(
                CartItemIds = cartItemIds,
                CouponCode = _checkoutState.value.couponCode.ifEmpty { null },
                PaymentMethod = if (finalTotal <= 0) "FREE" else "VNPAY",
                PaymentGateway = if (finalTotal <= 0) "FREE" else "VNPAY"
            )

            // Step 2: Create Order (REAL API)
            orderUsecase.createOrder(orderRequest).fold(
                onSuccess = { order ->
                    val orderTotal =
                        if (order.totalAmount > 0) order.finalAmount else order.totalAmount

                    if (finalTotal <= 0 || orderTotal <= 0) {
                        processFreeOrder(cartItemIds)
                    } else {
                        createVNPayUrl(order.id, order.orderCode, onPaymentUrlReady)
                    }
                },
                onFailure = { exception ->
                    _checkoutState.update {
                        it.copy(
                            isProcessing = false,
                            error = exception.message ?: "Lỗi tạo đơn hàng"
                        )
                    }
                }
            )
        }
    }

    private suspend fun processFreeOrder(cartItemIds: List<Int>) {
        orderUsecase.paymentFreeOrder(cartItemIds).fold(
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

    private suspend fun createVNPayUrl(
        orderId: Int,
        orderCode: String,
        onPaymentUrlReady: (String) -> Unit,
    ) {
        val vnpayRequest = VNPayPaymentRequest(
            OrderId = orderId,
            OrderDescription = "Thanh toan don hang $orderCode"
        )
        orderUsecase.vnpayCreatePaymentUrl(vnpayRequest).fold(
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