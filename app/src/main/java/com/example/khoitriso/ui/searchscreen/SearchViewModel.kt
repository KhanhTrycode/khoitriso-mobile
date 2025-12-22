package com.example.khoitriso.ui.searchscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.SearchType
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val bookUsecase: BookUsecase, // Uncomment khi dùng thật
    private val courseUsecase: CourseUsecase,
) : BaseViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow(SearchType.ALL)
    val activeFilter = _activeFilter.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private val _rawResults = MutableStateFlow<UiState<List<Any>>>(UiState.Success(emptyList()))

    val searchResults: StateFlow<UiState<List<Any>>> =
        combine(_rawResults, _activeFilter) { rawState, filter ->
            if (rawState is UiState.Success) {
                val filtered = when (filter) {
                    SearchType.ALL -> rawState.data
                    SearchType.COURSE -> rawState.data.filterIsInstance<Course>()
                    SearchType.BOOK -> rawState.data.filterIsInstance<Book>()
                    else -> rawState.data
                }
                UiState.Success(filtered)
            } else {
                rawState
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Success(emptyList())
        )

    fun initTab(tab: Int) {
        setFilter(tab)
        performSearch(isSkip = true)
    }

    fun onSearchFocusChanged(isFocused: Boolean) {
        _isSearching.value = isFocused
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.isBlank()) {
            _rawResults.value = UiState.Success(emptyList())
        }
    }

    fun setFilter(filter: Int) {
        _activeFilter.value = filter
    }

    fun performSearch(isSkip: Boolean = false) {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) {
            if (!isSkip) {
                _rawResults.value = UiState.Success(emptyList())
                return
            }
        }

        viewModelScope.launch {
            _rawResults.value = UiState.Loading

            val booksResult = bookUsecase.getBook(PagingRequest(search = query))
            val coursesResult = courseUsecase.getCourse(PagingRequest(search = query))

            val foundBooks: List<Book> = booksResult.fold(
                onSuccess = { response -> response.items },
                onFailure = { emptyList() }
            )

            val foundCourses: List<Course> = coursesResult.fold(
                onSuccess = { response -> response.items },
                onFailure = { emptyList() }
            )

            if (booksResult.isFailure && coursesResult.isFailure) {
                val error = booksResult.exceptionOrNull() ?: coursesResult.exceptionOrNull()
                _rawResults.value = UiState.Error(error?.message ?: "Đã xảy ra lỗi không xác định")
                return@launch
            }

            val combinedResults = foundBooks + foundCourses

            _rawResults.value = if (combinedResults.isEmpty()) {
                UiState.Error("Không tìm thấy kết quả phù hợp.")
            } else {
                UiState.Success(combinedResults)
            }
        }
    }

}