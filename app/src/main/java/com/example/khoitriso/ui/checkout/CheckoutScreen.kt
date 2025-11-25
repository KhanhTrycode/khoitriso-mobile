package com.example.khoitriso.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.khoitriso.R
import com.example.khoitriso.data.dto.CartItemDto
import com.example.khoitriso.utils.NavRoute
import java.text.NumberFormat
import java.util.*
import kotlin.math.max

@Composable
fun CheckoutScreen(
    navController: NavHostController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showVNPayWebView by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.paymentUrl) {
        if (uiState.paymentUrl != null) {
            showVNPayWebView = true
        }
    }

    LaunchedEffect(uiState.orderCode) {
        if (uiState.orderCode != null && uiState.paymentUrl == null) {
            // Free order - navigate to success
            navController.navigate(
                NavRoute.NavPaymentResult(success = true, orderCode = uiState.orderCode)
            )
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.checkout_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.cart != null && uiState.cart!!.CartItems.isNotEmpty()) {
                val subtotal = uiState.cart!!.TotalAmount
                val discount = uiState.discountAmount ?: 0.0
                val total = max(0.0, subtotal - discount)
                CheckoutBottomBar(
                    totalAmount = total,
                    isProcessing = uiState.isProcessing,
                    onCheckout = {
                        viewModel.checkout { paymentUrl ->
                            // Payment URL ready, will be handled by LaunchedEffect
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                ErrorCard(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadCart() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.cart == null || uiState.cart!!.CartItems.isEmpty() -> {
                EmptyCartState(
                    onBackToCart = { navController.navigate(NavRoute.cart) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                    items(uiState.cart!!.CartItems) { item ->
                        CheckoutItemCard(item = item)
                    }

                    // Coupon section
                    item {
                        CouponSection(
                            couponCode = uiState.couponCode,
                            onCouponCodeChange = { viewModel.setCouponCode(it) }
                        )
                    }

                    // Order summary
                    item {
                        val subtotal = uiState.cart!!.TotalAmount
                        val discount = uiState.discountAmount ?: 0.0
                        val total = max(0.0, subtotal - discount)
                        OrderSummaryCard(
                            subtotal = subtotal,
                            discount = discount,
                            total = total
                        )
                    }
                }
            }
        }
    }

    // VNPay WebView Modal
    if (showVNPayWebView && uiState.paymentUrl != null) {
        VNPayWebView(
            url = uiState.paymentUrl!!,
            onPaymentSuccess = { orderCode ->
                showVNPayWebView = false
                navController.navigate(
                    NavRoute.NavPaymentResult(success = true, orderCode = orderCode)
                )
            },
            onPaymentError = { error ->
                showVNPayWebView = false
                navController.navigate(
                    NavRoute.NavPaymentResult(success = false, orderCode = null)
                )
            },
            onDismiss = {
                showVNPayWebView = false
            }
        )
    }
}

@Composable
private fun CheckoutItemCard(item: CartItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.CoverImage ?: "",
                contentDescription = item.Title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.Title ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPrice(item.Price),
                    style = MaterialTheme.typography.titleLarge,
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
                Text(formatPrice(subtotal))
            }
            if (discount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.discount))
                    Text("-${formatPrice(discount)}", color = MaterialTheme.colorScheme.error)
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
                    text = formatPrice(total),
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
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatPrice(totalAmount),
                    style = MaterialTheme.typography.titleLarge,
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
                        style = MaterialTheme.typography.titleMedium
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

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.try_again))
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(price)
}

