package com.example.khoitriso.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.ui.theme.StarColor
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.LevelType
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.toDecimal
import com.example.khoitriso.utils.toVND

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
                    isFree = false,
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
                    FreeBadge(modifier = Modifier.align(Alignment.TopStart))
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
                                text = stringResource(R.string.level_recognition),
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                textColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        LevelType.ThongHieu -> {
                            StyledTag(
                                text = stringResource(R.string.level_understanding),
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        LevelType.VanDungThap -> {
                            StyledTag(
                                text = stringResource(R.string.level_low_application),
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        LevelType.VanDungCao -> {
                            StyledTag(
                                text = stringResource(R.string.level_high_application),
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
            text = stringResource(R.string.free),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Composable
fun ItemCardBottom(
    rating: Float,
    isFree: Boolean,
    price: Double,
    totalReviews: Int,
    estimatedDuration: Int? = null,
    onActionClick: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FractionalRatingStars(rating = rating)
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "(${totalReviews.toDecimal()})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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
                text = stringResource(R.string.free),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = stringResource(R.string.can_learn_now),
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                    text = stringResource(R.string.learn_now),
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                    text = stringResource(R.string.buy_now_action),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StyledTag(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Surface(color = backgroundColor, shape = RoundedCornerShape(4.dp)) {
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
    starSize: androidx.compose.ui.unit.Dp = 14.dp,
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

