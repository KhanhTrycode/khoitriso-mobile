package com.example.khoitriso.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.ui.common.BookCard
import com.example.khoitriso.ui.common.CourseCard
import com.example.khoitriso.utils.ItemType
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItemsScreen(
    categoryId: Int,
    categoryName: String,
    navController: NavController,
    viewModel: CategoryItemsViewModel = hiltViewModel()
) {
    val coursesState by viewModel.courses.collectAsState()
    val booksState by viewModel.books.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(categoryId) {
        viewModel.loadCategoryItems(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("Khóa học")
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("Sách")
                        }
                    }
                )
            }

            // Content
            when (selectedTab) {
                0 -> {
                    when (val state = coursesState) {
                        is UiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(state.message)
                            }
                        }
                        is UiState.Success -> {
                            if (state.data.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Không có khóa học nào trong danh mục này")
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.data.size) { index ->
                                        CourseCard(
                                            course = state.data[index],
                                            navController = navController,
                                            onActionClick = {
                                                navController.navigate(
                                                    NavRoute.NavCourseDetail(state.data[index].id)
                                                )
                                            },
                                            onAddToCart = {}
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    when (val state = booksState) {
                        is UiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(state.message)
                            }
                        }
                        is UiState.Success -> {
                            if (state.data.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Không có sách nào trong danh mục này")
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.data.size) { index ->
                                        BookCard(
                                            book = state.data[index],
                                            navController = navController,
                                            onActionClick = {
                                                navController.navigate(
                                                    NavRoute.NavBookDetail(state.data[index].id)
                                                )
                                            },
                                            onAddToCart = {}
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
