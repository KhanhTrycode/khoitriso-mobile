package com.example.khoitriso.utils

import android.util.Log
import com.example.khoitriso.BuildConfig
import com.example.khoitriso.R

object Constants {

    val BASE_API_URL = BuildConfig.API_KEY
    val GOOGLE_CLIENT_ID = BuildConfig.GOOGLE_CLIENT_ID
    val HEADER_HEIGHT: Int = 60
    val NAV_HEIGHT: Int = 60
    val THRESHOLD_SCROLL: Int = 120
    val BOOK_DEFAULT_COVER_IMAGE = R.drawable.book_placeholder
    val DEBUG = true
}

fun debug(message: String, tag: String = "MyDebugLog") {
    if (Constants.DEBUG) {
        Log.d(tag, message)
    }
}

object ItemType {
    val Book = 0
    val Course = 1
}

object QuestionType {
    val MultipleChoice = 0
    val TrueFalse = 1
    val ShortAnswer = 2
    val GroupType = 3
}

object LevelType {
    val NhanBiet = 0
    val ThongHieu = 1
    val VanDungThap= 2
    val VanDungCao= 3
}

object SearchType {
    val ALL = 0
    val COURSE = 1
    val BOOK = 2
}