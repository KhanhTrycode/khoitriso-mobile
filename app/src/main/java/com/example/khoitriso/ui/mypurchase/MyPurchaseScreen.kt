package com.example.khoitriso.ui.mypurchase

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.khoitriso.R
import com.example.khoitriso.data.dto.MyBookDto
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.utils.NavRoute
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.*

@Composable
fun MyPurchaseScreen(
    navController: NavHostController,
    initialTab: String = "courses",
    viewModel: MyPurchaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (uiState.activeTab != initialTab) {
            viewModel.setActiveTab(initialTab)
        }
    }

    // Handle exported book download
    LaunchedEffect(uiState.exportedBookBytes, uiState.exportedBookId) {
        uiState.exportedBookBytes?.let { bytes ->
            uiState.exportedBookId?.let { bookId ->
                downloadBookFile(context, bytes, bookId)
                viewModel.clearExportedBook()
            }
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.my_purchases_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(selectedTabIndex = if (uiState.activeTab == "courses") 0 else 1) {
                Tab(
                    selected = uiState.activeTab == "courses",
                    onClick = { viewModel.setActiveTab("courses") },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.courses_tab))
                        }
                    }
                )
                Tab(
                    selected = uiState.activeTab == "books",
                    onClick = { viewModel.setActiveTab("books") },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.books_tab))
                        }
                    }
                )
            }

            // Content
            when (uiState.activeTab) {
                "courses" -> {
                    if (uiState.isLoadingCourses) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.courses.isEmpty()) {
                        EmptyState(
                            title = stringResource(R.string.no_courses),
                            onAction = { navController.navigate(NavRoute.home) },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.courses) { course ->
                                MyCourseCard(
                                    course = course,
                                    onClick = {
                                        navController.navigate(NavRoute.NavCourseDetail(course.CourseId))
                                    }
                                )
                            }
                        }
                    }
                }
                "books" -> {
                    if (uiState.isLoadingBooks) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.books.isEmpty()) {
                        EmptyState(
                            title = stringResource(R.string.no_books),
                            onAction = { navController.navigate(NavRoute.home) },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.books) { book ->
                                MyBookCard(
                                    book = book,
                                    isExporting = uiState.exportingBookId == book.BookId,
                                    onClick = {
                                        navController.navigate(NavRoute.NavBookDetail(book.BookId))
                                    },
                                    onExportWord = {
                                        viewModel.exportBookToWord(book.BookId, includeExplanation = false)
                                    }
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
private fun MyCourseCard(
    course: MyCourseDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            AsyncImage(
                model = course.Course.Thumbnail ?: "",
                contentDescription = course.Course.Title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = course.Course.Title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.progress)}: ${course.ProgressPercentage.toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (course.ProgressPercentage / 100).toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onClick) {
                    Text(stringResource(R.string.continue_learning))
                }
            }
        }
    }
}

@Composable
private fun MyBookCard(
    book: MyBookDto,
    isExporting: Boolean,
    onClick: () -> Unit,
    onExportWord: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cover
            AsyncImage(
                model = book.Book.CoverImage ?: "",
                contentDescription = book.Book.Title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.Book.Title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${book.CompletedChapters}/${book.TotalChapters} ${stringResource(R.string.chapters)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onClick) {
                        Text(stringResource(R.string.read_book))
                    }
                    OutlinedButton(
                        onClick = onExportWord,
                        enabled = !isExporting
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text(stringResource(R.string.export_word))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAction) {
            Text(stringResource(R.string.continue_shopping))
        }
    }
}

private fun downloadBookFile(context: Context, bytes: ByteArray, bookId: Int) {
    try {
        val fileName = "book_${bookId}_${System.currentTimeMillis()}.docx"
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { it.write(bytes) }
        // TODO: Show notification or snackbar that file is downloaded
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

