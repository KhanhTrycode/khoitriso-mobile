package com.example.khoitriso.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.khoitriso.R
import com.example.khoitriso.ui.behavior.FractionalRatingStars
import com.example.khoitriso.utils.UiState

@Composable
fun CourseDetailScreen(
    onBack: () -> Unit = {},
    onBuyNow: (Course) -> Unit = {},
    onAddToCart: (Course) -> Unit = {},
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

    Column {
        DetailHeader(onBack = onBack)

        when (courseState) {
            is UiState.Error -> {}
            is UiState.Loading -> {}
            is UiState.Success<Course> -> {
                val course = (courseState as UiState.Success<Course>).data
                DetailContent(
                    course = course,
                    player = player,
                    onBuyNow = { onBuyNow(course) },
                    onAddToCart = { onAddToCart(course) }
                )
            }
        }
    }
}


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
        modifier = modifier
    )
}

@Composable
private fun DetailContent(
    course: Course,
    player: ExoPlayer?,
    onBuyNow: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {

        Media3AndroidView(player)



        Spacer(modifier = Modifier.height(20.dp))

        // Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onBuyNow,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Buy now")
            }

            OutlinedButton(
                onClick = onAddToCart,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Add to cart")
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Add to cart")
            }
        }
    }
}

@Composable
private fun InstructorRow(instructor: Instructor, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        // avatar may be an Int resource in mock data
        if (instructor.avatar == "") {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Instructor avatar",
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = instructor.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (instructor.bio != "") Text(
                text = instructor.bio.toString(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun Media3AndroidView(player: ExoPlayer?,modifier: Modifier = Modifier) {
    if (player != null) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f),
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
fun CourseInfo(course: Course){
    Text(text = course.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))

    // Rating row
    FractionalRatingStars(rating = course.rating)

    Text(text = "(${course.totalReviews} reviews) ${course.totalStudents} students", style = MaterialTheme.typography.labelMedium)

    Spacer(modifier = Modifier.height(8.dp))

    // Instructor
    InstructorRow(instructor = course.instructor)

    Spacer(modifier = Modifier.height(12.dp))

    Text(text = course.description, style = MaterialTheme.typography.bodyMedium)

    Spacer(modifier = Modifier.height(16.dp))

    // Price
    Text(
        text = "Price: ${course.price}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}
