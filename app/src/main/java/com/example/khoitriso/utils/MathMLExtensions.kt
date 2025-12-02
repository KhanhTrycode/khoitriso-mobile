package com.example.khoitriso.utils

import com.example.khoitriso.domain.models.Option
import com.example.khoitriso.domain.models.Question

/**
 * Extension functions để convert MathML sang LaTeX cho Question và Option
 */

/**
 * Convert questionContent từ MathML sang LaTeX
 */
fun Question.convertMathMLToLatex(): Question {
    val convertedContent = MathMLConverter.convertHtmlWithMathML(this.questionContent)
    return this.copy(questionContent = convertedContent)
}

/**
 * Convert optionText từ MathML sang LaTeX
 */
fun Option.convertMathMLToLatex(): Option {
    val convertedText = MathMLConverter.convertHtmlWithMathML(this.optionText)
    return this.copy(optionText = convertedText)
}

/**
 * Convert list questions
 */
@JvmName("convertQuestionsToLatex") // Unique name for the JVM
fun List<Question>.convertMathMLToLatex(): List<Question> {
    return this.map { it.convertMathMLToLatex() }
}

/**
 * Convert list options
 */
@JvmName("convertOptionsToLatex") // Unique name for the JVM
fun List<Option>.convertMathMLToLatex(): List<Option> {
    return this.map { it.convertMathMLToLatex() }
}