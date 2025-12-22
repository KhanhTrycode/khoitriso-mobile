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
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.ItemBuyNow
import com.example.khoitriso.utils.OrderStatus
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
            apiCall = {
                cartUsecase.getCart()
            }
        )
    }

    fun loadSingleCart(item: ItemBuyNow, cartItemId: Int) {
        _cart.value = UiState.Loading
        val cart = Carts(
            cartItems = listOf(
                CartItem(
                    id = cartItemId,
                    itemId = item.itemId,
                    itemType = item.itemType,
                    price = item.price,
                    coverImage = item.coverImage,
                    title = item.title
                )),
            totalItems = 1,
            totalPrice = item.price
        )

        _cart.value = UiState.Success(cart)
    }

    fun loadSingleItemForCheckout(item: ItemBuyNow) {
        viewModelScope.launch {
            _cart.value = UiState.Loading
            
            // Với BuyNow, cần thêm item vào cart trước để lấy cartItemId thực sự
            val addResult = cartUsecase.addToCart(item.itemId, item.itemType)
            addResult.fold(
                onSuccess = {
                    loadSingleCart(item,it)
                },
                onFailure = { exception ->
                    _cart.value = UiState.Error(exception.message ?: "Không thể thêm vào giỏ hàng")
                }
            )
        }
    }

    fun setCouponCode(code: String) {
        _checkoutState.update { it.copy(couponCode = code) }
    }

    fun checkout() {
        val currentCartState = _cart.value
        if (currentCartState !is UiState.Success) return

        val cartData = currentCartState.data

        viewModelScope.launch {
            _checkoutState.update { it.copy(isProcessing = true, error = null) }

            // Step 1: Prepare Data
            val cartItemIds = cartData.cartItems.map { it.id }
            val discount = _checkoutState.value.discountAmount
            val finalTotal = max(0.0, cartData.totalPrice - discount)

            val orderRequest = CreateOrderRequest(
                CartItemIds = cartItemIds,
                CouponCode = _checkoutState.value.couponCode.ifEmpty { null },
                PaymentMethod = if (finalTotal <= 0) "FREE" else "VNPAY",
                PaymentGateway = if (finalTotal <= 0) "FREE" else "VNPAY"
            )

            // Step 2: Create Order (REAL API)
            orderUsecase.createOrder(orderRequest).fold(
                onSuccess = { order ->
                    // Lưu orderCode ngay sau khi tạo order thành công
                    _checkoutState.update { it.copy(orderCode = order.orderCode) }
                    
                    val orderTotal =
                        if (order.totalAmount > 0) order.finalAmount else order.totalAmount

                    if (finalTotal <= 0 || orderTotal <= 0) {
                        // Đơn hàng miễn phí - đã được tạo với status FREE, chỉ cần navigate
                        _checkoutState.update {
                            it.copy(isProcessing = false)
                        }
                    } else {
                        // Đơn hàng có phí - tạo VNPay URL
                        createVNPayUrl(order.id, order.orderCode)
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


    private suspend fun createVNPayUrl(
        orderId: Int,
        orderCode: String,
    ) {
        val vnpayRequest = VNPayPaymentRequest(
            OrderId = orderId,
            OrderDescription = "Thanh toan don hang $orderCode"
        )
        orderUsecase.vnpayCreatePaymentUrl(vnpayRequest).fold(
            onSuccess = { response ->
                // Chỉ set paymentUrl, không set lại orderCode (đã set trước đó)
                // Tránh race condition với LaunchedEffect(orderCode)
                _checkoutState.update {
                    it.copy(
                        isProcessing = false,
                        paymentUrl = response.PaymentUrl
                    )
                }
            },
            onFailure = { exception ->
                _checkoutState.update {
                    it.copy(
                        isProcessing = false, 
                        error = exception.message ?: "Không thể tạo URL thanh toán VNPay"
                    )
                }
            }
        )
    }

    fun clearError() {
        _checkoutState.update { it.copy(error = null) }
    }
}