package com.example.khoitriso.ui.behavior

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.khoitriso.R

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
