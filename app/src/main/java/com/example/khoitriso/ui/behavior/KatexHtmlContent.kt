package com.example.khoitriso.ui.forum

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin

@Composable
fun KatexHtmlContent(
    html: String,
    modifier: Modifier = Modifier,
    textColor: Color = LocalContentColor.current, // Lấy màu chữ hiện tại của Theme
    textSizeSp: Float = 16f // Cỡ chữ mặc định
) {
    val context = LocalContext.current

    // 1. Khởi tạo Markwon (Nên nhớ instance này để tránh tạo lại nhiều lần gây nặng)
    val markwon = remember(context) {
        createMarkwon(context, textSizeSp)
    }

    // 2. Render Native TextView
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                // Cấu hình TextView cơ bản
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
                // Cho phép text có thể chọn/copy được (tuỳ chọn)
                setTextIsSelectable(true)
            }
        },
        update = { textView ->
            // Cập nhật màu sắc theo Theme Compose
            textView.setTextColor(textColor.toArgb())

            // Render nội dung: Markwon biến HTML/LaTeX thành Spannable
            markwon.setMarkdown(textView, html)
        }
    )
}

/**
 * Hàm cấu hình Markwon Singleton
 */
private fun createMarkwon(context: Context, textSizeSp: Float): Markwon {
    return Markwon.builder(context)
        .usePlugin(HtmlPlugin.create()) // Hỗ trợ thẻ HTML (<b>, <i>, <p>...)
        .usePlugin(
            JLatexMathPlugin.create(
                textSizeSp * 30, // Kích thước công thức toán (tinh chỉnh số này cho vừa mắt)
                { builder ->
                    // Cấu hình background cho công thức (thường để trong suốt)
                    builder.inlinesEnabled(true)
                }
            )
        )
        .usePlugin(MarkwonInlineParserPlugin.create()) // Parse markdown nhanh hơn
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                // Cấu hình thêm nếu cần (ví dụ font, link handler)
            }
        })
        .build()
}