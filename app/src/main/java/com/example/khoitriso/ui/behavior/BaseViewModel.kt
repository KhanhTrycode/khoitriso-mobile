package com.example.khoitriso.ui.behavior

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.usecase.category.GetCategory
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    // Nên đặt là isTestMode cho rõ ràng hơn
    protected val _isTestMode = false // Dùng protected để lớp con có thể truy cập

    protected fun <T> loadData(
        stateFlow: MutableStateFlow<UiState<T>>,
        mockData: T,
        isTestMode: Boolean = _isTestMode,
        apiCall: suspend () -> Result<T>,
    ) {
        viewModelScope.launch {
            stateFlow.value = UiState.Loading
            if (isTestMode) {
                stateFlow.value = UiState.Success(mockData)
            } else {
                try {
                    val result = apiCall()
                    result.fold(
                        onSuccess = { item ->
                            stateFlow.value = UiState.Success(item)
                        },
                        onFailure = { exception ->
                            // Nếu thất bại, cập nhật state với thông báo lỗi
                            stateFlow.value = UiState.Error(
                                exception.message ?: ("An unknown error " +
                                        "occurred")
                            )
                        }
                    )
                } catch (e: Exception) {
                    // Xử lý lỗi nếu cần
                    e.printStackTrace()
                    UiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun onBuy() {

    }

    fun onCart(){
    }
}
