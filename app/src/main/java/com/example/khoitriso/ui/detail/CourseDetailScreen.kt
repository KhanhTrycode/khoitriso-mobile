package com.example.khoitriso.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.Instructor
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.ui.behavior.ActionButtons
import com.example.khoitriso.ui.behavior.CreateBy
import com.example.khoitriso.ui.behavior.DetailHeader
import com.example.khoitriso.ui.behavior.FractionalRatingStars
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.toVND

@Composable
fun CourseDetailScreen(
    navController: NavController,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val courseState by viewModel.course.collectAsState()
    val player by viewModel.playerState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initializePlayer(
            context,
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.releasePlayer()
        }
    }
    Scaffold(
        topBar = {
            DetailHeader(onBack = {
                navController.popBackStack()
            })
        },
        bottomBar = {
            if (courseState is UiState.Success) {
                ActionButtons(
                    price = (courseState as UiState.Success<CourseDetail>).data.price,
                    onBuy = { viewModel.buyNow() },
                    onCart = { viewModel.addToCart() }
                )
            }
        }
    ) {
        when (courseState) {
            is UiState.Error -> {}
            is UiState.Loading -> {}
            is UiState.Success<CourseDetail> -> {
                val course = (courseState as UiState.Success<CourseDetail>).data
                DetailContent(
                    course,
                    player,
                    modifier = Modifier.padding(it)
                )
            }
        }

    }
}




@Composable
private fun DetailContent(
    course: CourseDetail,
    player: ExoPlayer?,
    modifier: Modifier = Modifier,
) {

    // Chuyển từ Column sang LazyColumn
    LazyColumn(
        modifier = modifier,
        // Thêm contentPadding để nội dung không bị dính sát vào cạnh màn hình
        contentPadding = PaddingValues(16.dp)
    ) {
        // Mỗi phần tử giao diện giờ đây là một `item` trong LazyColumn

        // 1. Video Player
        item {
            Media3AndroidView(player)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2. Thông tin chính của khóa học
        item {
            CourseInfo(course)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Mục "What you'll learn"
        item {
            InfoSection(
                title = "What you'll learn",
                icon = Icons.Default.CheckCircle,
                items = course.whatYouWillLearn
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 4. Mục "Requirements"
        item {
            InfoSection(
                title = "Requirements",
                icon = Icons.Default.CheckCircle,
                items = course.requirements
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 5. Mục "Course Lessons" (Thêm vào)
        item {
            Text(
                text = "Course Lessons",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(course.lessons) { lesson ->
            LessonRow(lesson = lesson, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}


@Composable
private fun LessonRow(
    lesson: Lesson,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (lesson.isFree) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.List,
            contentDescription = if (lesson.isFree) "Free lesson" else "Paid lesson",
            tint = if (lesson.isFree) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Duration: ${lesson.videoDuration / 60} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@Composable
fun Media3AndroidView(player: ExoPlayer?, modifier: Modifier = Modifier) {
    if (player != null) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                }
            },
            update = { playerView ->
                playerView.player = player
            }
        )
    }
}

@Composable
fun CourseInfo(course: CourseDetail) {
    Text(
        text = course.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))

    // Rating row
    FractionalRatingStars(rating = course.rating)

    Text(
        text = "(${course.totalReviews} reviews) ${course.totalStudents} students",
        style = MaterialTheme.typography.labelMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(text = course.description, style = MaterialTheme.typography.bodyMedium)

    Spacer(modifier = Modifier.height(12.dp))

    CreateBy(
        fullName = course.instructor.name,
        avatar = course.instructor.avatar
    )

    Spacer(modifier = Modifier.height(16.dp))

}

// Composable tái sử dụng để hiển thị các mục thông tin
@Composable
private fun InfoSection(title: String, icon: ImageVector, items: List<String>) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        items.forEach { item ->
            InfoRow(text = item, icon = icon)
        }
    }
}

// Một hàng trong mục thông tin
@Composable
private fun InfoRow(text: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}






