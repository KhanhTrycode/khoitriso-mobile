package com.example.khoitriso.ui.checkout

import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VNPayWebView(
    url: String,
    onPaymentSuccess: (orderCode: String) -> Unit,
    onPaymentError: (error: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var webView: WebView? by remember { mutableStateOf(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .height(600.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thanh toán VNPay",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng"
                    )
                }
            }

            // WebView
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                        }

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val currentUrl = request?.url?.toString() ?: return false
                                Log.d("VNPayWebView", "URL Loading: $currentUrl")

                                // Detect VNPay callback or payment result
                                // 1. Backend callback redirect: /api/vnpay/callback?vnp_...
                                // 2. Frontend payment result: /payment-result?success=...
                                if (currentUrl.contains("/api/vnpay/callback") ||
                                    currentUrl.contains("payment-result") ||
                                    currentUrl.contains("vnp_ResponseCode")
                                ) {
                                    Log.d("VNPayWebView", "Detected VNPay callback/result URL")
                                    val uri = Uri.parse(currentUrl)
                                    
                                    // Parse parameters from both VNPay direct callback and backend processed result
                                    val responseCode = uri.getQueryParameter("vnp_ResponseCode")
                                        ?: uri.getQueryParameter("responseCode")
                                    val transactionStatus = uri.getQueryParameter("vnp_TransactionStatus")
                                        ?: uri.getQueryParameter("transactionStatus")
                                    val orderCode = uri.getQueryParameter("vnp_TxnRef")
                                        ?: uri.getQueryParameter("orderCode")
                                    val message = uri.getQueryParameter("message")
                                        ?: uri.getQueryParameter("vnp_ResponseMessage")
                                    val success = uri.getQueryParameter("success")
                                    
                                    Log.d("VNPayWebView", "Params - responseCode: $responseCode, transactionStatus: $transactionStatus, orderCode: $orderCode, success: $success")
                                    
                                    // Determine if payment is successful
                                    // Success criteria:
                                    // - success=true from backend OR
                                    // - responseCode=00 AND transactionStatus=00 from VNPay
                                    val isSuccess = (success == "true") ||
                                        (responseCode == "00" && transactionStatus == "00")
                                    
                                    Log.d("VNPayWebView", "Payment Result - isSuccess: $isSuccess")
                                    
                                    if (isSuccess && !orderCode.isNullOrEmpty()) {
                                        Log.d("VNPayWebView", "Payment SUCCESS - OrderCode: $orderCode")
                                        onPaymentSuccess(orderCode)
                                    } else {
                                        // Extract error message based on response code
                                        val errorMessage = when {
                                            !message.isNullOrEmpty() -> message
                                            responseCode == "24" -> "Khách hàng hủy giao dịch"
                                            responseCode == "51" -> "Tài khoản không đủ số dư"
                                            responseCode == "11" -> "Đã hết hạn chờ thanh toán"
                                            responseCode == "12" -> "Thẻ/Tài khoản bị khóa"
                                            responseCode != null -> "Thanh toán thất bại (Mã lỗi: $responseCode)"
                                            else -> "Thanh toán thất bại"
                                        }
                                        Log.d("VNPayWebView", "Payment ERROR: $errorMessage")
                                        onPaymentError(errorMessage)
                                    }
                                    return true
                                }

                                return false
                            }
                        }

                        loadUrl(url)
                        webView = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
        }
    }
}

