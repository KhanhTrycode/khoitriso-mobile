package com.example.khoitriso.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.ui.behavior.BookCard
import com.example.khoitriso.ui.behavior.PaginationControls
import com.example.khoitriso.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreBookScreen(
    navController: NavController,
    viewModel: ExploreBookViewModel = hiltViewModel(),
) {
    val bookState = viewModel.book.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Khám phá") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            ScreenContent(
                bookState = bookState.value,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ScreenContent(
    bookState: UiState<MyResponese<Book>>,
    navController: NavController,
    viewModel: ExploreBookViewModel
) {
    when (bookState) {
        is UiState.Error -> {
            Text(text = bookState.message)
        }
        is UiState.Loading -> {
            CircularProgressIndicator()
        }
        is UiState.Success -> {
            // Sử dụng Column thay vì LazyColumn
            Column(modifier = Modifier.fillMaxSize()) {
                BookGrid(
                    books = bookState.data.items,
                    navController = navController,
                    // Chiếm phần lớn không gian có sẵn
                    modifier = Modifier.weight(1f)
                )
                PaginationControls(
                    currentPage = bookState.data.page,
                    totalPages = bookState.data.totalPages
                ) {
                    viewModel.changePage(it)
                }
            }
        }
    }
}

@Composable
fun BookGrid(books: List<Book>, navController: NavController, modifier: Modifier = Modifier) {
    if (books.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Không tìm thấy cuốn sách nào.")
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier, // Áp dụng modifier được truyền vào
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookCard(
                book = book,
                navController = navController,
            )
        }
    }
}
