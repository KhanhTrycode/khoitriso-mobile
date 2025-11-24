package com.example.khoitriso.ui.forum

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Composable để render HTML content với MathJax support mặc định
 * Luôn load MathJax để render MathML nếu có
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HtmlContent(html: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // Auto typeset MathJax sau khi page load xong
                        view?.evaluateJavascript("""
                            (function() {
                                if (window.MathJax && window.MathJax.typesetPromise) {
                                    window.MathJax.typesetPromise().catch(function(err) {
                                        console.warn('MathJax typeset error:', err);
                                    });
                                }
                            })();
                        """.trimIndent(), null)
                    }
                }
                
                // Bật JavaScript để MathJax hoạt động
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                
                // Luôn wrap HTML với MathJax script
                val finalHtml = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=yes">
                        <meta charset="UTF-8">
                        <style>
                            body {
                                margin: 0;
                                padding: 8px;
                                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                                font-size: 14px;
                                line-height: 1.5;
                                color: #333;
                            }
                            math {
                                display: inline;
                            }
                            math[display="block"] {
                                display: block;
                                margin: 10px 0;
                                text-align: center;
                            }
                            img {
                                max-width: 100%;
                                height: auto;
                                display: block;
                                margin: 8px 0;
                            }
                        </style>
                        <script>
                            window.MathJax = {
                                loader: { load: ['input/mml', 'output/chtml'] },
                                options: {
                                    renderActions: { addMenu: [0, '', ''] },
                                    skipHtmlTags: ['script', 'noscript', 'style', 'textarea', 'pre', 'code']
                                }
                            };
                        </script>
                        <script src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/mml-chtml.js" async></script>
                    </head>
                    <body>
                        $html
                    </body>
                    </html>
                    """
                
                loadDataWithBaseURL(null, finalHtml, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier
    )
}

