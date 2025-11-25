package com.example.khoitriso.ui.mypurchase

import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.dto.MyBookDto
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.domain.repository.BookRepository
import com.example.khoitriso.domain.repository.CourseRepository
import com.example.khoitriso.ui.behavior.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPurchaseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val bookRepository: BookRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MyPurchaseUiState())
    val uiState: StateFlow<MyPurchaseUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
        loadBooks()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCourses = true, error = null)
            courseRepository.getMyCourses().fold(
                onSuccess = { courses ->
                    _uiState.value = _uiState.value.copy(
                        courses = courses,
                        isLoadingCourses = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingCourses = false,
                        error = "Không thể tải khóa học. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingBooks = true, error = null)
            bookRepository.getMyBooks().fold(
                onSuccess = { books ->
                    _uiState.value = _uiState.value.copy(
                        books = books,
                        isLoadingBooks = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingBooks = false,
                        error = "Không thể tải sách. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun exportBookToWord(bookId: Int, includeExplanation: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(exportingBookId = bookId, error = null)
            bookRepository.exportBookToWord(bookId, includeExplanation).fold(
                onSuccess = { bytes ->
                    _uiState.value = _uiState.value.copy(
                        exportingBookId = null,
                        exportedBookBytes = bytes,
                        exportedBookId = bookId
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        exportingBookId = null,
                        error = "Không thể xuất sách. Vui lòng thử lại sau."
                    )
                }
            )
        }
    }

    fun setActiveTab(tab: String) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun clearExportedBook() {
        _uiState.value = _uiState.value.copy(
            exportedBookBytes = null,
            exportedBookId = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class MyPurchaseUiState(
    val activeTab: String = "courses",
    val courses: List<MyCourseDto> = emptyList(),
    val books: List<MyBookDto> = emptyList(),
    val isLoadingCourses: Boolean = false,
    val isLoadingBooks: Boolean = false,
    val error: String? = null,
    val exportingBookId: Int? = null,
    val exportedBookBytes: ByteArray? = null,
    val exportedBookId: Int? = null
)

