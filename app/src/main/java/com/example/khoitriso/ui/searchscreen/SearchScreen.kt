package com.example.khoitriso.ui.searchscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import com.example.khoitriso.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.ui.common.BookCard
import com.example.khoitriso.ui.common.CourseCard
import com.example.khoitriso.utils.SearchType
import com.example.khoitriso.utils.UiState

@Composable
fun SearchScreen(
    navController: NavController,
    paddingValues: PaddingValues, // Padding từ MainScreen (BottomBar)
    initialTab: Int? = null,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val focusManager = LocalFocusManager.current

    if(initialTab!= null){
        viewModel.initTab(initialTab)
    }
    // Scaffold giúp quản lý cấu trúc màn hình tốt hơn
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), // Áp dụng padding của bottom bar vào toàn bộ màn hình
        topBar = {
            // Header chứa SearchBar và Filter
            // Đặt ở topBar để nó luôn cố định khi scroll list kết quả
            SearchHeader(
                query = query,
                activeFilter = activeFilter,
                onQueryChange = viewModel::onQueryChange,
                onSearch = {
                    viewModel.performSearch()
                    focusManager.clearFocus()
                },
                onBack = { navController.popBackStack() }, // Logic back thực tế
                onFocusChanged = viewModel::onSearchFocusChanged,
                onFilterSelected = viewModel::setFilter
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            // 1. Nội dung kết quả tìm kiếm (Nằm dưới cùng)
            SearchContent(
                results = results,
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Lớp phủ mờ khi đang focus tìm kiếm (Nằm đè lên nội dung, nhưng dưới SearchBar vì SearchBar ở TopBar)
            // Lớp phủ này giúp tập trung sự chú ý vào thanh tìm kiếm
            AnimatedVisibility(
                visible = isSearching,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusManager.clearFocus() // Ẩn bàn phím và bỏ focus khi click ra ngoài
                        }
                )
            }
        }
    }
}

@Composable
fun SearchHeader(
    query: String,
    activeFilter: Int,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onFilterSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 8.dp) // Khoảng cách nhẹ với content
    ) {
        SearchBarField(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onBack = onBack,
            onFocusChanged = onFocusChanged,
            modifier = Modifier.padding(16.dp)
        )

        FilterChipsRow(
            selectedFilter = activeFilter,
            onFilterSelected = onFilterSelected,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun SearchBarField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Sử dụng OutlinedTextField với shape tròn cho hiện đại
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChanged(it.isFocused) },
        placeholder = { Text(stringResource(R.string.search_placeholder), fontSize = 10.sp) },
        leadingIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            } else {
                // Icon search trang trí khi chưa có text
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp), // Bo tròn như Google Search Bar
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}

@Composable
private fun FilterChipsRow(
    selectedFilter: Int,
    onFilterSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Dùng LazyRow để có thể cuộn ngang nếu nhiều filter
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChipItem(
                selected = selectedFilter == SearchType.ALL,
                label = "Tất cả",
                onClick = { onFilterSelected(SearchType.ALL) }
            )
        }
        item {
            FilterChipItem(
                selected = selectedFilter == SearchType.COURSE,
                label = "Khóa học",
                onClick = { onFilterSelected(SearchType.COURSE) }
            )
        }
        item {
            FilterChipItem(
                selected = selectedFilter == SearchType.BOOK,
                label = "Sách",
                onClick = { onFilterSelected(SearchType.BOOK) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipItem(selected: Boolean, label: String, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun SearchContent(
    results: UiState<List<Any>>,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (val state = results) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Error -> {
                EmptyStateMessage(
                    message = state.message,
                    icon = Icons.Default.SentimentDissatisfied
                )
            }
            is UiState.Success -> {
                val data = state.data
                if (data.isEmpty()) {
                    // Trạng thái trống ban đầu hoặc không tìm thấy
                    EmptyStateMessage(
                        message = "Nhập từ khóa để tìm kiếm\nSách hoặc Khóa học",
                        icon = Icons.Default.Search
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(data.size, key = { index ->
                            // Tốt nhất nên dùng ID thật của item làm key thay vì index
                            // Ví dụ: if (item is Book) item.id else (item as Course).id
                            index
                        }) { i ->
                            when (val item = data[i]) {
                                is Book -> BookCard(
                                    book = item,
                                    navController = navController,
                                    modifier = Modifier.clickable { /* Nav to Detail */ }
                                )
                                is Course -> CourseCard(
                                    course = item,
                                    navController = navController,
                                    modifier = Modifier.clickable { /* Nav to Detail */ }
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
private fun EmptyStateMessage(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}