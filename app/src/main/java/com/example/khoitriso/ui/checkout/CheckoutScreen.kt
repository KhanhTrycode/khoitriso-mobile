package com.example.khoitriso.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.khoitriso.R
import com.example.khoitriso.data.dto.CartItemDto
import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.ui.common.ErrorCard
import com.example.khoitriso.utils.ItemBuyNow
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.toVND
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavHostController,
    itemBuyNow: ItemBuyNow? = null,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    // Collect 2 states riêng biệt
    val cartState by viewModel.cart.collectAsState()
    val checkoutState by viewModel.checkoutState.collectAsState()

    if (itemBuyNow != null){
        viewModel.loadSingleItemForCheckout(itemBuyNow)
    }
    else{
        viewModel.loadCart()
    }

    // Xử lý Payment URL - Navigate to PaymentProcessingScreen
    LaunchedEffect(checkoutState.paymentUrl, checkoutState.orderCode) {
        if (checkoutState.paymentUrl != null && checkoutState.orderCode != null) {
            navController.navigate(
                NavRoute.NavPaymentProcessing(
                    paymentUrl = checkoutState.paymentUrl!!,
                    orderCode = checkoutState.orderCode!!
                )
            ) {
                popUpTo(NavRoute.CHECKOUT) { inclusive = true }
            }
        }
    }

    // Xử lý thành công (Free order)
    LaunchedEffect(checkoutState.orderCode) {
        if (checkoutState.orderCode != null && checkoutState.paymentUrl == null) {
            navController.navigate(
                NavRoute.NavPaymentResult(success = true, orderCode = checkoutState.orderCode)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checkout_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            when (val state = cartState) {
                is UiState.Success -> {
                    if (state.data.cartItems.isNotEmpty()) {
                        val subtotal = state.data.totalPrice
                        val discount = checkoutState.discountAmount
                        val total = max(0.0, subtotal - discount)

                        CheckoutBottomBar(
                            totalAmount = total,
                            isProcessing = checkoutState.isProcessing,
                            onCheckout = {
                                viewModel.checkout()
                            }
                        )
                    }
                }
                else -> {}
            }
        }
    ) { paddingValues ->

        when (val state = cartState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                ErrorCard(
                    message = state.message,
                    onRetry = { viewModel.loadCart() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is UiState.Success -> {
                val cart = state.data
                if (cart.cartItems.isEmpty()) {
                    EmptyCartState(
                        onBackToCart = { navController.navigate(NavRoute.CART) },
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.order_summary),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Cart items
                        items(cart.cartItems) { item ->
                            CheckoutItemCard(item = item)
                        }

                        // Coupon section
                        item {
                            CouponSection(
                                couponCode = checkoutState.couponCode,
                                onCouponCodeChange = { viewModel.setCouponCode(it) }
                            )
                        }

                        // Order summary
                        item {
                            val subtotal = cart.totalPrice
                            val discount = checkoutState.discountAmount
                            val total = max(0.0, subtotal - discount)
                            OrderSummaryCard(
                                subtotal = subtotal,
                                discount = discount,
                                total = total
                            )
                        }

                        // Hiển thị lỗi nếu có
                        if (checkoutState.error != null) {
                            item {
                                Text(
                                    text = checkoutState.error!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutItemCard(item: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.coverImage,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.price.toVND(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CouponSection(
    couponCode: String,
    onCouponCodeChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.coupon_code),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = couponCode,
                onValueChange = onCouponCodeChange,
                label = { Text(stringResource(R.string.coupon_code)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            // TODO: Add apply coupon button
        }
    }
}

@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    discount: Double,
    total: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.subtotal))
                Text(subtotal.toVND())
            }
            if (discount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.discount))
                    Text("-${discount.toVND()}", color = MaterialTheme.colorScheme.error)
                }
            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = total.toVND(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    totalAmount: Double,
    isProcessing: Boolean,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.total),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = totalAmount.toVND(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.payment_vnpay),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCartState(
    onBackToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.cart_empty),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackToCart) {
            Text(stringResource(R.string.back_to_cart))
        }
    }
}
