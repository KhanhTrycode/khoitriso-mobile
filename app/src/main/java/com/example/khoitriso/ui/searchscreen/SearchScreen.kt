package com.example.khoitriso.ui.searchscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.ui.behavior.BookCard
import com.example.khoitriso.ui.behavior.CourseCard
import com.example.khoitriso.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState() // Lấy trạng thái focus
    val focusManager = LocalFocusManager.current

    // Box là container chính để chứa tất cả các lớp
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = { }
        ) { paddingValues ->
            SearchContent(
                results = results,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Padding mặc định của Scaffold
                navController = navController,
                query = query
            )
        }

        AnimatedVisibility(
            visible = isSearching,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    // Click vào lớp phủ này sẽ làm mất focus
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // không có hiệu ứng gợn sóng
                    ) {
                        viewModel.onSearchFocusChanged(false)
                        focusManager.clearFocus()
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.TopCenter) // Đặt nó ở trên cùng
        ) {
            SearchBar(
                query = query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = {
                    viewModel.performSearch()
                    focusManager.clearFocus()
                },
                onBack = {
                    focusManager.clearFocus()
                },
                onFocusChanged = { viewModel.onSearchFocusChanged(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            FilterChips(
                selectedFilter = activeFilter,
                onFilterSelected = { viewModel.setFilter(it) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun SearchContent(
    results: UiState<List<Any>>, query: String, navController: NavController,
    modifier:
    Modifier =
        Modifier,
) {
    Box(
        modifier = Modifier
    ) {
        // ... (Toàn bộ logic when (state = results) không thay đổi)
        when (val state = results) {
            is UiState.Loading -> {
                if (query.isNotEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    MessageFullScreen("Search for books and courses")
                }
            }

            is UiState.Error -> {
                MessageFullScreen(state.message)
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    if (query.isEmpty()) {
                        MessageFullScreen("Search for books and courses")
                    } else {
                        MessageFullScreen("No results found for \"$query\"")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.data.size) { i ->
                            when (val item = state.data[i]) {
                                is Book -> BookCard(
                                    book = item,
                                    navController = navController,
                                    modifier = Modifier.clickable { /* Nav */ }
                                )

                                is Course -> CourseCard(
                                    course = item,
                                    navController = navController,
                                    modifier = Modifier.clickable { /* Nav */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    onFocusChanged: (Boolean) -> Unit, // Thêm callback này
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            // Theo dõi sự thay đổi focus của TextField
            .onFocusChanged { focusState ->
                onFocusChanged(focusState.isFocused)
            },
        // ... (các thuộc tính khác không đổi)
        singleLine = true,
        placeholder = { Text("Search...") },
        leadingIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear query")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
private fun FilterChips(
    selectedFilter: SearchFilter,
    onFilterSelected: (SearchFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == SearchFilter.ALL,
            onClick = { onFilterSelected(SearchFilter.ALL) },
            label = { Text("All") }
        )
        FilterChip(
            selected = selectedFilter == SearchFilter.COURSES,
            onClick = { onFilterSelected(SearchFilter.COURSES) },
            label = { Text("Courses") }
        )
        FilterChip(
            selected = selectedFilter == SearchFilter.BOOKS,
            onClick = { onFilterSelected(SearchFilter.BOOKS) },
            label = { Text("Books") }
        )
    }
}

@Composable
private fun MessageFullScreen(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
