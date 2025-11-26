package com.example.khoitriso.ui.behavior

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.app.Person
import com.example.khoitriso.domain.models.Instructor
import com.example.khoitriso.ui.theme.StarColor
import com.example.khoitriso.utils.debug
import com.example.khoitriso.utils.toDecimal
import com.example.khoitriso.utils.toVND
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun SafeImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Int = R.drawable.avatar_default, // drawable dự phòng
    error: Int = R.drawable.course_test, // drawable khi load lỗi,
    contentScale: ContentScale = ContentScale.Crop
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
            contentScale = contentScale
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
fun MyLoadingProcessing(modifier: Modifier = Modifier){
    CircularProgressIndicator()
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
fun RowBookCard(listItem: List<Book>, navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(listItem.size) { index ->
            BookCard(listItem[index], navController, Modifier.fillMaxWidth(0.4f))
        }
    }
}

@Composable
fun BookCard(book: Book, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .width(250.dp)
            .clickable {
                debug("BookCard: ${book.id}", "BookCard")
                navController.navigate(NavRoute.NavBookDetail(book.id))
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp, // Độ dày của border
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // Hình ảnh
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                SafeImage(
                    contentScale = ContentScale.Fit,
                    url = book.coverImage,
                    contentDescription = book.title,
                    error = Constants.BOOK_DEFAULT_COVER_IMAGE,
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                )
            }

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
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                ItemCardBottom(book.rating, false, book.price, book.totalReviews)
            }
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    // Sử dụng Card Composable để tạo hiệu ứng card với elevation và shape (giữ cấu trúc dọc)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                debug("CourseCard: ${course.id}", "CourseCard")
                navController.navigate(NavRoute.NavCourseDetail(course.id))
            },
        shape = RoundedCornerShape(12.dp), // Góc bo tròn
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Bóng đổ
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            SafeImage(
                url = course.thumbnail,
                contentDescription = course.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "By ${course.instructor.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ItemCardBottom(
                    course.rating,
                    course.isFree,
                    course.price,
                    course.totalReviews
                )
            }
        }
    }
}

@Composable
fun ItemCardBottom(rating: Float, isFree: Boolean, price: Int, totalReviews: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {


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
    emptyColor: Color = Color.LightGray,
) {

    Row(modifier = modifier) {
        Text(
            text = "$rating",
            style = MaterialTheme.typography.bodyMedium
                .copy(fontSize = 16.sp,color = StarColor, fontWeight = FontWeight.Bold),

            )
        Spacer(modifier = Modifier.width(4.dp))
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

//-----------------------------------------DETAIL SCREEN-----------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        modifier = modifier.background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun ActionButtons(price: Int, onBuy: () -> Unit, onCart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Nút thêm vào giỏ hàng (chỉ có icon)
        OutlinedIconButton(
            onClick = onCart,
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Icon(
                Icons.Outlined.ShoppingCart,
                contentDescription = "Add to Cart",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nút mua ngay
        Button(
            onClick = onBuy,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {

            Text(
                text = "${stringResource(R.string.buy_now)} - ${price.toVND()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CreateBy(fullName: String,avatar: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        // avatar may be an Int resource in mock data
        SafeImage(
            url = avatar,
            contentDescription = fullName,
            error = R.drawable.avatar_default,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row {
            Text(
                text = stringResource(R.string.created_by),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = fullName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

//Forum
@SuppressLint("SimpleDateFormat")
fun FormatTimeAgo(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return dateString
        val now = Date()
        val diffInSeconds = (now.time - date.time) / 1000

        when {
            diffInSeconds < 60 -> "vừa xong"
            diffInSeconds < 3600 -> "${diffInSeconds / 60} phút trước"
            diffInSeconds < 86400 -> "${diffInSeconds / 3600} giờ trước"
            diffInSeconds < 2592000 -> "${diffInSeconds / 86400} ngày trước"
            diffInSeconds < 31536000 -> "${diffInSeconds / 2592000} tháng trước"
            else -> "${diffInSeconds / 31536000} năm trước"
        }
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) { Text("Trước") }
        Spacer(Modifier.width(16.dp))
        Text("Trang $currentPage / $totalPages", fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(16.dp))
        OutlinedButton(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) { Text("Sau") }
    }
}
