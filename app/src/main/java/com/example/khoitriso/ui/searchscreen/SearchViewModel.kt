package com.example.khoitriso.ui.searchscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
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

enum class SearchFilter {
    ALL, COURSES, BOOKS
}

@HiltViewModel
class SearchViewModel @Inject constructor(
     private val bookUsecase: BookUsecase, // Uncomment khi dùng thật
     private val courseUsecase: CourseUsecase,
) : BaseViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow(SearchFilter.ALL)
    val activeFilter = _activeFilter.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private val _rawResults = MutableStateFlow<UiState<List<Any>>>(UiState.Success(emptyList()))

    val searchResults: StateFlow<UiState<List<Any>>> = combine(_rawResults, _activeFilter) { rawState, filter ->
        if (rawState is UiState.Success) {
            val filtered = when (filter) {
                SearchFilter.ALL -> rawState.data
                SearchFilter.COURSES -> rawState.data.filterIsInstance<Course>()
                SearchFilter.BOOKS -> rawState.data.filterIsInstance<Book>()
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

    fun onSearchFocusChanged(isFocused: Boolean) {
        _isSearching.value = isFocused
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.isBlank()) {
            _rawResults.value = UiState.Success(emptyList())
        }
    }

    fun setFilter(filter: SearchFilter) {
        _activeFilter.value = filter
    }

    fun performSearch() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _rawResults.value = UiState.Loading
            val foundBooks = bookUsecase.getBook(PagingRequest(search = query))
            val foundCourses = courseUsecase.getCourse(PagingRequest(search = query))

            val combinedResults = foundBooks + foundCourses

            _rawResults.value = if (combinedResults.isEmpty()) {
                UiState.Success(emptyList()) // Hoặc UiState.Error("No results") tùy logic
            } else {
                UiState.Success(combinedResults)
            }
        }
    }
}