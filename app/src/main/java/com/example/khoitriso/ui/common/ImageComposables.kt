package com.example.khoitriso.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.khoitriso.R

/**
 * Hiển thị ảnh an toàn với placeholder và error handling
 */
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
        modifier = modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
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

