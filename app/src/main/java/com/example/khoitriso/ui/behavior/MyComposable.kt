package com.example.khoitriso.ui.behavior

import android.media.browse.MediaBrowser.MediaItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.khoitriso.ui.theme.StarColor
import com.example.khoitriso.utils.toDecimal
import com.example.khoitriso.utils.toVND


@Composable
fun SafeImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Int = R.drawable.avatar_default, // drawable dự phòng
    error: Int = R.drawable.course_test, // drawable khi load lỗi
) {
    if (url != "Unknown") {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url ?: "") // nếu url null, Coil vẫn load placeholder
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            placeholder = painterResource(id = placeholder),
            error = painterResource(id = error),
            fallback = painterResource(id = placeholder),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(placeholder),
            contentDescription = "Thumbnail",
            modifier = modifier
        )

    }
}

@Composable
fun RowCourseCard(listItem: List<Course>, navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(listItem.size) { index ->
            CourseCard(listItem[index], navController)
        }
    }
}

@Composable
fun RowBookCard(listItem: List<Book>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(listItem.size) { index ->
            BookCard(listItem[index])
        }
    }
}

@Composable
fun BookCard(book: Book, modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Thumbnail full width
        SafeImage(
            url = book.coverImage,
            contentDescription = book.title,
            error = Constants.BOOK_DEFAULT_COVER_IMAGE,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        )

        // Info below image
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onBackground
            )
            ItemCardBottom(book.rating,false,book.price,book.totalReviews)
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable{
                navController.navigate(NavRoute.NavCourseDetail(course.id))
            }
    ) {
        // Thumbnail full width
        SafeImage(
            url = course.thumbnail,
            contentDescription = course.title,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        )

        // Info below image
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "By ${course.instructor.name}",
                style = MaterialTheme.typography.labelMedium,
            )
            ItemCardBottom(course.rating,course.isFree,course.price,course.totalReviews)
        }
    }
}

@Composable
fun ItemCardBottom(rating: Float, isFree: Boolean, price: Int, totalReviews: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "$rating",
            style = MaterialTheme.typography.bodyMedium
            .copy(fontSize = 16.sp,color = StarColor, fontWeight = FontWeight.Bold),

        )
        Spacer(modifier = Modifier.width(4.dp))
        FractionalRatingStars(rating = rating)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "(${totalReviews.toDecimal()})",
            style = MaterialTheme.typography.labelMedium
            )
    }

    Text(
        text = if (isFree) "Free" else price.toVND(),
        style = MaterialTheme.typography.bodyMedium,
        color = if (isFree) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun FractionalRatingStars(
    rating: Float,
    starSize: Dp = 16.dp,
    modifier: Modifier = Modifier,
    starColor: Color = StarColor,
    emptyColor: Color = Color.LightGray
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val fillFraction = when {
                i <= rating.toInt() -> 1f // full star
                i == rating.toInt() + 1 -> rating - rating.toInt() // fractional star
                else -> 0f // empty star
            }

            Box(
                modifier = Modifier
                    .size(starSize)
            ) {
                // Background: empty star
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = emptyColor,
                    modifier = Modifier.matchParentSize()
                )
                // Foreground: filled portion
                if (fillFraction > 0f) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = starColor,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(FractionalWidthShape(fillFraction))
                    )
                }
            }
        }
    }
}

// Shape cắt icon theo phần trăm width
fun FractionalWidthShape(fraction: Float) = GenericShape { size, _ ->
    addRect(androidx.compose.ui.geometry.Rect(0f, 0f, size.width * fraction, size.height))
}