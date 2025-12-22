package com.example.khoitriso.ui.homescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.khoitriso.R
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.ui.common.ObserverAsEvent
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.Instructor
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.ui.common.RowBookCard
import com.example.khoitriso.ui.common.RowCourseCard
import com.example.khoitriso.ui.common.SafeImage
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.SearchType
import com.example.khoitriso.utils.UiState


@SuppressLint("FrequentlyChangingValue")
@Composable
fun HomeScreen(
    lazyListState: LazyListState,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    paddingValues: PaddingValues,
) {
    val recommendedBooks by viewModel.recommendedBooks.collectAsState()
    val trendingCourses by viewModel.trendingCourses.collectAsState()
    val categoriesState by viewModel.categories.collectAsState()
    val tryCourse by viewModel.tryCourses.collectAsState()
    val myCourse by viewModel.myCourses.collectAsState()
    val user by viewModel.user.collectAsState()
    val snackBarState = remember { SnackbarHostState() }

    // Observe navigation events for token expiration
    ObserverAsEvent(viewModel.navigationEvents) { event ->
        when (event) {
            is com.example.khoitriso.ui.behavior.NavigationEvent.NavigateToLogin -> {
                navController.navigate(com.example.khoitriso.utils.NavRoute.LOGIN) {
                    popUpTo(0) { inclusive = true } // Clear entire back stack
                }
            }
        }
    }

    ObserverAsEvent(viewModel.events) { event ->
        when (event) {
            is com.example.khoitriso.utils.UiEvent.ShowSnackbar -> {
                snackBarState.showSnackbar(event.message)
            }
        }
    }

    androidx.compose.material3.Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarState) }
    ) { padding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                HomeHeaderSection(
                    username = user?.fullName ?: "",
                )
            }

            when (val state = tryCourse) {
                is UiState.Success -> {
                    item {
                        PromoBanner(
                            course = state.data,
                            onClick = { }
                        )
                    }
                }

                else -> {}
            }

            item {
                when (val state = myCourse) {
                    is UiState.Success -> {
                        ContinueLearningSection(state.data, onCardClick = {})
                    }

                    else -> {}
                }
            }

            // 4. Categories (Chip style hiện đại hơn)
            item {
                SectionTitle(
                    stringResource(R.string.categories),
                    "",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryList(categoriesState, navController)
            }

            // 5. Recommended Books
            item {
                SectionTitle(
                    stringResource(R.string.recommended_books),
                    stringResource(R.string.see_more),
                    onClickAction = {
                        navController.navigate(NavRoute.NavSearchTab(SearchType.BOOK))
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                RecommendedBook(viewModel,recommendedBooks, navController)
            }

            // 6. Trending Courses
            item {
                SectionTitle(
                    stringResource(R.string.trending_courses),
                    stringResource(R.string.see_more),
                    onClickAction = {
                        navController.navigate(NavRoute.NavSearchTab(SearchType.COURSE))
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TrendingCourse(viewModel,trendingCourses, navController)
            }
        }
    }
}

@Composable
fun PromoBanner(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Gọn hơn
            .padding(horizontal = 20.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.course_test),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Lớp phủ tối màu gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(24.dp)
                    .fillMaxWidth(0.7f) // Chỉ chiếm 70% chiều ngang
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        stringResource(R.string.hot_deal),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.promo_banner_text),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun ContinueLearningSection(
    courses: List<MyCourse>,
    onCardClick: (String) -> Unit,
) {
    if (courses.isNotEmpty()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            PaddingValues(horizontal = 20.dp).let {
                Text(
                    text = stringResource(R.string.continue_learning),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(courses) { course ->
                    ContinueLearningCard(course = course)
                }
            }
        }
    }
}

@Composable
fun ContinueLearningCard(course: MyCourse) {
    val progress = course.progressPercentage

    Card(
        modifier = Modifier
            .width(260.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            SafeImage(
                url = course.course.thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = course.course.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = (progress / 100f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "${progress.toInt()}%",
                        modifier = Modifier.padding(start = 12.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

    }
}

@Composable
fun HomeHeaderSection(
    username: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // Greeting Text
        Text(
            text = stringResource(R.string.good_morning, username),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = stringResource(R.string.what_skill_today),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

    }
}

@Composable
fun SectionTitle(
    title: String, actionText: String, onClickAction: () -> Unit = {},
    modifier:
    Modifier
    = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        )

        if (actionText.isNotEmpty()) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,

                    ),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        onClickAction()
                    }
            )
        }
    }
}

@Composable
fun FreeCourseCard(
    thumbnailUrl: Int,
    destination: String,
    course: Course,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                clip = true
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Thumbnail với overlay gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(thumbnailUrl),
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay mạnh hơn
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0.4f
                            )
                        )
                )

                // Content trên thumbnail
                ContentThumbnail(
                    destination, course.rating, course.estimatedDuration, course.level,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                )
            }

            // Bottom content với background đẹp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Instructor info
                    FreeCourseInstructor(
                        course.instructor,
                        course.title,
                        Modifier.weight(1f)
                    )

                    // CTA Button
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .clickable
                            { navController.navigate(NavRoute.NavCourseDetail(course.id)) }

                    ) {
                        Text(
                            text = stringResource(R.string.start_now),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContentThumbnail(
    destination: String, rating: Float, estimatedDuration: Int, level: Int,
    modifier:
    Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.free_badge),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = destination,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating và info
        FreeCourseRating(rating, estimatedDuration, level)
    }
}

@Composable
fun FreeCourseInstructor(instructor: Instructor, title: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = stringResource(R.string.by_instructor, instructor.name),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
fun FreeCourseRating(rating: Float, estimatedDuration: Int, level: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Rating",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$estimatedDuration ' • $level",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        )
    }
}

@Composable
fun FreeCourse(coursesState: UiState<Course>, navController: NavController) {
    when (coursesState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = coursesState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is UiState.Success -> {
            FreeCourseCard(
                thumbnailUrl = R.drawable.course_test,
                destination = stringResource(R.string.try_course_string),
                course = coursesState.data,
                navController = navController
            )
        }
    }
}


@Composable
fun CategoryCard(name: String, onClick: () -> Unit = {}) {

    Card(
        modifier = Modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun RowCategoryCard(categoryList: List<Category>, navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categoryList) { category ->
            CategoryCard(
                name = category.name,
                onClick = {
                    // Navigate đến search screen với query là category name
                    navController.navigate("categoryItems/${category.id}/${category.name}")
                }
            )
        }
    }
}

@Composable
fun CategoryList(categoriesState: UiState<List<Category>>, navController: NavController) {
    when (categoriesState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = categoriesState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is UiState.Success -> {
            val categories = categoriesState.data
            RowCategoryCard(categories, navController)
        }
    }
}

@Composable
fun TrendingCourse(viewModel: HomeViewModel,coursesState: UiState<List<Course>>, navController: NavController) {
    when (coursesState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = coursesState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is UiState.Success -> {
            val courses = coursesState.data
            RowCourseCard(
                courses,
                navController,
                onAddToCart = { itemId, itemType -> viewModel.addToCart(itemId, itemType) }
            )
        }
    }
}

@Composable
fun RecommendedBook(viewModel: HomeViewModel,booksState: UiState<List<Book>>, navController: NavController) {
    when (booksState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = booksState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is UiState.Success -> {
            val books = booksState.data
            RowBookCard(
                books,
                navController,
                onAddToCart = { itemId, itemType -> viewModel.addToCart(itemId, itemType) }
            )
        }
    }
}
