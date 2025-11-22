package com.example.khoitriso.ui.searchscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// Enum để định nghĩa các loại bộ lọc
enum class SearchFilter {
    ALL, COURSES, BOOKS
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val bookUsecase: BookUsecase,
    private val courseUsecase: CourseUsecase,
) : BaseViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // State cho bộ lọc hiện tại, mặc định là ALL
    private val _activeFilter = MutableStateFlow(SearchFilter.ALL)
    val activeFilter: StateFlow<SearchFilter> = _activeFilter.asStateFlow()

    // State chứa kết quả gốc từ usecase
    private val _rawResults = MutableStateFlow<UiState<List<Any>>>(UiState.Loading)

    // State cuối cùng để hiển thị trên UI, được tính toán từ bộ lọc và kết quả gốc
    private val _searchResults = MutableStateFlow<UiState<List<Any>>>(UiState.Loading)
    val searchResults: StateFlow<UiState<List<Any>>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun onSearchFocusChanged(isFocused: Boolean) {
        _isSearching.value = isFocused
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        if(newQuery.isNotBlank()) {
        } else {
            _rawResults.value = UiState.Success(emptyList())
        }
    }

    fun performSearch() {
        if (_searchQuery.value.isBlank()) {
            _rawResults.value = UiState.Success(emptyList())
            return
        }
        loadData(
            stateFlow = _rawResults,
            mockData = MockData.books + MockData.mockCourses,
            apiCall = {
                // Gọi cả hai use case và kết hợp kết quả
                val books = bookUsecase.getBook()
                val courses = courseUsecase.getCourse()
                val allResults = books.getOrElse { emptyList() } + courses.getOrElse { emptyList() }
                Result.success(allResults)
            }
        )
        putSearchResult()
    }

    fun putSearchResult(){
        viewModelScope.launch {
            combine(_rawResults, _activeFilter) { rawState, filter ->
                when (rawState) {
                    is UiState.Success -> {
                        val filteredData = when (filter) {
                            SearchFilter.ALL -> rawState.data
                            SearchFilter.COURSES -> rawState.data.filterIsInstance<Course>()
                            SearchFilter.BOOKS -> rawState.data.filterIsInstance<Book>()
                        }
                        UiState.Success(filteredData)
                    }
                    else -> rawState
                }
            }.collect { filteredState ->
                _searchResults.value = filteredState
            }
        }
    }
    fun setFilter(filter: SearchFilter) {
        _activeFilter.value = filter
    }
}
