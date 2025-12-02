package com.example.khoitriso.ui.mypurchase

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.khoitriso.R
import com.example.khoitriso.data.dto.MyBookDto
import com.example.khoitriso.data.dto.MyCourseDto
import com.example.khoitriso.domain.models.MyBook
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.ui.common.SafeImage
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Composable
fun MyPurchaseScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: MyPurchaseViewModel = hiltViewModel(),
) {
    val courseState by viewModel.courses.collectAsState()
    val bookState by viewModel.books.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val context = LocalContext.current



    Scaffold(
        modifier = Modifier.padding(paddingValues)
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
    // Tính toán tiến độ đọc
    val progress = if (book.totalChapters > 0)
        book.completedChapters.toFloat() / book.totalChapters.toFloat()
    else 0f

    ElevatedCard( // Dùng ElevatedCard để nổi bật hơn trên nền
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp) // Giảm padding tổng thể một chút để gọn hơn
                .height(IntrinsicSize.Min), // Để chiều cao ảnh và nội dung khớp nhau
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Ảnh bìa sách (Tỉ lệ chuẩn 2:3)
            SafeImage(
                url = book.book.coverImage,
                contentDescription = book.book.title,
                modifier = Modifier
                    .width(80.dp) // Chiều rộng cố định
                    .aspectRatio(0.67f) // Tỉ lệ 2:3 (Book standard)
                    .clip(RoundedCornerShape(8.dp))
                    .shadow(4.dp, RoundedCornerShape(8.dp)), // Thêm bóng đổ nhẹ cho ảnh
                contentScale = ContentScale.Crop
            )

            // 2. Nội dung bên phải
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween // Căn đều trên dưới
            ) {
                // Tiêu đề và thông tin
                Column {
                    Text(
                        text = book.book.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Thanh tiến trình đọc
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${book.completedChapters}/${book.totalChapters} chương",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Khu vực nút bấm (Action Buttons)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút Đọc Tiếp (Nổi bật nhất)
                    Button(
                        onClick = onClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp), // Chiều cao gọn hơn
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.read_book),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Nút Export (Icon Button) - SỬA LỖI ICON Ở ĐÂY
                    // Sử dụng FilledTonalIconButton hoặc OutlinedIconButton để icon hiển thị chuẩn
                    FilledTonalIconButton(
                        onClick = onExportWord,
                        enabled = !isExporting,
                        modifier = Modifier.size(36.dp) // Kích thước vuông vức khớp với nút Đọc
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export Word",
                                modifier = Modifier.size(20.dp) // Kích thước icon chuẩn
                            )
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

