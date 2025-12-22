package com.example.khoitriso.ui.profilescreen

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.local.AppLanguage
import com.example.khoitriso.data.local.AppTheme
import com.example.khoitriso.data.local.LanguageManager
import com.example.khoitriso.data.local.ThemeManager
import com.example.khoitriso.domain.models.BookQuestion
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.models.WishlistItem
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.order.OrderUsecase
import com.example.khoitriso.domain.usecase.user.UserUsecase
import com.example.khoitriso.domain.usecase.wishlist.WishlistUsecase
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
    private val userUsecase: UserUsecase,
    private val wishlistUsecase: WishlistUsecase,
    private val orderUsecase: OrderUsecase,
    private val languageManager: LanguageManager,
    private val themeManager: ThemeManager,
    private val bookUsecase: BookUsecase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    // State cho việc kích hoạt sách
    private val _activationState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val activationState: StateFlow<UiState<String>> = _activationState

    // State cho danh sách hóa đơn
    private val _orders = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val orders: StateFlow<UiState<List<Order>>> = _orders

    // State cho wishlist
    private val _wishlist = MutableStateFlow<UiState<List<WishlistItem>>>(UiState.Loading)
    val wishlist: StateFlow<UiState<List<WishlistItem>>> = _wishlist

    // State cho tra cứu câu hỏi sách
    private val _questionLookup = MutableStateFlow<UiState<BookQuestion>>(UiState.Loading)
    val questionLookup: StateFlow<UiState<BookQuestion>> = _questionLookup

    init {
        loadUserInfo()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userUsecase.getCurrentUser().fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        fullName = user.fullName,
                        email = user.email,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    // User load failed - likely token expired
                    _navigationEvents.send(com.example.khoitriso.ui.behavior.NavigationEvent.NavigateToLogin)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
                    )
                }
            )
        }
    }

    fun updateProfile(fullName: String, email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            userUsecase.updateProfile(fullName, email).fold(
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
            userUsecase.uploadAvatar(file).fold(
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
            bookUsecase.activeCode(code)
        }
    }

    fun resetActivationState() {
        // Reset to a neutral state that allows input
        _activationState.value = UiState.Success("")
    }

    // Hàm lấy lịch sử đơn hàng
    fun loadOrderHistory() {
        viewModelScope.launch {
            _orders.value = UiState.Loading
            orderUsecase.getOrder().fold(
                onSuccess = { response ->
                    _orders.value = UiState.Success(response.items.sortedByDescending { it.createdAt })
                },
                onFailure = { exception ->
                    _orders.value = UiState.Error(exception.message ?: "Không thể tải lịch sử đơn hàng")
                }
            )
        }
    }

    // Language functions
    fun getCurrentLanguage(): AppLanguage {
        return languageManager.getCurrentLanguage()
    }

    fun setLanguage(language: AppLanguage) {
        languageManager.setLanguage(language)
    }

    // Theme functions
    fun getCurrentTheme(): AppTheme {
        return themeManager.getCurrentTheme()
    }

    fun setTheme(theme: AppTheme) {
        themeManager.setTheme(theme)
    }

    // Wishlist functions
    fun loadWishlist() {
        viewModelScope.launch {
            _wishlist.value = UiState.Loading
            wishlistUsecase.getWishlist().fold(
                onSuccess = { response ->
                    _wishlist.value = UiState.Success(response)
                },
                onFailure = { exception ->
                    _wishlist.value = UiState.Error(exception.message ?: "Không thể tải wishlist")
                }
            )
        }
    }

    fun removeFromWishlist(wishlistId: Int) {
        viewModelScope.launch {
            wishlistUsecase.removeFromWishlist(wishlistId).fold(
                onSuccess = {
                    // Reload wishlist
                    loadWishlist()
                },
                onFailure = { exception ->
                    // Show error
                    _wishlist.value = UiState.Error(exception.message ?: "Không thể xóa khỏi wishlist")
                }
            )
        }
    }

    // Question lookup functions
    fun lookupQuestion(questionId: String) {
        val id = questionId.toIntOrNull()
        if (id == null || id <= 0) {
            _questionLookup.value = UiState.Error("ID câu hỏi không hợp lệ")
            return
        }

        viewModelScope.launch {
            _questionLookup.value = UiState.Loading
            bookUsecase.getBookQuestionById(id).fold(
                onSuccess = { question ->
                    _questionLookup.value = UiState.Success(question)
                },
                onFailure = { exception ->
                    _questionLookup.value = UiState.Error(exception.message ?: "Không thể tải câu hỏi")
                }
            )
        }
    }

    fun resetQuestionLookup() {
        _questionLookup.value = UiState.Loading
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
