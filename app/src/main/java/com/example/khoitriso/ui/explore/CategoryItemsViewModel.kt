package com.example.khoitriso.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.request.PagingRequest
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryItemsViewModel @Inject constructor(
    private val courseUsecase: CourseUsecase,
    private val bookUsecase: BookUsecase
) : BaseViewModel() {

    private val _courses = MutableStateFlow<UiState<List<Course>>>(UiState.Loading)
    val courses: StateFlow<UiState<List<Course>>> = _courses

    private val _books = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val books: StateFlow<UiState<List<Book>>> = _books

    fun loadCategoryItems(categoryId: Int) {
        loadCourses(categoryId)
        loadBooks(categoryId)
    }

    private fun loadCourses(categoryId: Int) {
        viewModelScope.launch {
            _courses.value = UiState.Loading
            courseUsecase.getCourse(PagingRequest(pageSize = 100)).fold(
                onSuccess = { response ->
                    // Filter courses by categoryId on client side
                    val filteredCourses = response.items.filter { 
                        it.category.id == categoryId
                    }
                    _courses.value = UiState.Success(filteredCourses)
                },
                onFailure = { error ->
                    _courses.value = UiState.Error(error.message ?: "Không thể tải khóa học")
                }
            )
        }
    }

    private fun loadBooks(categoryId: Int) {
        viewModelScope.launch {
            _books.value = UiState.Loading
            bookUsecase.getBook(PagingRequest(pageSize = 100)).fold(
                onSuccess = { response ->
                    // Filter books by categoryId on client side
                    val filteredBooks = response.items.filter { 
                        it.category.id == categoryId
                    }
                    _books.value = UiState.Success(filteredBooks)
                },
                onFailure = { error ->
                    _books.value = UiState.Error(error.message ?: "Không thể tải sách")
                }
            )
        }
    }
}
