package com.example.khoitriso.ui.common

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.khoitriso.utils.YouTubeUtils

/**
 * Hiển thị video player với ExoPlayer
 */
@Composable
fun Media3AndroidView(player: ExoPlayer?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    if (player != null) {
        // Đảm bảo audio attributes được set để có âm thanh
        DisposableEffect(player) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .setUsage(C.USAGE_MEDIA)
                .build()
            player.setAudioAttributes(audioAttributes, true)
            
            onDispose { }
        }
        
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f) // Tỉ lệ video chuẩn
                .background(Color.Black),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = true // Hiện controller với thời gian
                    controllerShowTimeoutMs = 3000 // Hiện controller trong 3 giây
                    controllerAutoShow = true // Tự động hiện controller khi tap
                }
            },
            update = { playerView ->
                playerView.player = player
                // Đảm bảo controller hiển thị thời gian
                playerView.useController = true
            }
        )
    }
}

/**
 * Hiển thị YouTube video bằng WebView
 */
@Composable
fun YouTubePlayerView(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val embedUrl = YouTubeUtils.getEmbedUrl(videoUrl)
    
    if (embedUrl != null) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .background(Color.Black),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    loadUrl(embedUrl)
                }
            },
            update = { webView ->
                // Update if needed
            }
        )
    }
}

/**
 * Hiển thị video player - tự động chọn ExoPlayer hoặc YouTube WebView
 */
@Composable
fun VideoPlayerView(
    videoUrl: String?,
    player: ExoPlayer?,
    modifier: Modifier = Modifier
) {
    when {
        videoUrl != null && YouTubeUtils.isYouTubeUrl(videoUrl) -> {
            YouTubePlayerView(videoUrl = videoUrl, modifier = modifier)
        }
        player != null -> {
            Media3AndroidView(player = player, modifier = modifier)
        }
    }
}

