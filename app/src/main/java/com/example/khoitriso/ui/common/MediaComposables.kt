package com.example.khoitriso.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Hiển thị video player với ExoPlayer
 */
@Composable
fun Media3AndroidView(player: ExoPlayer?, modifier: Modifier = Modifier) {
    if (player != null) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f) // Tỉ lệ video chuẩn
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

