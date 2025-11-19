package com.example.khoitriso.utils

import java.text.NumberFormat
import java.util.Locale

object Converter{


}

fun Int.toDecimal(): String {
    return "%,d".format(this)
}
fun Int.toVND(): String {
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(this)
    return "${formatted}₫"
}