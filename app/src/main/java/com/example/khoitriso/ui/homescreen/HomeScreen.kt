package com.example.khoitriso.ui.homescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.ui.behavior.SafeImage
import com.example.khoitriso.utils.UiState


@SuppressLint("FrequentlyChangingValue")
@Composable
fun HomeScreen(
    lazyListState: LazyListState,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val booksState by viewModel.books.collectAsState()
    val coursesState by viewModel.courses.collectAsState()
    val categoriesState by viewModel.categories.collectAsState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize()
    ) {
        // ===== Try Free Course =====
        item { SectionTitle("Try Free Course") }
            item {
                FreeCourse(coursesState)

        }

        // ===== Categories =====
        item { SectionTitle("Categories") }
        item {
            CategoryList(categoriesState)
        }

        // ===== Trending Courses =====
        item { SectionTitle("Trending Courses") }
        item {
            TrendingCourse(coursesState)
        }
    }

}

@Composable
fun CategoryList(categoriesState: UiState<List<Category>>) {
    when (categoriesState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Error -> Text(categoriesState.message)
        is UiState.Success -> {
            val categories = categoriesState.data
            RowCategoryCard(categories)
        }
    }
}

@Composable
fun TrendingCourse(coursesState: UiState<List<Course>>) {
    when (coursesState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Error -> Text(coursesState.message)
        is UiState.Success -> {
            val courses = coursesState.data
            RowCourseCard(courses)
        }
    }
}

@Composable
fun FreeCourse(coursesState: UiState<List<Course>>) {

    when (coursesState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Error -> Text(coursesState.message)
        is UiState.Success -> {
            val courses = coursesState.data
            if (courses.isNotEmpty()) {
                FreeCourseCard(
                    thumbnailUrl = R.drawable.ic_launcher_background,
                    destination = "Try this free",
                    course = courses[0]
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    )
}

@Composable
fun FreeCourseCard(
    thumbnailUrl: Int,
    destination: String,
    course: Course,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    )
    {
        Image(
            painter = painterResource(thumbnailUrl),
            contentDescription = "Thumbnail",
            modifier = Modifier
                .fillMaxWidth()
        )

        Text(destination, style = MaterialTheme.typography.bodyMedium, modifier = Modifier)
        CourseCard(course)
    }
}

@Composable
fun CourseCard(course: Course, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
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
                maxLines = 2
            )

            Text(
                text = "By ${course.instructor.name}",
                style = MaterialTheme.typography.labelMedium,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${course.rating}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = if (course.isFree) "Free" else "$${course.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (course.isFree) Color(0xFF4CAF50) else Color.Black
            )
        }
    }
}


@Composable
fun CategoryCard(name: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(20.dp))

    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
}


@Composable
fun RowCategoryCard(categoryList: List<Category>) {
    val chunkedList = categoryList.chunked(2) // chia mỗi 2 category thành 1 column

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        items(categoryList.size) { index ->
            CategoryCard(name = categoryList[index].name)

        }
    }
}

@Composable
fun RowCourseCard(courseList: List<Course>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(courseList.size) { index ->
            CourseCard(courseList[index])
        }
    }
}
