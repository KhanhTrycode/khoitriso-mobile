package com.example.khoitriso.ui.mypurchase

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.khoitriso.domain.models.MyBook
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.ui.behavior.SafeImage
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Composable
fun MyPurchaseScreen(
    navController: NavHostController,
    viewModel: MyPurchaseViewModel = hiltViewModel(),
) {
    val courseState by viewModel.courses.collectAsState()
    val bookState by viewModel.books.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val context = LocalContext.current



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
            TabRow(selectedTabIndex = if (activeTab == 0) 0 else 1) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
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
                    selected = activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.books_tab))
                        }
                    }
                )
            }

            // Content
            when (activeTab) {
                0 -> {
                    when (val courses = courseState) {
                        is UiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is UiState.Error -> {
                            Text(courses.message)

                        }
                        is UiState.Success<List<MyCourse>> -> {
                            MyCourseTab(courses.data,navController = navController)
                        }
                    }
                }
                1-> {
                    when(val books = bookState){
                        is UiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is UiState.Error -> {
                            Text(books.message)

                        }
                        is UiState.Success<List<MyBook>> -> {
                            MyBookTab(books.data,navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyCourseTab(courses: List<MyCourse>,navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(courses.size) { i ->
            MyCourseCard(
                course = courses[i],
                onClick = {
                    navController.navigate(NavRoute.NavLearningCourse(courses[i].courseId))
                }
            )
        }
    }
}

@Composable
private fun MyCourseCard(
    course: MyCourse,
    onClick: () -> Unit,
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
            SafeImage(
                url = course.course.thumbnail,
                contentDescription = course.course.title,
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
                    text = course.course.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.progress)}: ${course.progressPercentage.toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (course.progressPercentage / 100).toFloat(),
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
private fun MyBookTab(books: List<MyBook>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books.size) { i ->
            MyBookCard(
                book = books[i],
                onClick = {
                    navController.navigate(NavRoute.NavLearningBook(books[i].bookId))
                },
                isExporting = false,
                onExportWord = {}
            )
        }
    }
}
@Composable
private fun MyBookCard(
    book: MyBook,
    isExporting: Boolean,
    onClick: () -> Unit,
    onExportWord: () -> Unit,
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
            SafeImage(
                url = book.book.coverImage,
                contentDescription = book.book.title,
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
                    text =  book.book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${book.completedChapters}/${book.totalChapters} ${stringResource(R.string
                        .chapters)}",
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
    modifier: Modifier = Modifier,
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

