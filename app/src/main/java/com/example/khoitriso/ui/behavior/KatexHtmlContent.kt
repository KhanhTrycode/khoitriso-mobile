package com.example.khoitriso.ui.forum

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin

/**
 * Component hiển thị HTML với hỗ trợ MathML và LaTeX inline
 * - Nếu có MathML hoặc format [..]: dùng WebView với MathJax (hiển thị chính xác)
 * - Nếu không có: dùng Markwon với TextView (hiệu suất cao)
 * 
 * OPTIMIZATION:
 * - Caching WebView content để tránh re-render
 * - Lazy MathJax loading
 * - Disable scroll inside WebView để improve list performance
 */
@Composable
fun KatexHtmlContent(
    html: String,
    modifier: Modifier = Modifier,
    textColor: ComposeColor = LocalContentColor.current,
    textSizeSp: Float = 16f,
) {
    val context = LocalContext.current
    val processedHtml = remember(html) {
        preprocessMathContent(html)
    }

    val hasMathML = remember(processedHtml) {
        processedHtml.contains("<math", ignoreCase = true) ||
                processedHtml.contains("\\[") ||
                processedHtml.contains("\\(") ||
                processedHtml.contains("<img", ignoreCase = true)
    }

    if (hasMathML) {
        // Dùng WebView với MathJax cho MathML/LaTeX/hình ảnh
        MathMLWebView(
            html = processedHtml,
            modifier = modifier,
            textColor = textColor,
            textSizeSp = textSizeSp
        )
    } else {
        // Dùng Markwon với TextView cho HTML thường (hiệu suất cao)
        MarkwonTextView(
            html = processedHtml,
            modifier = modifier,
            textColor = textColor,
            textSizeSp = textSizeSp,
            context = context
        )
    }
}

/**
 * Xử lý format [y=g(x)] thành \(y=g(x)\) cho LaTeX inline
 * Chỉ xử lý nếu chưa có LaTeX delimiters hoặc MathML tags
 */
private fun preprocessMathContent(html: String): String {
    var result = html
    
    // Không xử lý nếu đã có LaTeX delimiters hoặc MathML
    if (result.contains("\\(") || result.contains("\\[") || result.contains("<math")) {
        return result
    }

    // Pattern để tìm [content] không phải là HTML tag hoặc attribute
    // Tránh match với [href="..."] hoặc [class="..."] hoặc [style="..."]
    // Chỉ match [content] không có dấu = hoặc " trong đó (tránh HTML attributes)
    val inlineMathPattern = Regex("""\[([^=\]]*[=+\-*/()<>0-9a-zA-Zα-ωΑ-Ω∞∑∫∂∇∈∉∀∃≤≥≠±×÷\s]+[^=\]]*)\]""")

    result = inlineMathPattern.replace(result) { matchResult ->
        val fullMatch = matchResult.value
        val content = matchResult.groupValues[1]
        
        // Kiểm tra xem có phải HTML attribute không (có dấu = hoặc " trong đó)
        if (content.contains("=") || content.contains("\"")) {
            return@replace fullMatch // Giữ nguyên nếu là HTML attribute
        }
        
        // Chỉ convert nếu có ký tự toán học và không phải chỉ là text thường
        if (content.contains(Regex("""[=+\-*/()<>0-9α-ωΑ-Ω∞∑∫∂∇∈∉∀∃≤≥≠±×÷]"""))) {
            // Không escape, giữ nguyên content để MathJax xử lý
            // Chỉ wrap bằng \(...\) để MathJax nhận diện là inline math
            "\\(${content}\\)"
        } else {
            fullMatch // Giữ nguyên nếu không phải công thức
        }
    }

    return result
}

/**
 * Render HTML thường bằng Markwon (không có MathML)
 */
@Composable
private fun MarkwonTextView(
    html: String,
    modifier: Modifier,
    textColor: ComposeColor,
    textSizeSp: Float,
    context: Context,
) {
    val markwon = remember(context) {
        createMarkwon(context, textSizeSp)
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
                setTextIsSelectable(true)
            }
        },
        update = { textView ->
            textView.setTextColor(textColor.toArgb())
            markwon.setMarkdown(textView, html)
        }
    )
}

/**
 * Render MathML bằng WebView với MathJax (hiển thị chính xác)
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun MathMLWebView(
    html: String,
    modifier: Modifier,
    textColor: ComposeColor,
    textSizeSp: Float,
) {
    val finalContent = remember(html, textColor, textSizeSp) {
        generateMathMLHtml(html, textColor, textSizeSp)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    // OPTIMIZATION: Aggressive caching để tránh reload
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    useWideViewPort = false
                    loadWithOverviewMode = false
                    textZoom = (textSizeSp * 6.25).toInt()
                    builtInZoomControls = false
                    displayZoomControls = false
                }
                setBackgroundColor(Color.TRANSPARENT)
                // Disable scrolling inside WebView to improve list scroll performance
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
            }
        },
        update = { webView ->
            // Chỉ load lại nếu content thực sự thay đổi (caching)
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
        }
    )
}

/**
 * Tạo HTML với MathJax để render MathML
 */
private fun generateMathMLHtml(
    content: String,
    textColor: ComposeColor,
    textSizeSp: Float,
): String {
    val colorHex = String.format("#%06X", 0xFFFFFF and textColor.toArgb())

    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <script>
                MathJax = {
                    tex: {
                        inlineMath: [['\\(', '\\)']],
                        displayMath: [['\\[', '\\]']],
                        processEscapes: true,
                        processEnvironments: true
                    },
                    mml: {
                        // MathML configuration
                        extensions: ['mml3.js'],
                        forceReparse: false
                    },
                    svg: {
                        fontCache: 'global',
                        scale: 1.0
                    },
                    chtml: {
                        scale: 1.0,
                        matchFontHeight: true
                    },
                    options: {
                        skipHtmlTags: ['script', 'noscript', 'style', 'textarea', 'pre'],
                        ignoreHtmlClass: 'tex2jax_ignore',
                        processHtmlClass: 'tex2jax_process',
                        // OPTIMIZATION: Disable menu và loading messages
                        enableMenu: false,
                        menuOptions: {
                            settings: {
                                zoom: 'NoZoom'
                            }
                        },
                        renderActions: {
                            addMenu: [],
                            checkLoading: []
                        }
                    },
                    startup: {
                        // OPTIMIZATION: Lazy load và render nhanh hơn
                        ready: () => {
                            MathJax.startup.defaultReady();
                        }
                    }
                };
            </script>
            <script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script>
            <script id="MathJax-script" async src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"></script>
            <style>
                body {
                    margin: 0;
                    padding: 8px;
                    font-family: 'Roboto', sans-serif;
                    font-size: ${textSizeSp}px;
                    line-height: 1.6;
                    color: $colorHex;
                    word-wrap: break-word;
                }
                math {
                    font-size: 1em;
                    display: inline-block;
                    vertical-align: middle;
                }
                img {
                    max-width: 100%;
                    height: auto;
                }
                table {
                    border-collapse: collapse;
                    width: 100%;
                    margin: 8px 0;
                }
                table, th, td {
                    border: 1px solid #ddd;
                }
                th, td {
                    padding: 8px;
                    text-align: left;
                }
            </style>
        </head>
        <body>
            $content
        </body>
        </html>
    """.trimIndent()
}

/**
 * Hàm cấu hình Markwon Singleton
 */
private fun createMarkwon(context: Context, textSizeSp: Float): Markwon {
    return Markwon.builder(context)
        .usePlugin(HtmlPlugin.create()) // Hỗ trợ thẻ HTML (<b>, <i>, <p>, <table>...)
        .usePlugin(
            ImagesPlugin.create() // Hỗ trợ hiển thị hình ảnh từ HTML
        )
        .usePlugin(
            JLatexMathPlugin.create(
                textSizeSp * 30, // Kích thước công thức toán (tinh chỉnh số này cho vừa mắt)
                { builder ->
                    // Cấu hình background cho công thức (thường để trong suốt)
                    builder.inlinesEnabled(true)
                    // Hỗ trợ nhiều định dạng LaTeX: \(...\), \[...\], $$...$$, $...$
                    builder.blocksEnabled(true)
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