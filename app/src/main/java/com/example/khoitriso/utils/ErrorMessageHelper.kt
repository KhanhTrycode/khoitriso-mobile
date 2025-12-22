package com.example.khoitriso.utils

import android.content.Context
import com.example.khoitriso.R

object ErrorMessageHelper {
    fun getErrorMessage(context: Context, errorType: ErrorType, vararg args: Any): String {
        return when (errorType) {
            ErrorType.RESPONSE_BODY_NULL -> context.getString(R.string.error_response_body_null)
            ErrorType.RESPONSE_RESULT_NULL -> context.getString(R.string.error_response_result_null)
            ErrorType.API_ERROR -> context.getString(R.string.error_api_error, args[0] as Int)
            ErrorType.UNKNOWN -> context.getString(R.string.error_unknown)
            ErrorType.NETWORK -> context.getString(R.string.error_network)
            ErrorType.ITEM_ALREADY_IN_CART -> context.getString(R.string.error_item_already_in_cart)
            ErrorType.QUESTION_NOT_FOUND -> context.getString(R.string.error_question_not_found)
            ErrorType.FAILED_TO_CREATE_QUESTION -> context.getString(R.string.error_failed_to_create_question)
            ErrorType.FAILED_TO_UPDATE_QUESTION -> context.getString(R.string.error_failed_to_update_question)
            ErrorType.FAILED_TO_CREATE_ANSWER -> context.getString(R.string.error_failed_to_create_answer)
            ErrorType.FAILED_TO_UPDATE_ANSWER -> context.getString(R.string.error_failed_to_update_answer)
            ErrorType.FAILED_TO_CREATE_COMMENT -> context.getString(R.string.error_failed_to_create_comment)
            ErrorType.FAILED_TO_UPDATE_COMMENT -> context.getString(R.string.error_failed_to_update_comment)
            ErrorType.FAILED_TO_VOTE -> context.getString(R.string.error_failed_to_vote)
            ErrorType.FAILED_TO_GET_STATS -> context.getString(R.string.error_failed_to_get_stats)
            ErrorType.RESPONSE_DATA_NULL -> context.getString(R.string.error_response_data_null)
            ErrorType.NO_USER_DATA -> context.getString(R.string.error_no_user_data)
            ErrorType.COURSE_NOT_FOUND -> context.getString(R.string.error_course_not_found)
            ErrorType.BOOK_NOT_FOUND -> context.getString(R.string.error_book_not_found)
        }
    }
}

enum class ErrorType {
    RESPONSE_BODY_NULL,
    RESPONSE_RESULT_NULL,
    API_ERROR,
    UNKNOWN,
    NETWORK,
    ITEM_ALREADY_IN_CART,
    QUESTION_NOT_FOUND,
    FAILED_TO_CREATE_QUESTION,
    FAILED_TO_UPDATE_QUESTION,
    FAILED_TO_CREATE_ANSWER,
    FAILED_TO_UPDATE_ANSWER,
    FAILED_TO_CREATE_COMMENT,
    FAILED_TO_UPDATE_COMMENT,
    FAILED_TO_VOTE,
    FAILED_TO_GET_STATS,
    RESPONSE_DATA_NULL,
    NO_USER_DATA,
    COURSE_NOT_FOUND,
    BOOK_NOT_FOUND
}

