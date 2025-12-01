package com.example.khoitriso.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.ui.behavior.*
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.ItemBuyNow
import com.example.khoitriso.utils.ItemType
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val bookState by viewModel.book.collectAsState()

    Scaffold(
        topBar = {
            DetailHeader(onBack = { navController.popBackStack() })
        },
        bottomBar = {
            if (bookState is UiState.Success) {
                ActionButtons(
                    price = (bookState as UiState.Success<BookDetail>).data.price,
                    onBuy = {
                        val book =(bookState as UiState.Success<BookDetail>).data
                        navController.navigate(ItemBuyNow(
                            itemId = book.id,
                            itemType = ItemType.Book,
                            coverImage = book.coverImage,
                            price = book.price,
                            title = book.title,
                        ))
                    },
                    onCart = { viewModel.addToCart((bookState as UiState.Success<BookDetail>).data.id) }
                )
            }
        }
    ) { paddingValues ->
        when (val state = bookState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is UiState.Success -> {
                BookDetailContent(book = state.data, modifier = Modifier.padding(paddingValues))
            }
        }
    }
}

@Composable
private fun BookDetailContent(book: BookDetail, modifier: Modifier = Modifier) {
    // State để quản lý Tabs
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Giới thiệu", "Mục lục")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // 1. Header Sách (Ảnh bìa + Thông tin cơ bản)
        item {
            BookHeaderSection(book)
        }

        // 2. Tab Row (Sticky Header giả lập hoặc item thường)
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        }

        // 3. Nội dung Tab
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (selectedTabIndex == 0) {
            // Tab Giới thiệu
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Mô tả",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Thông tin thêm (Ví dụ)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        BookMetaItem("Ngôn ngữ", book.language)
                        BookMetaItem("Năm xuất bản", book.publicationYear.toString())
                        BookMetaItem("Tái bản", book.edition)
                    }
                }
            }
        } else {
            // Tab Mục lục (Chapters)
            if (book.chapters.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Chưa có thông tin mục lục", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                itemsIndexed(book.chapters) { index, chapter ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp)
                            )
                            Column {
                                Text(
                                    text = chapter.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                if (chapter.questionCount > 0) {
                                    Text(
                                        text = "${chapter.questionCount} câu hỏi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun BookHeaderSection(book: BookDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp), // Padding rộng hơn cho thoáng
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ảnh bìa sách với Shadow
        SafeImage(
            url = book.coverImage,
            contentDescription = book.title,
            error = Constants.BOOK_DEFAULT_COVER_IMAGE,
            modifier = Modifier
                .height(240.dp) // Chiều cao cố định
                .aspectRatio(2f / 3f) // Tỉ lệ chuẩn sách
                .shadow(12.dp, RoundedCornerShape(8.dp)) // Đổ bóng đẹp
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tên sách
        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tác giả
        CreateBy(
            fullName = book.author.fullName,
            avatar = book.author.avatar
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Rating
        Row(verticalAlignment = Alignment.CenterVertically) {
            FractionalRatingStars(book.rating)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${book.totalReviews} reviews)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BookMetaItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}