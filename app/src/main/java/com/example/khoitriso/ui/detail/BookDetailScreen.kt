package com.example.khoitriso.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.Chapter
import com.example.khoitriso.ui.behavior.ActionButtons
import com.example.khoitriso.ui.behavior.CreateBy
import com.example.khoitriso.ui.behavior.DetailHeader
import com.example.khoitriso.ui.behavior.FractionalRatingStars
import com.example.khoitriso.ui.behavior.SafeImage
import com.example.khoitriso.utils.Constants
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
            DetailHeader(onBack = {
                navController.popBackStack()
            })
        },
        bottomBar = {
            if (bookState is UiState.Success) {
                ActionButtons(
                    price = (bookState as UiState.Success<BookDetail>).data.price,
                    onBuy = { viewModel.buyNow() },
                    onCart = { viewModel.addToCart((bookState as UiState.Success<BookDetail>)
                        .data.id) }
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
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        //1. Book Info
        item {
            BookInfo(book = book)
            Spacer(modifier= Modifier.height(16.dp))
        }
        //2.Rating
        item {
            FractionalRatingStars(book.rating)
            Text(
                text = "(${book.totalReviews} reviews)",
                style = MaterialTheme.typography.labelMedium
            )
        }

    }
}

@Composable
private fun BookInfo(book: BookDetail, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SafeImage(
                url = book.coverImage,
                contentDescription = book.title,
                error = Constants.BOOK_DEFAULT_COVER_IMAGE,
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            )
        }
        Spacer(modifier= Modifier.height(10.dp))

        Text(
            text = book.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier= Modifier.height(10.dp))
        CreateBy(book.author.fullName,book.author.avatar)
        Spacer(modifier= Modifier.height(15.dp))


        Text(
            text = book.description,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
    }
}

