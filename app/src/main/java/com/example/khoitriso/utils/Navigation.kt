package com.example.khoitriso.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

object  NavRoute {

    val exploreBook: String = "exloreBook"
    val login: String = "login"
    val home: String = "home"
    val profile: String = "profile"
    val search : String = "search"
    val learningPath = "learningPath"
    val courseDetail = "courseDetail"
    val bookDetail = "bookDetail"
    val forum = "forum"
    val forumAsk = "forum/ask"
    val forumBookmarks = "forum/bookmarks"
    val notifications = "notifications"
    val cart = "cart"
    val checkout = "checkout"
    val myPurchases = "myPurchases"
    const val CourseDetailWithArgs = "courseDetail/{courseId}"
    const val BookDetailWithArgs = "bookDetail/{bookId}"
    const val ForumDetailWithArgs = "forum/{questionId}"
    const val MyPurchasesWithTab = "myPurchases?tab={tab}"
    const val PaymentResultWithArgs = "paymentResult?success={success}&orderCode={orderCode}"
    fun NavCourseDetail(courseId: Int) = "$courseDetail/$courseId"
    fun NavBookDetail(bookId: Int) = "$bookDetail/$bookId"
    fun NavForumDetail(questionId: String) = "forum/$questionId"
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
        label = NavRoute.home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    MyNavigationBarItem(
        label = NavRoute.search,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    MyNavigationBarItem(
        label = NavRoute.learningPath,
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu
    ),
    MyNavigationBarItem(
        label = NavRoute.profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)