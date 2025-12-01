package com.example.khoitriso.ui.behavior

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.Option
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.ui.forum.HtmlContent
import com.example.khoitriso.ui.forum.OptimizedHtmlContent
import com.example.khoitriso.ui.theme.KhoiTriSoTheme
import com.example.khoitriso.ui.theme.StarColor
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.LevelType
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.QuestionType
import com.example.khoitriso.utils.toDecimal
import com.example.khoitriso.utils.toVND
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.forEach

// ---------------------- UTILS & EXTENSIONS ----------------------

fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer_float"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    this.background(brush)
}

// ---------------------- IMAGE COMPONENTS ----------------------

@Composable
fun SafeImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Int = R.drawable.avatar_default,
    error: Int = R.drawable.course_test,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrEmpty() && url != "Unknown") {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                error = painterResource(id = error),
                placeholder = painterResource(id = placeholder),
                contentScale = contentScale
            )
        } else {
            Image(
                painter = painterResource(placeholder),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
        }
    }
}

// ---------------------- CARDS ----------------------

@Composable
fun RowCourseCard(listItem: List<Course>, navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(listItem.size) { index ->
            CourseCard(
                course = listItem[index],
                navController = navController,
                modifier = Modifier.width(260.dp),
                onActionClick = {
                    // Xử lý sự kiện nút mua/học ngay tại đây nếu cần (hoặc truyền lên trên)
                    navController.navigate(NavRoute.NavCourseDetail(listItem[index].id))
                }
            )
        }
    }
}

@Composable
fun RowBookCard(listItem: List<Book>, navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(listItem.size) { index ->
            BookCard(
                book = listItem[index],
                navController = navController,
                modifier = Modifier.width(260.dp),
                onActionClick = {
                    navController.navigate(NavRoute.NavBookDetail(listItem[index].id))
                }
            )
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    navController: NavController,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .clickable { navController.navigate(NavRoute.NavBookDetail(book.id)) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            SafeImage(
                url = book.coverImage,
                contentDescription = book.title,
                error = Constants.BOOK_DEFAULT_COVER_IMAGE,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    minLines = 2
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SafeImage(
                        url = book.author.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = book.author.fullName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                ItemCardBottom(
                    rating = book.rating,
                    isFree = false, // Sách thường là trả phí
                    price = book.price,
                    totalReviews = book.totalReviews,
                    onActionClick = onActionClick
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    navController: NavController,
    modifier: Modifier = Modifier,
    onFavorite: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .clickable { navController.navigate(NavRoute.NavCourseDetail(course.id)) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box {
                SafeImage(
                    url = course.thumbnail,
                    contentDescription = course.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop,
                )
                if (course.isFree) {
                    FreeBadge(
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(36.dp)
                        .align(Alignment.TopEnd)
                        .clickable { onFavorite() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StyledTag(
                        text = course.category.name,
                        backgroundColor = Color(0xFFE3F2FD),
                        textColor = Color(0xFF2196F3)
                    )
                    when (course.level) {
                        LevelType.NhanBiet -> {
                            StyledTag(
                                text = "Nhận biết",
                                // Màu xanh lá cây, thân thiện, dễ bắt đầu
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                textColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        LevelType.ThongHieu -> {
                            StyledTag(
                                text = "Thông hiểu",
                                // Màu xanh dương, cấp độ trung bình
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        LevelType.VanDungThap -> {
                            StyledTag(
                                text = "Vận dụng thấp",
                                // Màu tím, cấp độ cao hơn
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        LevelType.VanDungCao -> {
                            StyledTag(
                                text = "Vận dụng cao",
                                // Màu đỏ/cam, cấp độ khó nhất, cảnh báo
                                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                textColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    minLines = 2
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SafeImage(
                        url = course.instructor.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = course.instructor.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                ItemCardBottom(
                    rating = course.rating,
                    isFree = course.isFree,
                    price = course.price,
                    totalReviews = course.totalReviews,
                    estimatedDuration = null,
                    onActionClick = onActionClick
                )
            }
        }
    }
}

@Composable
fun FreeBadge(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(bottomEnd = 8.dp),
        modifier = modifier
    ) {
        Text(
            text = "Miễn phí",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

// ---------------------- ACTIONS & BOTTOM ----------------------

@Composable
fun ItemCardBottom(
    rating: Float,
    isFree: Boolean,
    price: Double,
    totalReviews: Int,
    estimatedDuration: Int? = null,
    onActionClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                FractionalRatingStars(rating = rating)
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "(${totalReviews.toDecimal()})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Duration (Optional)
            if (estimatedDuration != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "$estimatedDuration h",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Button
        if (isFree) {
            FreeAction(onClick = onActionClick)
        } else {
            BuyAction(price = price, onClick = onActionClick)
        }
    }
}

@Composable
fun FreeAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Miễn phí",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Có thể học ngay",
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ), // Padding cho nội dung bên trong
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowCircleRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Học ngay",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BuyAction(
    price: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = price.toVND(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary
            )

        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ), // Padding cho nội dung bên trong
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Mua ngay",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ---------------------- SUB-COMPONENTS ----------------------

@Composable
fun StyledTag(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    fontSize = 8.sp
                )
            )
        }
    }
}

@Composable
fun FractionalRatingStars(
    rating: Float,
    starSize: Dp = 14.dp,
    modifier: Modifier = Modifier,
    starColor: Color = StarColor,
    emptyColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$rating",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.width(4.dp))
        for (i in 1..5) {
            val fillFraction = when {
                i <= rating.toInt() -> 1f
                i == rating.toInt() + 1 -> rating - rating.toInt()
                else -> 0f
            }

            Box(modifier = Modifier.size(starSize)) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = emptyColor,
                    modifier = Modifier.matchParentSize()
                )
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

fun FractionalWidthShape(fraction: Float) = GenericShape { size, _ ->
    addRect(Rect(0f, 0f, size.width * fraction, size.height))
}

// ---------------------- DETAIL SCREEN COMPONENTS & HELPERS ----------------------
// Các component DetailHeader, ActionButtons, CreateBy, MyLoadingProcessing,
// PaginationControls, ErrorDisplay, Media3AndroidView, ObserverAsEvent giữ nguyên như cũ
// vì không ảnh hưởng bởi yêu cầu này.
// ... (Giữ nguyên phần còn lại của file)

// ---------------------- DETAIL SCREEN COMPONENTS ----------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailHeader(
    onBack: () -> Unit,
    title: String = "", // Thêm title để linh hoạt
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            if (title.isNotEmpty())
                Text(
                    title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Sử dụng AutoMirrored cho RTL support
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
    )
}

@Composable
fun ActionButtons(price: Double, onBuy: () -> Unit, onCart: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp, // Tạo bóng đổ ngược lên trên
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding(), // Tránh bị che bởi thanh điều hướng hệ thống
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nút Cart
            OutlinedIconButton(
                onClick = onCart,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Icon(
                    Icons.Outlined.ShoppingCart,
                    contentDescription = "Add to Cart",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Nút Buy Now
            Button(
                onClick = onBuy,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.buy_now),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                VerticalDivider(
                    modifier = Modifier.height(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = price.toVND(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CreateBy(fullName: String, avatar: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        SafeImage(
            url = avatar,
            contentDescription = fullName,
            error = R.drawable.avatar_default,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            Text(
                text = stringResource(R.string.created_by),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ---------------------- COMMON & HELPERS ----------------------

@Composable
fun MyLoadingProcessing(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
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
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút Trước
        FilledTonalIconButton(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev")
        }

        Spacer(Modifier.width(20.dp))

        // Text trang
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.height(32.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "$currentPage / $totalPages",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(20.dp))

        // Nút Sau
        FilledTonalIconButton(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next")
        }
    }
}

@Composable
fun ErrorDisplay(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier
                .size(80.dp)
                .background(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    CircleShape
                )
                .padding(16.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Đã xảy ra lỗi!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thử lại")
        }
    }
}

// ---------------------- UTILS ----------------------

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
fun Media3AndroidView(player: ExoPlayer?, modifier: Modifier = Modifier) {
    if (player != null) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f) // Tỉ lệ video chuẩn
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black),
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = true // Hiện controller
                }
            },
            update = { playerView ->
                playerView.player = player
            }
        )
    }
}

@Composable
fun <T> ObserverAsEvent(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1, key2) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}

//---------------------------Question-----------------------------------
@Composable
fun QuestionItem(question: Question, onOptionSelected: (Int, Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "Câu ${question.orderIndex}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Nội dung câu hỏi
                Box(modifier = Modifier.weight(1f)) {
                    OptimizedHtmlContent(html = question.questionContent)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            OptionList(
                questionType = question.questionType,
                options = question.options,
                onOptionSelected = {
                    onOptionSelected(it, question.id)
                }
            )
        }
    }
}

@Composable
fun OptionList(
    questionType: Int,
    options: List<Option>,
    onOptionSelected: (Int) -> Unit,
) {
    when (questionType) {
        QuestionType.MultipleChoice -> MultipleChoiceOptionList(options, onOptionSelected)
        QuestionType.TrueFalse -> TrueFalseOptionList(options, onOptionSelected)
        QuestionType.ShortAnswer -> ShortAnswerInput()
        else -> Text("Loại câu hỏi chưa hỗ trợ", color = MaterialTheme.colorScheme.error)
    }
}



@Composable
fun MultipleChoiceOptionList(options: List<Option>, onOptionSelected: (Int) -> Unit) {
    val (selectedOptionId, setSelectedOption) = remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            val isSelected = option.id == selectedOptionId

            // Animation màu sắc
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                label = "bgColor"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                label = "borderColor"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            setSelectedOption(option.id)
                            onOptionSelected(option.id)
                        }
                    ),
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Radio Icon
                    Icon(
                        imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        OptimizedHtmlContent(html = option.optionText)
                    }
                }
            }
        }
    }
}

@Composable
fun TrueFalseOptionList(
    options: List<Option>,
    onOptionSelected: (Int) -> Unit,
) {
    val (selectedId, setSelected) = remember { mutableStateOf<Int?>(null) }
    val trueOption = options.firstOrNull { it.optionText.equals("Đúng", ignoreCase = true) || it.optionText.equals("True", ignoreCase = true) }
    val falseOption = options.firstOrNull { it.optionText.equals("Sai", ignoreCase = true) || it.optionText.equals("False", ignoreCase = true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (trueOption != null) {
            TrueFalseCard(
                text = "Đúng",
                isSelected = selectedId == trueOption.id,
                icon = Icons.Rounded.Check,
                color = Color(0xFF4CAF50), // Màu xanh lá
                modifier = Modifier.weight(1f),
                onClick = {
                    setSelected(trueOption.id)
                    onOptionSelected(trueOption.id)
                }
            )
        }
        if (falseOption != null) {
            TrueFalseCard(
                text = "Sai",
                isSelected = selectedId == falseOption.id,
                icon = Icons.Rounded.Close,
                color = Color(0xFFE53935), // Màu đỏ
                modifier = Modifier.weight(1f),
                onClick = {
                    setSelected(falseOption.id)
                    onOptionSelected(falseOption.id)
                }
            )
        }
    }
}

@Composable
fun TrueFalseCard(
    text: String,
    isSelected: Boolean,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
    )
    val borderColor by animateColorAsState(
        if (isSelected) color else MaterialTheme.colorScheme.outlineVariant
    )

    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ShortAnswerInput() {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Nhập câu trả lời...") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            Icon(Icons.Rounded.Edit, contentDescription = null)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}