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
    val ORDER_HISTORY : String = "orderHistory"
    val EXPLORE_BOOK: String = "exloreBook"
    val EXPLORE_COURSE: String = "exloreCourse"
    val LOGIN: String = "login"
    val HOME: String = "home"
    val PROFILE: String = "profile"
    const val SEARCH = "search?tab={tab}"
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
    const val CHECKOUT = "checkout"
    val MY_PURCHASES = "myPurchases"
    const val ASSIGNMENT = "assignment"
    const val CATEGORY_ITEMS = "categoryItems"
    const val CheckoutWithArgs = "$CHECKOUT?itemType={itemType}&itemId={itemId}"
    const val CourseDetailWithArgs = "courseDetail/{courseId}"
    const val LearningCourseWithArgs = "$LEARNING_COURSE/{courseId}"
    const val LearningBookWithArgs = "$LEARNING_BOOK/{bookId}"
    const val BookDetailWithArgs = "bookDetail/{bookId}"
    const val ForumDetailWithArgs = "forum/{questionId}"
    const val MyPurchasesWithTab = "myPurchases?tab={tab}"
    const val AssignmentWithArgs = "assignment/{assignmentId}"
    const val PaymentResultWithArgs = "paymentResult?success={success}&orderCode={orderCode}"
    const val PaymentProcessingWithArgs = "paymentProcessing?paymentUrl={paymentUrl}&orderCode={orderCode}"
    const val CategoryItemsWithArgs = "$CATEGORY_ITEMS/{categoryId}/{categoryName}"
    fun NavCourseDetail(courseId: Int) = "$COURSE_DETAIL/$courseId"
    fun NavLearningCourse(courseId: Int) = "$LEARNING_COURSE/$courseId"
    fun NavLearningBook(bookId: Int) = "$LEARNING_BOOK/$bookId"
    fun NavBookDetail(bookId: Int) = "$BOOK_DETAIL/$bookId"
    fun NavForumDetail(questionId: String) = "forum/$questionId"
    fun NavAssignment(assignmentId: Int) = "assignment/$assignmentId"
    fun NavMyPurchases(tab: String = "courses") = "myPurchases?tab=$tab"
    fun NavPaymentResult(success: Boolean, orderCode: String?) =
        "paymentResult?success=$success&orderCode=${orderCode ?: ""}"
    fun NavPaymentProcessing(paymentUrl: String, orderCode: String) =
        "paymentProcessing?paymentUrl=${java.net.URLEncoder.encode(paymentUrl, "UTF-8")}&orderCode=$orderCode"
    fun NavSearchTab(tab:Int) = "search?tab=$tab" //0 = course, 1 = book
    fun NavCheckOutNow(itemType: Int, itemId: Int) = "$CHECKOUT?itemType=$itemType&itemId=$itemId"
    fun NavCategoryItems(categoryId: Int, categoryName: String) = 
        "$CATEGORY_ITEMS/$categoryId/$categoryName"
}


data class MyNavigationBarItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

@Serializable
data class ItemBuyNow(
    val itemId: Int,
    val itemType: Int,
    val coverImage: String,
    val price: Double,
    val title: String
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
        label = NavRoute.NavMyPurchases("courses"),
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