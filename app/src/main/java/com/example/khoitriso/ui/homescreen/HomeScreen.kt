package com.example.khoitriso.ui.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.khoitriso.R
import com.example.khoitriso.ui.theme.KhoiTriSoTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavDestination
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.test.MockData
import java.nio.file.WatchEvent

@Composable
fun HeaderScreen(
    avatarUrl: Int,
    displayName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.4f)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Image(
            painter = painterResource(id = avatarUrl),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Display Name
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)    // <-- đẩy tên chiếm không gian còn lại
        )

        IconButton(modifier = Modifier, onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Cart",
                tint = Color.White

            )
        }
        // Notification button
        IconButton(modifier = Modifier, onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notification",
                tint = Color.White
            )
        }

    }
}

//@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    KhoiTriSoTheme {
        val bottomNavItems = listOf("Home", "Search", "Profile") // sample

        Scaffold(
            topBar = {
                HeaderScreen(
                    avatarUrl = R.drawable.ic_launcher_background,
                    displayName = "KhoiTriSo"
                )
            },
            bottomBar = {
                BottomNavigationBar(items = bottomNavItems)
            }
        ) { innerPadding ->
            // Content scrollable
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F0F0))
            ) {
                item {
                    SectionTitle("Try Free Course")
                    FreeCourseCard(
                        R.drawable.ic_launcher_background,
                        "Try this free",
                        MockData.mockCourses[0]
                    )
                }

                item {
                    SectionTitle("Recommended")
                    RowCourseCard(MockData.recommendedCourses)
                }

                item {
                    SectionTitle("Categories")
                    RowCategoryCard(MockData.categories)
                }


                item {
                    SectionTitle("Trending Courses")
                    MockData.trendingCourses.forEach { course ->
                        CourseCard(course)
                    }
                }

            }
        }
    }
}


@Composable
fun HomeScreen() {

}

@Composable
fun BottomNavigationBar(items: List<String>) {
    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = item) },
                label = { Text(item) },
                selected = false,
                onClick = {}
            )
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
    modifier: Modifier = Modifier
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
        Image(
            painter = painterResource(id = course.Thumbnail),
            contentDescription = course.Title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // cố định chiều cao
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentScale = ContentScale.Crop
        )

        // Info below image
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = course.Title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )

            Text(
                text = "By ${course.Instructor.Name}",
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
                    text = "${course.Rating}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = if (course.IsFree) "Free" else "$${course.Price}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (course.IsFree) Color(0xFF4CAF50) else Color.Black
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
                CategoryCard(name = categoryList[index].Name)

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
