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

    val books: StateFlow<UiState<List<Book>>> = _books
    val courses: StateFlow<UiState<List<Course>>> = _courses
    val categories: StateFlow<UiState<List<Category>>> = _categories


    init {
        getCourse()
        getBooks()
        getCategory()

    }

    private fun getCategory() {
        viewModelScope.launch {
            _categories.value = UiState.Loading
            try {
                val result = categoryUsecase.getCategory()
                _categories.value = UiState.Success(result)
                Log.d("HomeViewModel", "getCategory: $result")
            } catch (e: Exception) {
                _categories.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getBooks() {
        viewModelScope.launch {
            _books.value = UiState.Loading
            try {
                val result = bookUsecase.getBook()
                _books.value = UiState.Success(result)
                Log.d("HomeViewModel", "getBook: $result")

            } catch (e: Exception) {
                _books.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getCourse() {
        viewModelScope.launch {
            _courses.value = UiState.Loading
            try {
                val result = courseUsecase.getCourse()
                _courses.value = UiState.Success(result)
                Log.d("HomeViewModel", "getCourse: $result")

            } catch (e: Exception) {
                _courses.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

}