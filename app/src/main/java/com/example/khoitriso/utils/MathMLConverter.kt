package com.example.khoitriso.utils

import java.util.regex.Pattern

/**
 * Converter MathML sang LaTeX để dùng với Katex (nhẹ hơn WebView MathJS)
 * Sử dụng regex để convert nhanh hơn
 */
object MathMLConverter {
    
    /**
     * Convert toàn bộ HTML có MathML sang HTML có LaTeX
     * Pattern: Tìm <math>...</math> và convert sang LaTeX
     */
    fun convertHtmlWithMathML(html: String): String {
        if (!html.contains("<math")) return html
        
        var result = html
        val mathPattern = Pattern.compile("<math[^>]*>(.*?)</math>", Pattern.DOTALL)
        val matcher = mathPattern.matcher(result)
        
        val replacements = mutableListOf<Pair<String, String>>()
        
        while (matcher.find()) {
            val mathml = matcher.group(0)
            val content = matcher.group(1) ?: ""
            val latex = convertMathMLContentToLatex(content)
            if (latex.isNotEmpty()) {
                // Sử dụng \[ ... \] cho display math (JLatexMathPlugin nhận diện tốt hơn)
                replacements.add(mathml to "\\[$latex\\]")
            }
        }
        
        // Apply replacements từ cuối lên đầu để không ảnh hưởng index
        replacements.reversed().forEach { (mathml, latex) ->
            result = result.replace(mathml, latex)
        }
        
        return result
    }
    
    /**
     * Convert MathML content sang LaTeX (đơn giản hóa bằng regex)
     */
    private fun convertMathMLContentToLatex(content: String): String {
        var latex = content
        
        // Convert mfrac: <mfrac><mi>a</mi><mi>b</mi></mfrac> -> \frac{a}{b}
        val mfracPattern = Pattern.compile("<mfrac[^>]*>(.*?)</mfrac>", Pattern.DOTALL)
        var mfracMatcher = mfracPattern.matcher(latex)
        while (mfracMatcher.find()) {
            val inner = mfracMatcher.group(1) ?: ""
            val parts = splitMathMLChildren(inner)
            if (parts.size >= 2) {
                val num = convertMathMLContentToLatex(parts[0])
                val den = convertMathMLContentToLatex(parts[1])
                latex = latex.replace(mfracMatcher.group(0), "\\frac{$num}{$den}")
                mfracMatcher = mfracPattern.matcher(latex)
            }
        }
        
        // Convert msup: <msup><mi>x</mi><mn>2</mn></msup> -> x^{2}
        val msupPattern = Pattern.compile("<msup[^>]*>(.*?)</msup>", Pattern.DOTALL)
        var msupMatcher = msupPattern.matcher(latex)
        while (msupMatcher.find()) {
            val inner = msupMatcher.group(1) ?: ""
            val parts = splitMathMLChildren(inner)
            if (parts.size >= 2) {
                val base = convertMathMLContentToLatex(parts[0])
                val exp = convertMathMLContentToLatex(parts[1])
                latex = latex.replace(msupMatcher.group(0), "{$base}^{$exp}")
                msupMatcher = msupPattern.matcher(latex)
            }
        }
        
        // Convert msub: <msub><mi>x</mi><mn>1</mn></msub> -> x_{1}
        val msubPattern = Pattern.compile("<msub[^>]*>(.*?)</msub>", Pattern.DOTALL)
        var msubMatcher = msubPattern.matcher(latex)
        while (msubMatcher.find()) {
            val inner = msubMatcher.group(1) ?: ""
            val parts = splitMathMLChildren(inner)
            if (parts.size >= 2) {
                val base = convertMathMLContentToLatex(parts[0])
                val sub = convertMathMLContentToLatex(parts[1])
                latex = latex.replace(msubMatcher.group(0), "{$base}_{$sub}")
                msubMatcher = msubPattern.matcher(latex)
            }
        }
        
        // Convert msqrt: <msqrt><mi>x</mi></msqrt> -> \sqrt{x}
        val msqrtPattern = Pattern.compile("<msqrt[^>]*>(.*?)</msqrt>", Pattern.DOTALL)
        var msqrtMatcher = msqrtPattern.matcher(latex)
        while (msqrtMatcher.find()) {
            val inner = msqrtMatcher.group(1) ?: ""
            val content = convertMathMLContentToLatex(inner)
            latex = latex.replace(msqrtMatcher.group(0), "\\sqrt{$content}")
            msqrtMatcher = msqrtPattern.matcher(latex)
        }
        
        // Convert mroot: <mroot><mi>x</mi><mn>3</mn></mroot> -> \sqrt[3]{x}
        val mrootPattern = Pattern.compile("<mroot[^>]*>(.*?)</mroot>", Pattern.DOTALL)
        var mrootMatcher = mrootPattern.matcher(latex)
        while (mrootMatcher.find()) {
            val inner = mrootMatcher.group(1) ?: ""
            val parts = splitMathMLChildren(inner)
            if (parts.size >= 2) {
                val base = convertMathMLContentToLatex(parts[0])
                val root = convertMathMLContentToLatex(parts[1])
                latex = latex.replace(mrootMatcher.group(0), "\\sqrt[$root]{$base}")
                mrootMatcher = mrootPattern.matcher(latex)
            }
        }
        
        // Convert mi (identifier): <mi>x</mi> -> x
        latex = latex.replace(Regex("<mi[^>]*>(.*?)</mi>", setOf(RegexOption.DOT_MATCHES_ALL)), "$1")
        
        // Convert mn (number): <mn>2</mn> -> 2
        latex = latex.replace(Regex("<mn[^>]*>(.*?)</mn>", setOf(RegexOption.DOT_MATCHES_ALL)), "$1")
        
        // Convert mo (operator) với các ký tự đặc biệt
        latex = latex.replace(Regex("<mo[^>]*>(.*?)</mo>", setOf(RegexOption.DOT_MATCHES_ALL))) { matchResult ->
            val op = matchResult.groupValues[1]
            when (op.trim()) {
                "+" -> "+"
                "-" -> "-"
                "×", "*" -> "\\times"
                "÷", "/" -> "\\div"
                "=" -> "="
                "<" -> "<"
                ">" -> ">"
                "≤" -> "\\leq"
                "≥" -> "\\geq"
                "≠" -> "\\neq"
                "±" -> "\\pm"
                "∞" -> "\\infty"
                "∑" -> "\\sum"
                "∫" -> "\\int"
                "∂" -> "\\partial"
                "∇" -> "\\nabla"
                "∈" -> "\\in"
                "∉" -> "\\notin"
                "∀" -> "\\forall"
                "∃" -> "\\exists"
                "(" -> "("
                ")" -> ")"
                "[" -> "["
                "]" -> "]"
                "," -> ","
                "." -> "."
                ";" -> ";"
                ":" -> ":"
                else -> op
            }
        }
        
        // Remove mrow, mstyle tags (chỉ là wrapper)
        latex = latex.replace(Regex("<mrow[^>]*>(.*?)</mrow>", setOf(RegexOption.DOT_MATCHES_ALL)), "$1")
        latex = latex.replace(Regex("<mstyle[^>]*>(.*?)</mstyle>", setOf(RegexOption.DOT_MATCHES_ALL)), "$1")
        
        return latex.trim()
    }
    
    /**
     * Split MathML children (đơn giản hóa - tách theo tag đóng mở)
     */
    private fun splitMathMLChildren(content: String): List<String> {
        val parts = mutableListOf<String>()
        var depth = 0
        var start = 0
        var inTag = false
        
        for (i in content.indices) {
            when {
                content[i] == '<' && i + 1 < content.length && content[i + 1] != '/' -> {
                    if (depth == 0 && !inTag) {
                        if (i > start) {
                            parts.add(content.substring(start, i).trim())
                        }
                        start = i
                    }
                    depth++
                    inTag = true
                }
                content[i] == '>' -> {
                    inTag = false
                    if (depth == 1) {
                        // Tìm tag đóng tương ứng
                        val tagStart = content.indexOf('<', start)
                        val tagName = content.substring(tagStart + 1, i).split(' ', '>')[0]
                        val closeTag = "</$tagName>"
                        val closeIndex = content.indexOf(closeTag, i)
                        if (closeIndex != -1) {
                            parts.add(content.substring(start, closeIndex + closeTag.length))
                            start = closeIndex + closeTag.length
                            depth = 0
                        }
                    } else {
                        depth--
                    }
                }
            }
        }
        
        if (start < content.length) {
            val remaining = content.substring(start).trim()
            if (remaining.isNotEmpty()) {
                parts.add(remaining)
            }
        }
        
        return parts.filter { it.isNotEmpty() }
    }
}

