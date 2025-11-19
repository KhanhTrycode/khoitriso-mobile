package com.example.khoitriso.ui.searchscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.test.MockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val bookUsecase: BookUsecase,
    private val courseUsecase: CourseUsecase
) : ViewModel() {

    private val _query = MutableStateFlow("")

    private val _searchResults = MutableStateFlow<List<Any>>(emptyList()) // Any = Book hoặc Course
    val searchResults: StateFlow<List<Any>> = _searchResults

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        performSearch(newQuery)
    }

    fun performSearch(keyword: String) {
        viewModelScope.launch {
            // giả lập lấy data từ mock hoặc usecase
            val books = MockData.books.filter { it.title.contains(keyword, ignoreCase = true) }
            val courses = MockData.mockCourses.filter { it.title.contains(keyword, ignoreCase = true) }
            _searchResults.value = books + courses
            Log.d("Search",_searchResults.value.toString())
        }
    }
}
