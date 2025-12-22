package com.example.khoitriso.ui.mypurchase

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.example.khoitriso.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.models.OrderItem
import com.example.khoitriso.ui.checkout.VNPayWebView
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.OrderStatus
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.toVND
import java.text.NumberFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel(),
) {
    val ordersState by viewModel.orders.collectAsState()
    val paymentUrl by viewModel.paymentUrl.collectAsState()
    val paymentError by viewModel.paymentError.collectAsState()

    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    
    // Xử lý Payment URL - Navigate to PaymentProcessingScreen
    LaunchedEffect(paymentUrl, selectedOrder) {
        if (paymentUrl != null && selectedOrder != null) {
            navController.navigate(
                NavRoute.NavPaymentProcessing(
                    paymentUrl = paymentUrl!!,
                    orderCode = selectedOrder!!.orderCode
                )
            )
            viewModel.clearPaymentUrl()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.order_history),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val state = ordersState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadOrders() }) {
                            Text(stringResource(R.string.try_again))
                        }
                    }
                }

                is UiState.Success -> {
                    val orders = state.data.items

                    if (orders.isEmpty()) {
                        EmptyOrderState()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(orders) { order ->
                                OrderCard(
                                    order = order,
                                    onClick = { selectedOrder = order } // 2. Set state khi click
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Hiển thị Dialog nếu selectedOrder != null
        if (selectedOrder != null) {
            OrderDetailDialog(
                order = selectedOrder!!,
                onDismiss = { selectedOrder = null },
                onRepay = { order ->
                    viewModel.continuePayment(order)
                }
            )
        }
        
        // 4. Hiển thị lỗi nếu có
        paymentError?.let { error ->
            LaunchedEffect(error) {
                // Có thể hiển thị Snackbar hoặc AlertDialog
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit, // Callback click
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Thêm clickable
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Code + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Đơn hàng #${order.orderCode}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatDate(order.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Hiển thị tối đa 2 món để gọn gàng
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                order.items.take(2).forEach { item ->
                    OrderItemRow(item)
                }
                if (order.items.size > 2) {
                    Text(
                        text = "+ ${order.items.size - 2} sản phẩm khác",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 52.dp) // Căn lề thẳng với text tên món
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Footer: Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tổng tiền:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = order.totalAmount.toVND(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// --- NEW COMPONENT: ORDER DETAIL DIALOG ---
@Composable
fun OrderDetailDialog(
    order: Order,
    onDismiss: () -> Unit,
    onRepay: (Order) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Full width dialog
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Chiếm 95% chiều ngang
                .fillMaxHeight(0.85f) // Chiếm 85% chiều cao
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // --- Dialog Header ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chi tiết đơn hàng",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "#${order.orderCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // --- Dialog Body (Scrollable) ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()), // Cho phép cuộn
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Trạng thái & Ngày
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OrderStatusChip(status = order.status, statusName = order.statusName)
                        Text(
                            text = formatDate(order.createdAt),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 2. Thông tin thanh toán
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            InfoRow(
                                icon = Icons.Default.CreditCard,
                                label = "Phương thức",
                                value = order.paymentMethod ?: "Không xác định"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow(
                                icon = Icons.Default.Info,
                                label = "Mã giao dịch",
                                value = order.transactionId ?: "---"
                            )
                        }
                    }

                    HorizontalDivider()

                    // 3. Danh sách sản phẩm (Full list)
                    Text(
                        text = "Danh sách sản phẩm (${order.items.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    order.items.forEach { item ->
                        OrderItemRow(item)
                    }

                    HorizontalDivider()

                    // 4. Tổng kết tiền
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PriceRow(
                            "Tạm tính",
                            order.totalAmount
                        ) // Giả sử Subtotal = Total nếu ko có discount
                        if (order.discountAmount > 0) {
                            PriceRow("Giảm giá", -order.discountAmount, isDiscount = true)
                        }

                        // Tổng cộng to
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Thành tiền",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = order.finalAmount.toVND(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // --- Dialog Footer ---
                Column(modifier = Modifier.padding(16.dp)) {
                    // Logic kiểm tra: Nếu đơn hàng chưa hoàn thành và chưa hủy
                    val isPending = order.statusName.lowercase().let {
                        it.contains("pending") || it.contains("waiting") || it.contains("chờ") || it.contains(
                            "process"
                        )
                    }

                    if (isPending) {
                        Button(
                            onClick = { onRepay(order) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(stringResource(R.string.continue_payment))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

// Helper Composable cho Dialog
@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PriceRow(label: String, amount: Double, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount.toVND(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isDiscount) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ... (Giữ nguyên các hàm OrderItemRow, OrderStatusChip, EmptyOrderState, formatCurrency, formatDate từ câu trả lời trước)
@Composable
fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.itemName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "x${item.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = item.price.toVND(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun OrderStatusChip(status: Int, statusName: String) {
    // Logic xác định màu sắc và text hiển thị
    val (bgColor, contentColor, displayText) = when (status) {
        // Ưu tiên kiểm tra bằng status code (Int) trước
        0, 1 -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFEF6C00),
            statusName.ifEmpty { "Chờ xử lý" }) // Pending/Processing
        2 -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            statusName.ifEmpty { "Hoàn thành" }) // Paid/Completed/Success
        3 -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            statusName.ifEmpty { "Đã hủy" }) // Cancelled/Failed
        4 -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFF1976D2),
            statusName.ifEmpty { "Đã hoàn tiền" }) // Refunded
        else -> {
            // Nếu status code không xác định, fallback về kiểm tra statusName (String)
            when (statusName.lowercase()) {
                "completed", "hoàn thành", "success" -> Triple(
                    Color(0xFFE8F5E9),
                    Color(0xFF2E7D32),
                    statusName
                )

                "processing", "đang xử lý", "pending", "chờ thanh toán" -> Triple(
                    Color(0xFFFFF3E0),
                    Color(0xFFEF6C00),
                    statusName
                )

                "cancelled", "đã hủy", "failed" -> Triple(
                    Color(0xFFFFEBEE),
                    Color(0xFFC62828),
                    statusName
                )

                else -> Triple(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    MaterialTheme.colorScheme.onSurface,
                    statusName.ifEmpty { "Không xác định" }
                )
            }
        }
    }

    // Nếu không có gì để hiển thị, thì không vẽ gì cả
    if (displayText.isBlank()) return

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp), // Bo tròn hơn cho đẹp
    ) {
        Text(
            text = displayText,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium, // Dùng labelMedium cho dễ đọc hơn
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}


@Composable
fun EmptyOrderState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chưa có đơn hàng nào",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun formatDate(dateString: String): String {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val zdt = ZonedDateTime.parse(dateString)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            zdt.format(formatter)
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}