package com.example.khoitriso.ui.profilescreen

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.repository.UserRepository
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    // State cho việc kích hoạt sách
    private val _activationState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val activationState: StateFlow<UiState<String>> = _activationState

    // State cho danh sách hóa đơn
    private val _orders = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val orders: StateFlow<UiState<List<Order>>> = _orders

    init {
        loadUserInfo()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.getCurrentUser().fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        fullName = user.fullName,
                        email = user.email,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Không thể tải thông tin. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun updateProfile(fullName: String, email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            userRepository.updateProfile(fullName, email).fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        fullName = user.fullName,
                        email = user.email,
                        isSaving = false,
                        editMode = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Không thể cập nhật thông tin. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun uploadAvatar(file: MultipartBody.Part) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true, error = null)
            userRepository.uploadAvatar(file).fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        isUploading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        error = "Không thể tải ảnh đại diện. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun setEditMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(editMode = enabled)
    }

    fun updateFullName(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Hàm kích hoạt sách
    fun activateBook(code: String) {
        if (code.isBlank()) {
            _activationState.value = UiState.Error("Vui lòng nhập mã kích hoạt")
            return
        }

        viewModelScope.launch {
            _activationState.value = UiState.Loading
            delay(1500) // Giả lập gọi API

            // Mock logic: Nếu code bắt đầu bằng "BOOK", thành công
            if (code.startsWith("BOOK", ignoreCase = true)) {
                _activationState.value = UiState.Success("Kích hoạt sách thành công! Sách đã được thêm vào thư viện.")
                // Ở đây thực tế bạn sẽ gọi EventBus hoặc reload lại data của MyLearningViewModel
            } else {
                _activationState.value = UiState.Error("Mã kích hoạt không hợp lệ hoặc đã sử dụng.")
            }
        }
    }

    fun resetActivationState() {
        _activationState.value = UiState.Loading
    }

    // Hàm lấy lịch sử đơn hàng
    fun loadOrderHistory() {
        viewModelScope.launch {
            _orders.value = UiState.Loading
            delay(1000)
            // Lấy từ MockData
            _orders.value = UiState.Success(MockData.mockOrders.sortedByDescending { it.createdAt })
        }
    }
}

data class ProfileUiState(
    val user: User? = null,
    val fullName: String = "",
    val email: String = "",
    val editMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploading: Boolean = false,
    val error: String? = null
)
