package com.example.khoitriso.ui.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.book.BookUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.usecase.category.CategoryUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.launch
import kotlin.math.log

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookUsecase: BookUsecase,
    private val courseUsecase: CourseUsecase,
    private val categoryUsecase: CategoryUsecase,
) : BaseViewModel() {

    private val _categories = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    private val _courses = MutableStateFlow<UiState<List<Course>>>(UiState.Loading)
    private val _books = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)

    val categories: StateFlow<UiState<List<Category>>> = _categories

    private val _recommendedBooks = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val recommendedBooks: StateFlow<UiState<List<Book>>> = _recommendedBooks

    private val _trendingCourses = MutableStateFlow<UiState<List<Course>>>(UiState.Loading)
    val trendingCourses: StateFlow<UiState<List<Course>>> = _trendingCourses

    private val _tryCourses = MutableStateFlow<UiState<Course>>(UiState.Loading)
    val tryCourses: StateFlow<UiState<Course>> = _tryCourses

    init {
        getCourse()
        getBooks()
        getCategory()
    }

    private fun getCategory() {
        viewModelScope.launch {
            _categories.value = UiState.Loading
            try {
                val result = MockData.categories
                _categories.value = UiState.Success(result)
            } catch (e: Exception) {
                _categories.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getBooks() {
        viewModelScope.launch {
            _books.value = UiState.Loading
            try {
                val result = MockData.books
                _books.value = UiState.Success(result)

                // update recommended books
                val recommended = if (result.size >= 5) result.subList(0, 5) else result
                _recommendedBooks.value = UiState.Success(recommended)
                Log.d("HomeViewModel", "getBooks: $recommended")
                Log.d("HomeViewModel", "getBooks: ${_books.value}")
            } catch (e: Exception) {
                _books.value = UiState.Error(e.message ?: "Unknown error")
                _recommendedBooks.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getCourse() {
        viewModelScope.launch {
            _courses.value = UiState.Loading
            try {
                val result = MockData.mockCourses
                _courses.value = UiState.Success(result)

                // update trending courses
                val trending = if (result.size >= 5) result.subList(0, 5) else result
                _trendingCourses.value = UiState.Success(trending)

                // update try course (take first course)
                if (result.isNotEmpty()) {
                    _tryCourses.value = UiState.Success(result.first())
                } else {
                    _tryCourses.value = UiState.Error("No courses available")
                }

            } catch (e: Exception) {
                _courses.value = UiState.Error(e.message ?: "Unknown error")
                _trendingCourses.value = UiState.Error(e.message ?: "Unknown error")
                _tryCourses.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

}
