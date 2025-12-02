package com.example.khoitriso.ui.forum

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HtmlContent(html: String, modifier: Modifier = Modifier) {
    // 1. Tạo khung HTML hoàn chỉnh (chỉ làm 1 lần khi html thay đổi)
    val finalContent = remember(html) {
        generateMathmlHtml(html)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    useWideViewPort = false
                    loadWithOverviewMode = false
                    textZoom = 100 // Cố định cỡ chữ
                }

                setBackgroundColor(Color.TRANSPARENT)
            }
        },
        update = { webView ->
            if (webView.tag != finalContent) {
                webView.loadDataWithBaseURL(
                    "about:blank",
                    finalContent,
                    "text/html",
                    "UTF-8",
                    null
                )
                webView.tag = finalContent
            }
        },
        onRelease = { webView ->
            // Dọn dẹp bộ nhớ
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.destroy()
        }
    )
}

/**
 * Hàm sinh HTML hỗ trợ MathML
 */
private fun generateMathmlHtml(content: String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    font-family: 'Roboto', sans-serif;
                    font-size: 15px; 
                    line-height: 1.5;
                    color: #1f1f1f;
                }
                /* CSS để MathML hiển thị đẹp */
                math { font-size: 1.1em; }
                img { max-width: 100%; height: auto; }
            </style>
            
            <script src="file:///android_asset/mathjax/mml-chtml.js"></script>
        </head>
        <body>
            $content
        </body>
        </html>
    """.trimIndent()
}

@Composable
fun OptimizedHtmlContent(html: String, modifier: Modifier = Modifier) {
    key(html) {
        HtmlContent(html, modifier)
    }
}