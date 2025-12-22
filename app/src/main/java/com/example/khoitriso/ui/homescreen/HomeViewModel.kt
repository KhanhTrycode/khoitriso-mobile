package com.example.khoitriso.ui.homescreen

import android.util.Log
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.usecase.book.BookUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.usecase.category.CategoryUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.domain.usecase.order.CartUsecase
import com.example.khoitriso.domain.usecase.wishlist.WishlistUsecase
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.ItemType
import com.example.khoitriso.utils.UiEvent
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookUsecase: BookUsecase,
    private val courseUsecase: CourseUsecase,
    private val categoryUsecase: CategoryUsecase,
    private val cartUsecase: CartUsecase,
    private val wishlistUsecase: WishlistUsecase,
    private val userManager: UserManager,
) : BaseViewModel() {
    
    // Track wishlist items: "itemType-itemId" -> wishlistId
    private val _wishlistItems = MutableStateFlow<Map<String, Int>>(emptyMap())
    
    // Computed property để check item có trong wishlist không
    private val _wishlistStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val wishlistStatus: StateFlow<Map<String, Boolean>> = _wishlistStatus

    private val _categories = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    private val _courses = MutableStateFlow<UiState<MyResponese<Course>>>(UiState.Loading)
    private val _books = MutableStateFlow<UiState<MyResponese<Book>>>(UiState.Loading)

    val categories: StateFlow<UiState<List<Category>>> = _categories

    private val _recommendedBooks = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val recommendedBooks: StateFlow<UiState<List<Book>>> = _recommendedBooks

    private val _trendingCourses = MutableStateFlow<UiState<List<Course>>>(UiState.Loading)
    val trendingCourses: StateFlow<UiState<List<Course>>> = _trendingCourses

    private val _tryCourses = MutableStateFlow<UiState<Course>>(UiState.Loading)
    val tryCourses: StateFlow<UiState<Course>> = _tryCourses

    private val _myCourses = MutableStateFlow<UiState<List<MyCourse>>>(UiState.Loading)
    val myCourses: StateFlow<UiState<List<MyCourse>>> = _myCourses

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    init {
        getUser()
        getCourse()
        getBooks()
        getCategory()
        getMyCourse()
        loadWishlist()
    }

    private fun getUser() {
        loadUser(
            user = _user,
            userManager = userManager,
            onUserLoadFailed = {
                // User not found in local storage - token might be expired
                // Send navigation event to login
                _navigationEvents.send(com.example.khoitriso.ui.behavior.NavigationEvent.NavigateToLogin)
            }
        )
    }

    private fun getCategory() {
        loadData(_categories, apiCall = {
            categoryUsecase.getCategory()
        })
    }

    fun getBooks() {
        loadDataWithPage(
            _books,
            apiCall = {
                debug("Calling getBook API...", "HomeViewModel")
                // Gọi với null để không dùng default PagingRequest
                val result = try {
                    bookUsecase.getBook(com.example.khoitriso.data.dto.request.PagingRequest())
                } catch (e: Exception) {
                    debug("Exception calling getBook: ${e.message}", "HomeViewModel")
                    Result.failure(e)
                }
                debug("getBook result: ${result.isSuccess}", "HomeViewModel")
                result.onFailure { 
                    debug("getBook error: ${it.message}", "HomeViewModel")
                    it.printStackTrace()
                }
                result.onSuccess {
                    debug("getBook success: ${it.items.size} items", "HomeViewModel")
                }
                result
            },
            onSuccess = { bookState ->
                debug("getBook success in onSuccess: ${bookState.data.items.size} books", "HomeViewModel")
                val books = bookState.data.items
                val recommendedBooks = books.shuffled().take(5)
                _recommendedBooks.value = UiState.Success(recommendedBooks)
            },
            onFailure = {
                debug("getBook failed in loadDataWithPage", "HomeViewModel")
            }
        )
    }

    fun getMyCourse() {
        loadData(
            stateFlow = _myCourses,
            apiCall = {
                courseUsecase.getMyCourse()
            }
        )
    }

    fun getCourse() {
        loadDataWithPage(
            _courses,
            apiCall = {
                courseUsecase.getCourse()
            },
            onSuccess = { courseState ->
                val courses = courseState.data.items
                val trendingCourses = courses.shuffled().take(5)
                _trendingCourses.value = UiState.Success(trendingCourses)
                _tryCourses.value = UiState.Success(trendingCourses.first())
            }
        )

    }

    fun addToCart(itemId: Int, itemType: Int) {
        viewModelScope.launch {
            cartUsecase.addToCart(itemId = itemId, itemType = itemType).fold(
                onSuccess = {
                    _events.send(UiEvent.ShowSnackbar("Đã thêm vào giỏ hàng thành công!"))
                },
                onFailure = { error ->
                    _events.send(UiEvent.ShowSnackbar("Lỗi: ${error.message}"))
                }
            )
        }
    }
    
    // Load danh sách wishlist và build map
    fun loadWishlist() {
        viewModelScope.launch {
            debug("Loading wishlist...", "HomeViewModel")
            wishlistUsecase.getWishlist().fold(
                onSuccess = { items ->
                    debug("Wishlist loaded: ${items.size} items", "HomeViewModel")
                    // Tạo map: "itemType-itemId" -> wishlistId
                    val itemsMap = items.associate { 
                        val key = "${it.itemType}-${it.itemId}"
                        debug("Wishlist item: key=$key, wishlistId=${it.id}", "HomeViewModel")
                        key to it.id 
                    }
                    _wishlistItems.value = itemsMap
                    
                    // Update status map
                    _wishlistStatus.value = itemsMap.mapValues { true }
                    debug("Wishlist status map: ${_wishlistStatus.value}", "HomeViewModel")
                },
                onFailure = { error ->
                    debug("Failed to load wishlist: ${error.message}", "HomeViewModel")
                }
            )
        }
    }
    
    fun toggleWishlist(itemId: Int, itemType: Int) {
        viewModelScope.launch {
            val key = "$itemType-$itemId"
            val isCurrentlyInWishlist = _wishlistStatus.value[key] ?: false
            
            debug("Toggle wishlist: key=$key, isInWishlist=$isCurrentlyInWishlist", "HomeViewModel")
            
            if (isCurrentlyInWishlist) {
                // Item đã có trong wishlist -> Remove bằng itemId và itemType
                wishlistUsecase.removeItemFromWishlist(itemId, itemType).fold(
                    onSuccess = {
                        // Update state ngay khi API success
                        _wishlistItems.value = _wishlistItems.value.toMutableMap().apply { remove(key) }
                        _wishlistStatus.value = _wishlistStatus.value.toMutableMap().apply { remove(key) }
                        debug("Removed from wishlist: $key", "HomeViewModel")
                        _events.send(UiEvent.ShowSnackbar("Đã xóa khỏi yêu thích!"))
                    },
                    onFailure = { error ->
                        debug("Failed to remove from wishlist: ${error.message}", "HomeViewModel")
                        _events.send(UiEvent.ShowSnackbar("Lỗi: ${error.message}"))
                    }
                )
            } else {
                // Item chưa có trong wishlist -> Add
                wishlistUsecase.addToWishlist(itemId, itemType).fold(
                    onSuccess = {
                        // API success -> update state ngay, không cần wishlistId
                        _wishlistItems.value = _wishlistItems.value.toMutableMap().apply { put(key, itemId) }
                        _wishlistStatus.value = _wishlistStatus.value.toMutableMap().apply { put(key, true) }
                        debug("Added to wishlist: key=$key", "HomeViewModel")
                        _events.send(UiEvent.ShowSnackbar("Đã thêm vào yêu thích!"))
                    },
                    onFailure = { error ->
                        debug("Failed to add to wishlist: ${error.message}", "HomeViewModel")
                        _events.send(UiEvent.ShowSnackbar("Lỗi: ${error.message}"))
                    }
                )
            }
        }
    }
}
