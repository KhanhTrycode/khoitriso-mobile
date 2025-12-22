package com.example.khoitriso.ui.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.usecase.order.OrderUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentProcessingViewModel @Inject constructor(
    private val orderUsecase: OrderUsecase
) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Checking())
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private var pollingJob: Job? = null
    private val maxAttempts = 60 // 60 lần x 5 giây = 5 phút
    private val pollingInterval = 5000L // 5 giây

    fun startPolling(orderCode: String) {
        stopPolling()
        _paymentState.value = PaymentState.Checking(0)
        
        pollingJob = viewModelScope.launch {
            var attemptCount = 0
            
            while (attemptCount < maxAttempts) {
                attemptCount++
                _paymentState.value = PaymentState.Checking(attemptCount)
                
                Log.d("PaymentProcessing", "Polling attempt $attemptCount for order $orderCode")
                
                val result = orderUsecase.queryTransaction(orderCode)
                
                result.fold(
                    onSuccess = { queryResponse ->
                        Log.d("PaymentProcessing", "Query success: ${queryResponse.TransactionStatus}")
                        
                        // TransactionStatus: 00 = Success, 01 = Failed, 02 = Pending
                        when (queryResponse.TransactionStatus) {
                            "00" -> {
                                Log.d("PaymentProcessing", "Payment SUCCESS")
                                _paymentState.value = PaymentState.Success
                                return@launch
                            }
                            "01" -> {
                                Log.d("PaymentProcessing", "Payment FAILED")
                                _paymentState.value = PaymentState.Failed(
                                    queryResponse.Message ?: "Giao dịch thất bại"
                                )
                                return@launch
                            }
                            "02" -> {
                                // Pending - tiếp tục polling
                                Log.d("PaymentProcessing", "Payment PENDING - continue polling")
                            }
                            else -> {
                                // Unknown status - tiếp tục polling
                                Log.d("PaymentProcessing", "Unknown status: ${queryResponse.TransactionStatus}")
                            }
                        }
                    },
                    onFailure = { exception ->
                        Log.e("PaymentProcessing", "Query failed: ${exception.message}")
                        // Tiếp tục polling khi có lỗi API (có thể do chưa có transaction)
                    }
                )
                
                delay(pollingInterval)
            }
            
            // Timeout sau maxAttempts
            Log.d("PaymentProcessing", "Polling TIMEOUT after $attemptCount attempts")
            _paymentState.value = PaymentState.Timeout
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
