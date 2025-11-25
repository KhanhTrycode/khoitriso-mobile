package com.example.khoitriso.ui.checkout

import android.net.Uri
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

                                // Detect VNPay callback
                                // Backend callback URL format: /api/vnpay/callback?...
                                // Or payment-result page with processed params
                                if (currentUrl.contains("/api/vnpay/callback") ||
                                    currentUrl.contains("payment-result") ||
                                    currentUrl.contains("vnp_ResponseCode")
                                ) {
                                    val uri = Uri.parse(currentUrl)
                                    
                                    // Check if this is direct VNPay callback (has vnp_ params)
                                    val hasVnpParams = uri.queryParameterNames.any { it.startsWith("vnp_") }
                                    
                                    if (hasVnpParams && currentUrl.contains("/api/vnpay/callback")) {
                                        // Direct VNPay callback - parse vnp_ params directly
                                        val responseCode = uri.getQueryParameter("vnp_ResponseCode")
                                        val orderCode = uri.getQueryParameter("vnp_TxnRef")
                                            ?: uri.getQueryParameter("vnp_OrderInfo")
                                        val message = uri.getQueryParameter("vnp_ResponseMessage")
                                        
                                        if (responseCode == "00") {
                                            onPaymentSuccess(orderCode ?: "")
                                        } else {
                                            onPaymentError(message ?: "Thanh toán thất bại")
                                        }
                                        return true
                                    }
                                    
                                    // Processed result from backend (payment-result page)
                                    val responseCode = uri.getQueryParameter("vnp_ResponseCode")
                                        ?: uri.getQueryParameter("responseCode")
                                    val orderCode = uri.getQueryParameter("vnp_TxnRef")
                                        ?: uri.getQueryParameter("orderCode")
                                        ?: uri.getQueryParameter("vnp_OrderInfo")
                                    val transactionStatus = uri.getQueryParameter("transactionStatus")
                                    val message = uri.getQueryParameter("message")
                                        ?: uri.getQueryParameter("vnp_ResponseMessage")
                                    val success = uri.getQueryParameter("success") == "true"

                                    if (success && responseCode == "00" && transactionStatus == "00") {
                                        onPaymentSuccess(orderCode ?: "")
                                    } else if (success == false || responseCode != "00") {
                                        onPaymentError(message ?: "Thanh toán thất bại")
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

