package com.example.khoitriso.ui.learning

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import com.example.khoitriso.data.local.UserManager
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.Chapter
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.test.MockData
import com.example.khoitriso.ui.behavior.BaseViewModel
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LearningBookViewModel @Inject constructor(
    private val bookUsecase: BookUsecase,
    private val userManager: UserManager,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {
    private val _book: MutableStateFlow<UiState<BookDetail>> = MutableStateFlow(UiState.Loading)
    val book: StateFlow<UiState<BookDetail>> = _book
    private val _chapters: MutableStateFlow<UiState<List<Chapter>>> = MutableStateFlow(UiState.Loading)
    val chapters: StateFlow<UiState<List<Chapter>>> = _chapters
    private val _questions: MutableStateFlow<UiState<List<Question>>> = MutableStateFlow(UiState
        .Loading)
    val questions: StateFlow<UiState<List<Question>>> = _questions
    private val _currentChapterIndex = MutableStateFlow(1)
    val currentChapterIndex: StateFlow<Int> = _currentChapterIndex
    init {
        loadBook()
    }

    fun loadBook() {
        getBookById(){
            debug(_book.value.toString(),"test")
            loadChapters()
        }
    }

    private fun getBookById(onSucess: (UiState.Success<BookDetail>) -> Unit){
        val id = savedStateHandle.get<Int>("bookId") ?: -1
        loadData(
            stateFlow = _book,
            mockData = MockData.mockBookDetail1,
            apiCall = {
                bookUsecase.getBookById(id)
            },
            onSuccess = onSucess
        )
    }

    private fun loadChapters(){
//        val id = savedStateHandle.get<Int>("bookId") ?: -1
//        loadData(
//            stateFlow = _chapters,
//            mockData = MockData.mockChapters,
//            apiCall = {
//                bookUsecase.getChapterOfBook(id)
//            }
//        )
        _chapters.value = UiState.Success(MockData.mockChapters)
        loadQuestion()
    }

    fun changeChapter(chapterIndex: Int){
        _currentChapterIndex.value += chapterIndex
    }

    private fun loadQuestion(){
        _questions.value = UiState.Success(MockData.mockQuestionList)
    }
}