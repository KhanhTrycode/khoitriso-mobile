package com.example.khoitriso.ui.payment

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.utils.NavRoute
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentProcessingScreen(
    navController: NavController,
    paymentUrl: String,
    orderCode: String,
    viewModel: PaymentProcessingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val paymentState by viewModel.paymentState.collectAsState()

    // Mở browser ngay khi vào màn hình
    LaunchedEffect(paymentUrl) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
        context.startActivity(intent)
        
        // Đợi 3 giây rồi bắt đầu polling
        delay(3000)
        viewModel.startPolling(orderCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán") },
                navigationIcon = {
                    IconButton(onClick = { 
                        viewModel.stopPolling()
                        navController.popBackStack() 
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = paymentState) {
                is PaymentState.Checking -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Đang kiểm tra thanh toán...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vui lòng hoàn tất thanh toán trên trình duyệt",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Đã kiểm tra ${state.attemptCount} lần",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                is PaymentState.Success -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Thanh toán thành công!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mã đơn hàng: $orderCode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            navController.navigate(NavRoute.NavMyPurchases("courses")) {
                                popUpTo(NavRoute.HOME) { inclusive = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Xem khóa học của tôi")
                    }
                }
                
                is PaymentState.Failed -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Thanh toán thất bại",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            viewModel.startPolling(orderCode)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kiểm tra lại")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Quay lại")
                    }
                }
                
                is PaymentState.Timeout -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Hết thời gian chờ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Không thể xác nhận thanh toán. Vui lòng kiểm tra lại trong lịch sử đơn hàng.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            navController.navigate(NavRoute.NavMyPurchases("orders")) {
                                popUpTo(NavRoute.HOME) { inclusive = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Xem đơn hàng")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Quay lại")
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPolling()
        }
    }
}

sealed class PaymentState {
    data class Checking(val attemptCount: Int = 0) : PaymentState()
    object Success : PaymentState()
    data class Failed(val message: String) : PaymentState()
    object Timeout : PaymentState()
}
