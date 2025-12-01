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
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookUsecase: BookUsecase,
    private val courseUsecase: CourseUsecase,
    private val categoryUsecase: CategoryUsecase,
    private val userManager: UserManager
) : BaseViewModel() {

    private val _categories = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    private val _courses = MutableStateFlow<UiState<List<Course>>>(UiState.Loading)
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

    init {
        getUser()
        getCourse()
        getBooks()
        getCategory()
        getMyCourse()
    }

    private fun getUser(){
        if(_isTestMode){
            _user.value = MockData.mockUser1
        }
        else{
            loadUser(_user, userManager)
        }
    }

    private fun getCategory() {
        loadData(_categories, MockData.mockCategories, apiCall = {
            categoryUsecase.getCategory()
        })
    }

    fun getBooks() {
        loadDataWithPage(
            _books, mockData = MockData.mockBooks
        ) {
            bookUsecase.getBook()
        }
        viewModelScope.launch {
            _books.collectLatest { bookState ->
                if (bookState is UiState.Success<MyResponese<Book>>) {
                    val books = bookState.data.items
                    val recommendedBooks = books.shuffled().take(5)
                    _recommendedBooks.value = UiState.Success(recommendedBooks)

                }
            }
        }
    }

    fun getMyCourse(){
        loadData(
            stateFlow = _myCourses,
            mockData = MockData.mockMyCoursesList,
            apiCall = {
                courseUsecase.getMyCourse()
            }
        )
    }

    fun getCourse() {
        loadData(_courses, MockData.mockCourses, apiCall = {
            courseUsecase.getCourse()
        })
        viewModelScope.launch {
            _courses.collectLatest { courseState ->
                if (courseState is UiState.Success<List<Course>>) {
                    val courses = courseState.data
                    val trendingCourses = courses.shuffled().take(5)
                    _trendingCourses.value = UiState.Success(trendingCourses)
                    _tryCourses.value = UiState.Success(trendingCourses.first())
                }
            }
        }
    }
}
