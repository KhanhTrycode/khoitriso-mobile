package com.example.khoitriso.utils

import java.text.NumberFormat
import java.util.Locale

object Converter{


}

fun Int.toDecimal(): String {
    return "%,d".format(this)
}
fun Double.toVND(): String {
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(this)
    return "${formatted}₫"
}

fun Int.toFileSize(): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (kotlin.math.log10(this.toDouble()) / kotlin.math.log10(1024.0)).toInt()
    return String.format("%.1f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}