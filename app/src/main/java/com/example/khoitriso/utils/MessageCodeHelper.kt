package com.example.khoitriso.utils

import android.content.Context
import com.example.khoitriso.R

/**
 * Helper object to get localized messages from MessageCode
 * Maps MessageCode enum values to string resources
 */
object MessageCodeHelper {
    /**
     * Get localized message string from MessageCode
     * @param context Android Context to access string resources
     * @param messageCode The MessageCode enum value
     * @return Localized string message, or the message code itself if resource not found
     */
    fun getMessage(context: Context, messageCode: MessageCode): String {
        return try {
            val stringResId = getStringResourceId(messageCode)
            context.getString(stringResId)
        } catch (e: Exception) {
            // If string resource not found, return the code itself
            messageCode.code
        }
    }

    /**
     * Get localized message string from message code string
     * @param context Android Context to access string resources
     * @param code The message code string from API response
     * @return Localized string message, or the code itself if not found
     */
    fun getMessage(context: Context, code: String?): String {
        val messageCode = MessageCode.fromString(code)
        return getMessage(context, messageCode)
    }

    /**
     * Get string resource ID for a MessageCode
     * @param messageCode The MessageCode enum value
     * @return String resource ID
     */
    private fun getStringResourceId(messageCode: MessageCode): Int {
        return when (messageCode) {
            // ===== SUCCESS MESSAGES =====
            MessageCode.SUCCESS -> R.string.msg_success
            MessageCode.CREATED_SUCCESS -> R.string.msg_created_success
            MessageCode.UPDATED_SUCCESS -> R.string.msg_updated_success
            MessageCode.DELETED_SUCCESS -> R.string.msg_deleted_success

            // ===== AUTH MESSAGES =====
            MessageCode.LOGIN_SUCCESS -> R.string.msg_login_success
            MessageCode.LOGOUT_SUCCESS -> R.string.msg_logout_success
            MessageCode.REGISTER_SUCCESS -> R.string.msg_register_success
            MessageCode.REFRESH_TOKEN_SUCCESS -> R.string.msg_refresh_token_success
            MessageCode.PASSWORD_CHANGED_SUCCESS -> R.string.msg_password_changed_success
            MessageCode.PASSWORD_RESET_SUCCESS -> R.string.msg_password_reset_success
            MessageCode.EMAIL_VERIFIED_SUCCESS -> R.string.msg_email_verified_success
            MessageCode.PROFILE_UPDATED_SUCCESS -> R.string.msg_profile_updated_success
            MessageCode.PROFILE_RETRIEVED_SUCCESS -> R.string.msg_profile_retrieved_success
            MessageCode.OAUTH_SUCCESS -> R.string.msg_oauth_success
            MessageCode.GOOGLE_SDK_LOGIN_SUCCESS -> R.string.msg_google_sdk_login_success
            MessageCode.FACEBOOK_SDK_LOGIN_SUCCESS -> R.string.msg_facebook_sdk_login_success

            // ===== USER MESSAGES =====
            MessageCode.USER_CREATED_SUCCESS -> R.string.msg_user_created_success
            MessageCode.USER_UPDATED_SUCCESS -> R.string.msg_user_updated_success
            MessageCode.USER_DELETED_SUCCESS -> R.string.msg_user_deleted_success
            MessageCode.USER_LIST_SUCCESS -> R.string.msg_user_list_success
            MessageCode.USER_DETAIL_SUCCESS -> R.string.msg_user_detail_success
            MessageCode.AVATAR_UPDATED_SUCCESS -> R.string.msg_avatar_updated_success

            // ===== COURSE MESSAGES =====
            MessageCode.COURSE_CREATED_SUCCESS -> R.string.msg_course_created_success
            MessageCode.COURSE_UPDATED_SUCCESS -> R.string.msg_course_updated_success
            MessageCode.COURSE_DELETED_SUCCESS -> R.string.msg_course_deleted_success
            MessageCode.COURSE_LIST_SUCCESS -> R.string.msg_course_list_success
            MessageCode.COURSE_DETAIL_SUCCESS -> R.string.msg_course_detail_success
            MessageCode.COURSE_ENROLLED_SUCCESS -> R.string.msg_course_enrolled_success
            MessageCode.COURSE_SUBMITTED_FOR_REVIEW_SUCCESS -> R.string.msg_course_submitted_for_review_success
            MessageCode.ENROLLMENT_SUCCESS -> R.string.msg_enrollment_success
            MessageCode.UNENROLLMENT_SUCCESS -> R.string.msg_unenrollment_success
            MessageCode.ENROLLMENT_NOT_FOUND -> R.string.msg_enrollment_not_found
            MessageCode.ALREADY_ENROLLED -> R.string.msg_already_enrolled
            MessageCode.PAYMENT_REQUIRED -> R.string.msg_payment_required

            // ===== CATEGORY MESSAGES =====
            MessageCode.CATEGORY_CREATED_SUCCESS -> R.string.msg_category_created_success
            MessageCode.CATEGORY_UPDATED_SUCCESS -> R.string.msg_category_updated_success
            MessageCode.CATEGORY_DELETED_SUCCESS -> R.string.msg_category_deleted_success
            MessageCode.CATEGORY_LIST_SUCCESS -> R.string.msg_category_list_success
            MessageCode.CATEGORY_DETAIL_SUCCESS -> R.string.msg_category_detail_success

            // ===== CART MESSAGES =====
            MessageCode.CART_ITEM_ADDED_SUCCESS -> R.string.msg_cart_item_added_success
            MessageCode.CART_ITEM_UPDATED_SUCCESS -> R.string.msg_cart_item_updated_success
            MessageCode.CART_ITEM_DELETED_SUCCESS -> R.string.msg_cart_item_deleted_success
            MessageCode.CART_CLEARED_SUCCESS -> R.string.msg_cart_cleared_success
            MessageCode.CART_LIST_SUCCESS -> R.string.msg_cart_list_success

            // ===== ORDER MESSAGES =====
            MessageCode.ORDER_CREATED_SUCCESS -> R.string.msg_order_created_success
            MessageCode.ORDER_UPDATED_SUCCESS -> R.string.msg_order_updated_success
            MessageCode.ORDER_CANCELLED_SUCCESS -> R.string.msg_order_cancelled_success
            MessageCode.ORDER_LIST_SUCCESS -> R.string.msg_order_list_success
            MessageCode.ORDER_DETAIL_SUCCESS -> R.string.msg_order_detail_success
            MessageCode.ORDER_STATUS_UPDATED_SUCCESS -> R.string.msg_order_status_updated_success
            MessageCode.ORDER_PAYMENT_PROCESSED_SUCCESS -> R.string.msg_order_payment_processed_success
            MessageCode.ORDER_NOT_FOUND -> R.string.msg_order_not_found
            MessageCode.VNPAY_PAYMENT_URL_SUCCESS -> R.string.msg_vnpay_payment_url_success
            MessageCode.VNPAY_QUERY_SUCCESS -> R.string.msg_vnpay_query_success
            MessageCode.VNPAY_QUERY_FAILED -> R.string.msg_vnpay_query_failed
            MessageCode.VNPAY_STATUS_SUCCESS -> R.string.msg_vnpay_status_success

            // ===== COUPON MESSAGES =====
            MessageCode.COUPON_CREATED_SUCCESS -> R.string.msg_coupon_created_success
            MessageCode.COUPON_UPDATED_SUCCESS -> R.string.msg_coupon_updated_success
            MessageCode.COUPON_DELETED_SUCCESS -> R.string.msg_coupon_deleted_success
            MessageCode.COUPON_LIST_SUCCESS -> R.string.msg_coupon_list_success
            MessageCode.COUPON_DETAIL_SUCCESS -> R.string.msg_coupon_detail_success
            MessageCode.COUPON_VALID_SUCCESS -> R.string.msg_coupon_valid_success
            MessageCode.COUPON_INVALID -> R.string.msg_coupon_invalid
            MessageCode.COUPON_NOT_FOUND -> R.string.msg_coupon_not_found
            MessageCode.COUPON_ALREADY_EXISTS -> R.string.msg_coupon_already_exists

            // ===== LEARNING PATH MESSAGES =====
            MessageCode.LEARNING_PATH_CREATED_SUCCESS -> R.string.msg_learning_path_created_success
            MessageCode.LEARNING_PATH_UPDATED_SUCCESS -> R.string.msg_learning_path_updated_success
            MessageCode.LEARNING_PATH_DELETED_SUCCESS -> R.string.msg_learning_path_deleted_success
            MessageCode.LEARNING_PATH_LIST_SUCCESS -> R.string.msg_learning_path_list_success
            MessageCode.LEARNING_PATH_DETAIL_SUCCESS -> R.string.msg_learning_path_detail_success
            MessageCode.LEARNING_PATH_COURSES_SUCCESS -> R.string.msg_learning_path_courses_success
            MessageCode.LEARNING_PATH_COURSE_ADDED_SUCCESS -> R.string.msg_learning_path_course_added_success
            MessageCode.LEARNING_PATH_COURSE_REMOVED_SUCCESS -> R.string.msg_learning_path_course_removed_success
            MessageCode.LEARNING_PATH_COURSE_UPDATED_SUCCESS -> R.string.msg_learning_path_course_updated_success
            MessageCode.LEARNING_PATH_ENROLLED_SUCCESS -> R.string.msg_learning_path_enrolled_success
            MessageCode.LEARNING_PATH_ALREADY_ENROLLED -> R.string.msg_learning_path_already_enrolled
            MessageCode.LEARNING_PATH_NOT_FOUND -> R.string.msg_learning_path_not_found
            MessageCode.LEARNING_PATH_COURSE_ALREADY_EXISTS -> R.string.msg_learning_path_course_already_exists
            MessageCode.LEARNING_PATH_COURSE_NOT_FOUND -> R.string.msg_learning_path_course_not_found

            // ===== WISHLIST MESSAGES =====
            MessageCode.WISHLIST_ITEM_ADDED_SUCCESS -> R.string.msg_wishlist_item_added_success
            MessageCode.WISHLIST_ITEM_DELETED_SUCCESS -> R.string.msg_wishlist_item_deleted_success
            MessageCode.WISHLIST_LIST_SUCCESS -> R.string.msg_wishlist_list_success

            // ===== REVIEW MESSAGES =====
            MessageCode.REVIEW_CREATED_SUCCESS -> R.string.msg_review_created_success
            MessageCode.REVIEW_UPDATED_SUCCESS -> R.string.msg_review_updated_success
            MessageCode.REVIEW_DELETED_SUCCESS -> R.string.msg_review_deleted_success
            MessageCode.REVIEW_LIST_SUCCESS -> R.string.msg_review_list_success
            MessageCode.REVIEW_ALREADY_EXISTS -> R.string.msg_review_already_exists
            MessageCode.REVIEW_NOT_FOUND -> R.string.msg_review_not_found

            // ===== NOTIFICATION MESSAGES =====
            MessageCode.NOTIFICATION_CREATED_SUCCESS -> R.string.msg_notification_created_success
            MessageCode.NOTIFICATION_LIST_SUCCESS -> R.string.msg_notification_list_success
            MessageCode.NOTIFICATION_READ_SUCCESS -> R.string.msg_notification_read_success
            MessageCode.NOTIFICATION_DELETED_SUCCESS -> R.string.msg_notification_deleted_success
            MessageCode.NOTIFICATION_NOT_FOUND -> R.string.msg_notification_not_found

            // ===== NOTIFICATION CONTENT MESSAGES =====
            // Course Notifications - Titles
            MessageCode.NOTI_TITLE_COURSE_ENROLLED -> R.string.msg_noti_title_course_enrolled
            MessageCode.NOTI_TITLE_COURSE_COMPLETED -> R.string.msg_noti_title_course_completed
            MessageCode.NOTI_TITLE_COURSE_NEW_LESSON -> R.string.msg_noti_title_course_new_lesson
            MessageCode.NOTI_TITLE_COURSE_APPROVED -> R.string.msg_noti_title_course_approved
            MessageCode.NOTI_TITLE_COURSE_REJECTED -> R.string.msg_noti_title_course_rejected

            // Course Notifications - Messages
            MessageCode.NOTI_COURSE_ENROLLED -> R.string.msg_noti_course_enrolled
            MessageCode.NOTI_COURSE_COMPLETED -> R.string.msg_noti_course_completed
            MessageCode.NOTI_COURSE_NEW_LESSON -> R.string.msg_noti_course_new_lesson
            MessageCode.NOTI_COURSE_APPROVED -> R.string.msg_noti_course_approved
            MessageCode.NOTI_COURSE_REJECTED -> R.string.msg_noti_course_rejected

            // Order & Payment Notifications - Titles
            MessageCode.NOTI_TITLE_ORDER_CREATED -> R.string.msg_noti_title_order_created
            MessageCode.NOTI_TITLE_ORDER_PAID -> R.string.msg_noti_title_order_paid
            MessageCode.NOTI_TITLE_ORDER_CANCELLED -> R.string.msg_noti_title_order_cancelled

            // Order & Payment Notifications - Messages
            MessageCode.NOTI_ORDER_CREATED -> R.string.msg_noti_order_created
            MessageCode.NOTI_ORDER_PAID -> R.string.msg_noti_order_paid
            MessageCode.NOTI_ORDER_CANCELLED -> R.string.msg_noti_order_cancelled

            // Certificate Notifications
            MessageCode.NOTI_TITLE_CERTIFICATE_ISSUED -> R.string.msg_noti_title_certificate_issued
            MessageCode.NOTI_CERTIFICATE_ISSUED -> R.string.msg_noti_certificate_issued

            // Assignment Notifications
            MessageCode.NOTI_TITLE_ASSIGNMENT_NEW -> R.string.msg_noti_title_assignment_new
            MessageCode.NOTI_TITLE_ASSIGNMENT_GRADED -> R.string.msg_noti_title_assignment_graded
            MessageCode.NOTI_TITLE_ASSIGNMENT_DUE_SOON -> R.string.msg_noti_title_assignment_due_soon
            MessageCode.NOTI_ASSIGNMENT_NEW -> R.string.msg_noti_assignment_new
            MessageCode.NOTI_ASSIGNMENT_GRADED -> R.string.msg_noti_assignment_graded
            MessageCode.NOTI_ASSIGNMENT_DUE_SOON -> R.string.msg_noti_assignment_due_soon

            // Lesson Notifications
            MessageCode.NOTI_TITLE_LESSON_COMPLETED -> R.string.msg_noti_title_lesson_completed
            MessageCode.NOTI_TITLE_LESSON_DISCUSSION_REPLY -> R.string.msg_noti_title_lesson_discussion_reply
            MessageCode.NOTI_LESSON_COMPLETED -> R.string.msg_noti_lesson_completed
            MessageCode.NOTI_LESSON_DISCUSSION_REPLY -> R.string.msg_noti_lesson_discussion_reply

            // Book Notifications
            MessageCode.NOTI_TITLE_BOOK_ACTIVATED -> R.string.msg_noti_title_book_activated
            MessageCode.NOTI_TITLE_BOOK_APPROVED -> R.string.msg_noti_title_book_approved
            MessageCode.NOTI_TITLE_BOOK_REJECTED -> R.string.msg_noti_title_book_rejected
            MessageCode.NOTI_BOOK_ACTIVATED -> R.string.msg_noti_book_activated
            MessageCode.NOTI_BOOK_APPROVED -> R.string.msg_noti_book_approved
            MessageCode.NOTI_BOOK_REJECTED -> R.string.msg_noti_book_rejected

            // LiveClass Notifications
            MessageCode.NOTI_TITLE_LIVE_CLASS_SCHEDULED -> R.string.msg_noti_title_live_class_scheduled
            MessageCode.NOTI_TITLE_LIVE_CLASS_STARTING -> R.string.msg_noti_title_live_class_starting
            MessageCode.NOTI_TITLE_LIVE_CLASS_STARTED -> R.string.msg_noti_title_live_class_started
            MessageCode.NOTI_LIVE_CLASS_SCHEDULED -> R.string.msg_noti_live_class_scheduled
            MessageCode.NOTI_LIVE_CLASS_STARTING -> R.string.msg_noti_live_class_starting
            MessageCode.NOTI_LIVE_CLASS_STARTED -> R.string.msg_noti_live_class_started

            // LearningPath Notifications
            MessageCode.NOTI_TITLE_LEARNING_PATH_ENROLLED -> R.string.msg_noti_title_learning_path_enrolled
            MessageCode.NOTI_TITLE_LEARNING_PATH_COMPLETED -> R.string.msg_noti_title_learning_path_completed
            MessageCode.NOTI_LEARNING_PATH_ENROLLED -> R.string.msg_noti_learning_path_enrolled
            MessageCode.NOTI_LEARNING_PATH_COMPLETED -> R.string.msg_noti_learning_path_completed

            // Forum Notifications
            MessageCode.NOTI_TITLE_FORUM_QUESTION_ANSWERED -> R.string.msg_noti_title_forum_question_answered
            MessageCode.NOTI_TITLE_FORUM_ANSWER_ACCEPTED -> R.string.msg_noti_title_forum_answer_accepted
            MessageCode.NOTI_FORUM_QUESTION_ANSWERED -> R.string.msg_noti_forum_question_answered
            MessageCode.NOTI_FORUM_ANSWER_ACCEPTED -> R.string.msg_noti_forum_answer_accepted
            MessageCode.ANSWER_NOT_ACCEPTED -> R.string.msg_answer_not_accepted

            // Review Notifications
            MessageCode.NOTI_TITLE_REVIEW_REPLIED -> R.string.msg_noti_title_review_replied
            MessageCode.NOTI_TITLE_REVIEW_HELPFUL_MILESTONE -> R.string.msg_noti_title_review_helpful_milestone
            MessageCode.NOTI_REVIEW_REPLIED -> R.string.msg_noti_review_replied
            MessageCode.NOTI_REVIEW_HELPFUL_MILESTONE -> R.string.msg_noti_review_helpful_milestone

            // Wishlist Notifications
            MessageCode.NOTI_TITLE_WISHLIST_ON_SALE -> R.string.msg_noti_title_wishlist_on_sale
            MessageCode.NOTI_WISHLIST_ON_SALE -> R.string.msg_noti_wishlist_on_sale

            // Coupon Notifications
            MessageCode.NOTI_TITLE_COUPON_APPLIED -> R.string.msg_noti_title_coupon_applied
            MessageCode.NOTI_TITLE_COUPON_EXPIRING -> R.string.msg_noti_title_coupon_expiring
            MessageCode.NOTI_COUPON_APPLIED -> R.string.msg_noti_coupon_applied
            MessageCode.NOTI_COUPON_EXPIRING -> R.string.msg_noti_coupon_expiring

            // System Notifications
            MessageCode.NOTI_TITLE_SYSTEM_ANNOUNCEMENT -> R.string.msg_noti_title_system_announcement
            MessageCode.NOTI_TITLE_SYSTEM_MAINTENANCE -> R.string.msg_noti_title_system_maintenance
            MessageCode.NOTI_SYSTEM_ANNOUNCEMENT -> R.string.msg_noti_system_announcement
            MessageCode.NOTI_SYSTEM_MAINTENANCE -> R.string.msg_noti_system_maintenance

            // ===== LESSON MESSAGES =====
            MessageCode.LESSON_CREATED_SUCCESS -> R.string.msg_lesson_created_success
            MessageCode.LESSON_UPDATED_SUCCESS -> R.string.msg_lesson_updated_success
            MessageCode.LESSON_DELETED_SUCCESS -> R.string.msg_lesson_deleted_success
            MessageCode.LESSON_NOT_FOUND -> R.string.msg_lesson_not_found
            MessageCode.LESSON_PROGRESS_UPDATED_SUCCESS -> R.string.msg_lesson_progress_updated_success
            MessageCode.VIDEO_PROGRESS_RETRIEVED_SUCCESS -> R.string.msg_video_progress_retrieved_success
            MessageCode.VIDEO_PROGRESS_UPDATED_SUCCESS -> R.string.msg_video_progress_updated_success

            // ===== ASSIGNMENT MESSAGES =====
            MessageCode.ASSIGNMENT_CREATED_SUCCESS -> R.string.msg_assignment_created_success
            MessageCode.ASSIGNMENT_UPDATED_SUCCESS -> R.string.msg_assignment_updated_success
            MessageCode.ASSIGNMENT_DELETED_SUCCESS -> R.string.msg_assignment_deleted_success
            MessageCode.ASSIGNMENT_NOT_FOUND -> R.string.msg_assignment_not_found
            MessageCode.ASSIGNMENT_LIST_SUCCESS -> R.string.msg_assignment_list_success
            MessageCode.ASSIGNMENT_DETAIL_SUCCESS -> R.string.msg_assignment_detail_success
            MessageCode.QUESTION_ADDED_TO_ASSIGNMENT_SUCCESS -> R.string.msg_question_added_to_assignment_success
            MessageCode.QUESTION_REMOVED_FROM_ASSIGNMENT_SUCCESS -> R.string.msg_question_removed_from_assignment_success
            MessageCode.QUESTION_NOT_FOUND -> R.string.msg_question_not_found
            MessageCode.QUESTION_UPDATED_SUCCESS -> R.string.msg_question_updated_success
            MessageCode.QUESTION_CREATED_SUCCESS -> R.string.msg_question_created_success
            MessageCode.QUESTIONS_CREATED_SUCCESS -> R.string.msg_questions_created_success
            MessageCode.ASSIGNMENT_SUBMITTED_SUCCESS -> R.string.msg_assignment_submitted_success
            MessageCode.ASSIGNMENT_GRADED_SUCCESS -> R.string.msg_assignment_graded_success
            MessageCode.MAX_ATTEMPTS_EXCEEDED -> R.string.msg_max_attempts_exceeded
            MessageCode.ATTEMPT_NOT_FOUND -> R.string.msg_attempt_not_found
            MessageCode.ASSIGNMENT_DUE_DATE_PASSED -> R.string.msg_assignment_due_date_passed
            MessageCode.INVALID_DEFAULT_POINTS_CONFIGURATION -> R.string.msg_invalid_default_points_configuration
            MessageCode.TOTAL_POINTS_MUST_EQUAL_TEN -> R.string.msg_total_points_must_equal_ten
            MessageCode.ASSIGNMENT_ANSWERS_NOT_AVAILABLE -> R.string.msg_assignment_answers_not_available
            MessageCode.ASSIGNMENT_ATTEMPT_IN_PROGRESS -> R.string.msg_assignment_attempt_in_progress
            MessageCode.ASSIGNMENT_TIME_LIMIT_EXCEEDED -> R.string.msg_assignment_time_limit_exceeded
            MessageCode.ASSIGNMENT_ATTEMPT_ALREADY_COMPLETED -> R.string.msg_assignment_attempt_already_completed
            MessageCode.ASSIGNMENT_BATCH_GRADED_SUCCESS -> R.string.msg_assignment_batch_graded_success

            // ===== LESSON MATERIAL MESSAGES =====
            MessageCode.LESSON_MATERIAL_CREATED_SUCCESS -> R.string.msg_lesson_material_created_success
            MessageCode.LESSON_MATERIAL_UPDATED_SUCCESS -> R.string.msg_lesson_material_updated_success
            MessageCode.LESSON_MATERIAL_DELETED_SUCCESS -> R.string.msg_lesson_material_deleted_success
            MessageCode.LESSON_MATERIAL_NOT_FOUND -> R.string.msg_lesson_material_not_found
            MessageCode.LESSON_MATERIAL_LIST_SUCCESS -> R.string.msg_lesson_material_list_success

            // ===== DISCUSSION MESSAGES =====
            MessageCode.DISCUSSION_CREATED_SUCCESS -> R.string.msg_discussion_created_success
            MessageCode.DISCUSSION_UPDATED_SUCCESS -> R.string.msg_discussion_updated_success
            MessageCode.DISCUSSION_DELETED_SUCCESS -> R.string.msg_discussion_deleted_success
            MessageCode.DISCUSSION_NOT_FOUND -> R.string.msg_discussion_not_found
            MessageCode.REPLY_CREATED_SUCCESS -> R.string.msg_reply_created_success
            MessageCode.DISCUSSION_LIKE_SUCCESS -> R.string.msg_discussion_like_success
            MessageCode.DISCUSSION_UNLIKE_SUCCESS -> R.string.msg_discussion_unlike_success
            MessageCode.LIKE_UPDATED_SUCCESS -> R.string.msg_like_updated_success

            // ===== LIVE CLASS MESSAGES =====
            MessageCode.LIVE_CLASS_CREATED_SUCCESS -> R.string.msg_live_class_created_success
            MessageCode.LIVE_CLASS_UPDATED_SUCCESS -> R.string.msg_live_class_updated_success
            MessageCode.LIVE_CLASS_DELETED_SUCCESS -> R.string.msg_live_class_deleted_success
            MessageCode.LIVE_CLASS_NOT_FOUND -> R.string.msg_live_class_not_found
            MessageCode.LIVE_CLASS_JOINED_SUCCESS -> R.string.msg_live_class_joined_success
            MessageCode.LIVE_CLASS_LEFT_SUCCESS -> R.string.msg_live_class_left_success
            MessageCode.LIVE_CLASS_FULL -> R.string.msg_live_class_full
            MessageCode.LIVE_CLASS_NOT_STARTED -> R.string.msg_live_class_not_started
            MessageCode.LIVE_CLASS_ENDED -> R.string.msg_live_class_ended
            MessageCode.LIVE_CLASS_ALREADY_JOINED -> R.string.msg_live_class_already_joined
            MessageCode.LIVE_CLASS_PARTICIPATION_NOT_FOUND -> R.string.msg_live_class_participation_not_found
            MessageCode.LIVE_CLASS_HAS_PARTICIPANTS -> R.string.msg_live_class_has_participants
            MessageCode.LIVE_CLASS_INSTRUCTOR_ONLY -> R.string.msg_live_class_instructor_only

            // ===== CERTIFICATE MESSAGES =====
            MessageCode.CERTIFICATE_GENERATED_SUCCESS -> R.string.msg_certificate_generated_success
            MessageCode.CERTIFICATE_NOT_FOUND -> R.string.msg_certificate_not_found
            MessageCode.CERTIFICATE_VERIFIED_SUCCESS -> R.string.msg_certificate_verified_success
            MessageCode.CERTIFICATE_REQUIREMENTS_NOT_MET -> R.string.msg_certificate_requirements_not_met

            // ===== ANALYTICS MESSAGES =====
            MessageCode.ANALYTICS_DASHBOARD_SUCCESS -> R.string.msg_analytics_dashboard_success
            MessageCode.ANALYTICS_COURSE_SUCCESS -> R.string.msg_analytics_course_success
            MessageCode.ANALYTICS_INSTRUCTOR_SUCCESS -> R.string.msg_analytics_instructor_success
            MessageCode.INVALID_PERIOD -> R.string.msg_invalid_period

            // ===== BACKUP MESSAGES =====
            MessageCode.BACKUP_CREATED_SUCCESS -> R.string.msg_backup_created_success
            MessageCode.BACKUP_LIST_SUCCESS -> R.string.msg_backup_list_success
            MessageCode.BACKUP_DETAIL_SUCCESS -> R.string.msg_backup_detail_success
            MessageCode.BACKUP_DELETED_SUCCESS -> R.string.msg_backup_deleted_success
            MessageCode.BACKUP_DOWNLOAD_SUCCESS -> R.string.msg_backup_download_success
            MessageCode.BACKUP_RESTORE_SUCCESS -> R.string.msg_backup_restore_success
            MessageCode.BACKUP_SETTINGS_UPDATED_SUCCESS -> R.string.msg_backup_settings_updated_success
            MessageCode.BACKUP_IN_PROGRESS -> R.string.msg_backup_in_progress
            MessageCode.BACKUP_FAILED -> R.string.msg_backup_failed
            MessageCode.BACKUP_NOT_FOUND -> R.string.msg_backup_not_found
            MessageCode.BACKUP_RESTORE_FAILED -> R.string.msg_backup_restore_failed
            MessageCode.BACKUP_RESTORE_CONFIRMATION_REQUIRED -> R.string.msg_backup_restore_confirmation_required

            // ===== ERROR MESSAGES =====
            // Validation Errors
            MessageCode.VALIDATION_ERROR -> R.string.msg_validation_error
            MessageCode.INVALID_INPUT -> R.string.msg_invalid_input
            MessageCode.INVALID_EMAIL -> R.string.msg_invalid_email
            MessageCode.INVALID_PASSWORD -> R.string.msg_invalid_password
            MessageCode.INVALID_PHONE_NUMBER -> R.string.msg_invalid_phone_number
            MessageCode.INVALID_PHONE_FORMAT -> R.string.msg_invalid_phone_format
            MessageCode.INVALID_LANGUAGE -> R.string.msg_invalid_language
            MessageCode.INVALID_TIMEZONE -> R.string.msg_invalid_timezone
            MessageCode.FILE_REQUIRED -> R.string.msg_file_required
            MessageCode.INVALID_FILE_TYPE -> R.string.msg_invalid_file_type
            MessageCode.FILE_TOO_LARGE -> R.string.msg_file_too_large

            // Authentication Errors
            MessageCode.UNAUTHORIZED -> R.string.msg_unauthorized
            MessageCode.FORBIDDEN -> R.string.msg_forbidden
            MessageCode.TOKEN_EXPIRED -> R.string.msg_token_expired
            MessageCode.TOKEN_INVALID -> R.string.msg_token_invalid
            MessageCode.REFRESH_TOKEN_INVALID -> R.string.msg_refresh_token_invalid
            MessageCode.REFRESH_TOKEN_EXPIRED -> R.string.msg_refresh_token_expired
            MessageCode.INVALID_CREDENTIALS -> R.string.msg_invalid_credentials
            MessageCode.ACCOUNT_LOCKED -> R.string.msg_account_locked
            MessageCode.ACCOUNT_NOT_VERIFIED -> R.string.msg_account_not_verified
            MessageCode.OAUTH_INVALID_STATE_OR_CODE -> R.string.msg_oauth_invalid_state_or_code
            MessageCode.OAUTH_TOKEN_EXCHANGE_FAILED -> R.string.msg_oauth_token_exchange_failed
            MessageCode.OAUTH_INVALID_TOKEN_RESPONSE -> R.string.msg_oauth_invalid_token_response
            MessageCode.OAUTH_FAILED_FETCH_USER_INFO -> R.string.msg_oauth_failed_fetch_user_info
            MessageCode.FACEBOOK_CLIENTID_NOT_CONFIGURED -> R.string.msg_facebook_clientid_not_configured
            MessageCode.FACEBOOK_CLIENTSECRET_NOT_CONFIGURED -> R.string.msg_facebook_clientsecret_not_configured
            MessageCode.FACEBOOK_EMAIL_PERMISSION_REQUIRED -> R.string.msg_facebook_email_permission_required
            MessageCode.STUDENT_MUST_USE_OAUTH -> R.string.msg_student_must_use_oauth
            MessageCode.RESET_TOKEN_INVALID -> R.string.msg_reset_token_invalid
            MessageCode.RESET_TOKEN_EXPIRED -> R.string.msg_reset_token_expired
            MessageCode.EMAIL_VERIFICATION_TOKEN_INVALID -> R.string.msg_email_verification_token_invalid
            MessageCode.EMAIL_VERIFICATION_TOKEN_EXPIRED -> R.string.msg_email_verification_token_expired
            MessageCode.REGISTER_FAILED -> R.string.msg_register_failed
            MessageCode.PASSWORD_CHANGE_FAILED -> R.string.msg_password_change_failed
            MessageCode.USER_DOES_NOT_EXIST -> R.string.msg_user_does_not_exist
            MessageCode.USERNAME_EMAIL_EXISTS -> R.string.msg_username_email_exists
            MessageCode.PASSWORD_NOT_STRONG_ENOUGH -> R.string.msg_password_not_strong_enough
            MessageCode.CURRENT_PASSWORD_INCORRECT -> R.string.msg_current_password_incorrect

            // Resource Errors
            MessageCode.NOT_FOUND -> R.string.msg_not_found
            MessageCode.RESOURCE_NOT_FOUND -> R.string.msg_resource_not_found
            MessageCode.USER_NOT_FOUND -> R.string.msg_user_not_found
            MessageCode.COURSE_NOT_FOUND -> R.string.msg_course_not_found
            MessageCode.CATEGORY_NOT_FOUND -> R.string.msg_category_not_found
            MessageCode.CART_ITEM_NOT_FOUND -> R.string.msg_cart_item_not_found

            // Business Logic Errors
            MessageCode.EMAIL_ALREADY_EXISTS -> R.string.msg_email_already_exists
            MessageCode.USERNAME_ALREADY_EXISTS -> R.string.msg_username_already_exists
            MessageCode.WEAK_PASSWORD -> R.string.msg_weak_password
            MessageCode.PASSWORD_MISMATCH -> R.string.msg_password_mismatch
            MessageCode.COURSE_ALREADY_ENROLLED -> R.string.msg_course_already_enrolled
            MessageCode.INSUFFICIENT_PERMISSION -> R.string.msg_insufficient_permission
            MessageCode.ITEM_ALREADY_IN_CART -> R.string.msg_item_already_in_cart
            MessageCode.ITEM_ALREADY_IN_WISHLIST -> R.string.msg_item_already_in_wishlist

            // System Errors
            MessageCode.INTERNAL_SERVER_ERROR -> R.string.msg_internal_server_error
            MessageCode.DATABASE_ERROR -> R.string.msg_database_error
            MessageCode.EXTERNAL_SERVICE_ERROR -> R.string.msg_external_service_error

            // Rate Limiting
            MessageCode.RATE_LIMIT_EXCEEDED -> R.string.msg_rate_limit_exceeded
            MessageCode.TOO_MANY_REQUESTS -> R.string.msg_too_many_requests

            // Settings
            MessageCode.SETTINGS_UPDATED_SUCCESS -> R.string.msg_settings_updated_success
            MessageCode.SETTING_CREATED_SUCCESS -> R.string.msg_setting_created_success
            MessageCode.SETTING_KEY_ALREADY_EXISTS -> R.string.msg_setting_key_already_exists

            // ===== FILE/UPLOAD MESSAGES =====
            MessageCode.UPLOAD_SUCCESS -> R.string.msg_upload_success
            MessageCode.UPLOAD_FAILED -> R.string.msg_upload_failed
            MessageCode.PRESIGNED_URL_GENERATED -> R.string.msg_presigned_url_generated
            MessageCode.FILE_DELETED_SUCCESS -> R.string.msg_file_deleted_success
            MessageCode.FILE_DELETE_FAILED -> R.string.msg_file_delete_failed
            MessageCode.FILE_NOT_FOUND -> R.string.msg_file_not_found
            MessageCode.BATCH_DELETE_FAILED -> R.string.msg_batch_delete_failed
            MessageCode.FILE_CLEANUP_SUCCESS -> R.string.msg_file_cleanup_success
            MessageCode.FILE_CREATED_SUCCESS -> R.string.msg_file_created_success
            MessageCode.FILE_CREATE_FAILED -> R.string.msg_file_create_failed
            MessageCode.FILE_UPDATED_SUCCESS -> R.string.msg_file_updated_success
            MessageCode.FILE_UPDATE_FAILED -> R.string.msg_file_update_failed
            MessageCode.FILE_COPIED_SUCCESS -> R.string.msg_file_copied_success
            MessageCode.FILE_COPY_FAILED -> R.string.msg_file_copy_failed

            // ===== BOOK MESSAGES =====
            MessageCode.BOOK_CREATED_SUCCESS -> R.string.msg_book_created_success
            MessageCode.BOOK_UPDATED_SUCCESS -> R.string.msg_book_updated_success
            MessageCode.BOOK_DELETED_SUCCESS -> R.string.msg_book_deleted_success
            MessageCode.BOOK_NOT_FOUND -> R.string.msg_book_not_found
            MessageCode.BOOK_QUESTION_CREATED_SUCCESS -> R.string.msg_book_question_created_success
            MessageCode.BOOK_QUESTION_UPDATED_SUCCESS -> R.string.msg_book_question_updated_success
            MessageCode.BOOK_SOLUTION_CREATED_SUCCESS -> R.string.msg_book_solution_created_success
            MessageCode.BOOK_SOLUTION_UPDATED_SUCCESS -> R.string.msg_book_solution_updated_success
            MessageCode.ACTIVATION_CODE_GENERATED_SUCCESS -> R.string.msg_activation_code_generated_success
            MessageCode.ACTIVATION_CODE_ACTIVATED_SUCCESS -> R.string.msg_activation_code_activated_success
            MessageCode.ACTIVATION_CODE_INVALID -> R.string.msg_activation_code_invalid
            MessageCode.ACTIVATION_CODE_EXPIRED -> R.string.msg_activation_code_expired
            MessageCode.ACTIVATION_CODE_ALREADY_USED -> R.string.msg_activation_code_already_used
            MessageCode.BOOK_ALREADY_OWNED -> R.string.msg_book_already_owned
            MessageCode.BOOK_ACTIVATED_SUCCESS -> R.string.msg_book_activated_success
            MessageCode.MY_BOOKS_RETRIEVED_SUCCESS -> R.string.msg_my_books_retrieved_success
            MessageCode.BOOK_CHAPTERS_RETRIEVED_SUCCESS -> R.string.msg_book_chapters_retrieved_success
            MessageCode.BOOK_CHAPTER_CREATED_SUCCESS -> R.string.msg_book_chapter_created_success
            MessageCode.BOOK_CHAPTER_UPDATED_SUCCESS -> R.string.msg_book_chapter_updated_success
            MessageCode.BOOK_CHAPTER_DELETED_SUCCESS -> R.string.msg_book_chapter_deleted_success
            MessageCode.BOOK_CHAPTER_NOT_FOUND -> R.string.msg_book_chapter_not_found

            // Unknown message code
            MessageCode.UNKNOWN -> R.string.msg_unknown
        }
    }
}

