package com.example.khoitriso.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DeviceHub
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.khoitriso.domain.models.Question
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

object NavRoute {

    val EXPLORE_BOOK: String = "exloreBook"
    val EXPLORE_COURSE: String = "exloreCourse"
    val LOGIN: String = "login"
    val HOME: String = "home"
    val PROFILE: String = "profile"
    val SEARCH: String = "search"
    val LEARNING_PATH = "learningPath"
    val COURSE_DETAIL = "courseDetail"
    const val LEARNING_COURSE = "learningCourse"
    const val LEARNING_BOOK = "learningBook"
    val BOOK_DETAIL = "bookDetail"
    val FORUM = "forum"
    val FORUM_ASK = "forum/ask"
    val FORUM_BOOKMARKS = "forum/bookmarks"
    val NOTIFICATIONS = "notifications"
    val CART = "cart"
    val CHECKOUT = "checkout"
    val MY_PURCHASES = "myPurchases"
    const val ASSIGNMENT = "assignment"
    const val CourseDetailWithArgs = "courseDetail/{courseId}"
    const val LearningCourseWithArgs = "$LEARNING_COURSE/{courseId}"
    const val LearningBookWithArgs = "$LEARNING_BOOK/{bookId}"
    const val BookDetailWithArgs = "bookDetail/{bookId}"
    const val ForumDetailWithArgs = "forum/{questionId}"
    const val MyPurchasesWithTab = "myPurchases?tab={tab}"
    const val AssignmentWithArgs = "assignment/{assignmentId}"
    const val PaymentResultWithArgs = "paymentResult?success={success}&orderCode={orderCode}"
    fun NavCourseDetail(courseId: Int) = "$COURSE_DETAIL/$courseId"
    fun NavLearningCourse(courseId: Int) = "$LEARNING_COURSE/$courseId"
    fun NavLearningBook(bookId: Int) = "$LEARNING_BOOK/$bookId"
    fun NavBookDetail(bookId: Int) = "$BOOK_DETAIL/$bookId"
    fun NavForumDetail(questionId: String) = "forum/$questionId"
    fun NavAssignment(assignmentId: Int) = "assignment/$assignmentId"
    fun NavMyPurchases(tab: String = "courses") = "myPurchases?tab=$tab"
    fun NavPaymentResult(success: Boolean, orderCode: String?) =
        "paymentResult?success=$success&orderCode=${orderCode ?: ""}"
}


data class MyNavigationBarItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val navigationBarItems = listOf(
    MyNavigationBarItem(
        label = NavRoute.HOME,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    MyNavigationBarItem(
        label = NavRoute.SEARCH,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    MyNavigationBarItem(
        label = NavRoute.MY_PURCHASES,
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu
    ),
    MyNavigationBarItem(
        label = NavRoute.FORUM,
        selectedIcon = Icons.Filled.DeviceHub,
        unselectedIcon = Icons.Outlined.DeviceHub
    ),
    MyNavigationBarItem(
        label = NavRoute.PROFILE,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)