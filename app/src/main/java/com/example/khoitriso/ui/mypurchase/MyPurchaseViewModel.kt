package com.example.khoitriso.ui.mypurchase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.MyBookDto
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.MyBook
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.repository.BookRepository
import com.example.khoitriso.domain.repository.CourseRepository
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPurchaseViewModel @Inject constructor(
    private val courseUsecase: CourseUsecase,
    private val bookUsecase: BookUsecase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val _activeTab: MutableStateFlow<Int> = MutableStateFlow(0)


    private val _courses = MutableStateFlow<UiState<List<MyCourse>>>(UiState.Loading)
    val courses: StateFlow<UiState<List<MyCourse>>> = _courses
    private val _books = MutableStateFlow<UiState<List<MyBook>>>(UiState.Loading)
    val books: StateFlow<UiState<List<MyBook>>> = _books

    val activeTab: StateFlow<Int> = _activeTab


    init {
        when(savedStateHandle.get<String>("tab")){
            "courses" -> setActiveTab(0)
            "books" -> setActiveTab(1)
        }

        loadCourses()
        loadBooks()
    }

    fun loadCourses() {
        loadData(
            stateFlow = _courses,
            mockData = MockData.mockMyCoursesList,
            apiCall = {
                courseUsecase.getMyCourse()
            }
        )
    }

    fun loadBooks() {
        loadData(
            stateFlow = _books,
            mockData = MockData.mockMyBooksList,
            apiCall = {
                bookUsecase.getMyBook()
            }
        )
    }

//    fun exportBookToWord(bookId: Int, includeExplanation: Boolean) {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(exportingBookId = bookId, error = null)
//            bookRepository.exportBookToWord(bookId, includeExplanation).fold(
//                onSuccess = { bytes ->
//                    _uiState.value = _uiState.value.copy(
//                        exportingBookId = null,
//                        exportedBookBytes = bytes,
//                        exportedBookId = bookId
//                    )
//                },
//                onFailure = { exception ->
//                    _uiState.value = _uiState.value.copy(
//                        exportingBookId = null,
//                        error = "Không thể xuất sách. Vui lòng thử lại sau."
//                    )
//                }
//            )
//        }
//    }

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }
//
//    fun clearExportedBook() {
//        _uiState.value = _uiState.value.copy(
//            exportedBookBytes = null,
//            exportedBookId = null
//        )
//    }
//
//    fun clearError() {
//        _uiState.value = _uiState.value.copy(error = null)
//    }
}


