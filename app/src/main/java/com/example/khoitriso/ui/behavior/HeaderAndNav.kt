package com.example.khoitriso.ui.behavior

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.ui.detail.BookDetailScreen
import com.example.khoitriso.ui.detail.CourseDetailScreen
import com.example.khoitriso.ui.explore.CategoryItemsScreen
import com.example.khoitriso.ui.explore.ExploreBookScreen
import com.example.khoitriso.ui.forum.ForumListScreen
import com.example.khoitriso.ui.forum.ForumDetailScreen
import com.example.khoitriso.ui.notification.NotificationScreen
import com.example.khoitriso.ui.homescreen.HomeScreen
import com.example.khoitriso.ui.loginscreen.LoginScreen
import com.example.khoitriso.ui.profilescreen.ProfileScreen
import com.example.khoitriso.ui.searchscreen.SearchScreen
import com.example.khoitriso.ui.cart.CartScreen
import com.example.khoitriso.ui.checkout.CheckoutScreen
import com.example.khoitriso.ui.explore.ExploreCourseScreen
import com.example.khoitriso.ui.forum.ForumAskScreen
import com.example.khoitriso.ui.forum.ForumBookmarksScreen
import com.example.khoitriso.ui.learning.AssignmentScreen
import com.example.khoitriso.ui.learning.LearningBookScreen
import com.example.khoitriso.ui.learning.LearningCourseScreen
import com.example.khoitriso.ui.mypurchase.MyPurchaseScreen
import com.example.khoitriso.ui.mypurchase.OrderScreen
import com.example.khoitriso.ui.paymentresult.PaymentResultScreen
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.ItemBuyNow
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.navigationBarItems

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostContainer(navController: NavHostController) {
    val lazyListState = rememberLazyListState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoute.LOGIN

    val noHeaderNavScreens = listOf(NavRoute.LOGIN)

    NavHost(
        navController = navController,
        startDestination = NavRoute.LOGIN
    ) {
        composable(NavRoute.LOGIN) {
            LoginScreen(navController) // navigate("home") sẽ tìm được
        }
        composable(NavRoute.HOME) {
            val lazyListState = rememberLazyListState() // Tạo state ở đây để truyền vào cả 2

            HeaderNavScaffold(
                lazyListState = lazyListState,
                navController = navController
            ) { paddingValues ->
                // Nội dung bên trong Scaffold
                HomeScreen(
                    lazyListState = lazyListState,
                    navController = navController,
                    paddingValues = paddingValues // Truyền padding xuống
                )
            }
        }
        composable(
            route = NavRoute.SEARCH,
            arguments = listOf(navArgument("initialQuery") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            // Lấy tham số ra từ backStackEntry
            val initialTab = backStackEntry.arguments?.getInt("tab")
            HeaderNavScaffold(
                lazyListState = lazyListState,
                navController = navController
            ) {
                SearchScreen(
                    navController = navController,
                    paddingValues = it,
                    initialTab = initialTab // Truyền vào màn hình
                )
            }
        }
        composable(NavRoute.PROFILE) {

            HeaderNavScaffold(lazyListState, navController) { paddingValue ->
                ProfileScreen(
                    lazyListState = lazyListState,
                    paddingValues = paddingValue,
                    navController = navController
                )
            }
        }
        composable(NavRoute.MyPurchasesWithTab,
            arguments = listOf(navArgument("tab") { type = NavType.StringType })
        ) {

            HeaderNavScaffold(lazyListState, navController) {
                MyPurchaseScreen(navController, it)
            }
        }
        composable(
            NavRoute.CourseDetailWithArgs,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) {
            CourseDetailScreen(
                navController
            )
        }

        composable(
            NavRoute.ORDER_HISTORY
        )
        {
            OrderScreen(navController)
        }


        composable(
            NavRoute.LearningCourseWithArgs,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) {
            LearningCourseScreen(navController)
        }
        composable(
            NavRoute.LearningBookWithArgs,
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) {
            LearningBookScreen(navController)
        }

        composable(
            NavRoute.BookDetailWithArgs,
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) {
            BookDetailScreen(
                navController
            )
        }
        composable(NavRoute.FORUM) {
            HeaderNavScaffold(
                lazyListState,
                navController
            ) {
                ForumListScreen(navController, it)
            }

        }
        composable(
            NavRoute.ForumDetailWithArgs,
            arguments = listOf(navArgument("questionId") { type = NavType.StringType })
        ) { backStackEntry ->
            ForumDetailScreen(navController)
        }
        composable(NavRoute.FORUM_ASK) {
            ForumAskScreen(navController)
        }
        composable(NavRoute.FORUM_BOOKMARKS) {
            ForumBookmarksScreen(navController)
        }
        composable(NavRoute.NOTIFICATIONS) {
            NotificationScreen(navController)
        }

        composable(NavRoute.EXPLORE_BOOK) {
            ExploreBookScreen(navController)
        }
        composable(NavRoute.EXPLORE_COURSE) {
            ExploreCourseScreen(navController)
        }

        composable(
            NavRoute.CategoryItemsWithArgs,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            CategoryItemsScreen(
                categoryId = categoryId,
                categoryName = categoryName,
                navController = navController
            )
        }

        // Cart
        composable(NavRoute.CART) {
            CartScreen(navController)
        }

        // Checkout
        composable(NavRoute.CHECKOUT) {
            CheckoutScreen(navController)
        }

        composable<ItemBuyNow>{
            val args = it.toRoute<ItemBuyNow>()
            CheckoutScreen(navController, args)
        }

        composable(
            NavRoute.AssignmentWithArgs,
            arguments = listOf(
                navArgument("assignmentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            AssignmentScreen(navController)
        }


        // Payment Result
        composable(
            route = NavRoute.PaymentResultWithArgs,
            arguments = listOf(
                navArgument("success") { type = NavType.BoolType },
                navArgument("orderCode") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val success = backStackEntry.arguments?.getBoolean("success") ?: false
            val orderCode = backStackEntry.arguments?.getString("orderCode")
            PaymentResultScreen(
                navController = navController,
                success = success,
                orderCode = orderCode
            )
        }

        // Payment Processing (Browser + Polling)
        composable(
            route = NavRoute.PaymentProcessingWithArgs,
            arguments = listOf(
                navArgument("paymentUrl") { type = NavType.StringType },
                navArgument("orderCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val paymentUrl = backStackEntry.arguments?.getString("paymentUrl") ?: ""
            val orderCode = backStackEntry.arguments?.getString("orderCode") ?: ""
            com.example.khoitriso.ui.payment.PaymentProcessingScreen(
                navController = navController,
                paymentUrl = java.net.URLDecoder.decode(paymentUrl, "UTF-8"),
                orderCode = orderCode
            )
        }
    }
}


@Composable
fun HeaderNavScaffold(
    lazyListState: LazyListState,
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit = {}, // Sửa: Truyền PaddingValues xuống
) {


    var previousScroll by remember { mutableIntStateOf(0) } // Dùng mutableIntStateOf cho primitive
    var headerVisible by remember { mutableStateOf(true) }

    val headerHeight = Constants.HEADER_HEIGHT.dp
    val navHeight = Constants.NAV_HEIGHT.dp
    val scrollThreshold = Constants.THRESHOLD_SCROLL

    // Logic detect scroll giữ nguyên
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val currentScroll = index * 10000 + offset
                val diff = currentScroll - previousScroll

                // Chỉ đổi trạng thái nếu scroll đủ nhiều để tránh nháy
                if (Math.abs(diff) > scrollThreshold) {
                    headerVisible = diff < 0 // Lướt lên (diff âm) -> Hiện, Lướt xuống -> Ẩn
                }
                previousScroll = currentScroll
            }
    }

    // Animation cho Header Y offset
    val headerOffset by animateDpAsState(
        targetValue = if (headerVisible) 0.dp else -headerHeight,
        animationSpec = tween(300), label = "headerOffset"
    )

    val topPadding by animateDpAsState(
        targetValue = if (headerVisible) headerHeight else 0.dp,
        animationSpec = tween(300), label = "topPadding"
    )

    Box(Modifier.fillMaxSize()) {
        // 1. Content
        // Truyền padding vào để screen con tự xử lý (quan trọng cho LazyColumn)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            content(
                PaddingValues(
                    top = topPadding, // Padding top động
                    bottom = navHeight // Padding bottom cố định cho Nav
                )
            )
        }

        // 2. Header (Global) - Luôn nằm trên cùng
        HeaderScreen(
            displayName = "KhoiTriSo", // Tên App ngắn gọn
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .offset(y = headerOffset)
                .background(MaterialTheme.colorScheme.surface) // Cần background để che content khi lướt qua
                .zIndex(1f) // Đảm bảo luôn nổi lên trên
        )

        // 3. Nav bar (Bottom)
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .height(navHeight)
                .zIndex(1f)
        ) {
            MyNavigationBar(navController)
        }
    }
}


@Composable
fun HeaderScreen(
    displayName: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Spacer(modifier = Modifier.width(12.dp))

        // Display Name
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            modifier = Modifier,
            onClick = { navController.navigate(NavRoute.CART) }
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Cart",
                tint = Color.Black
            )
        }
        // Notification button
        IconButton(
            modifier = Modifier,
            onClick = { navController.navigate(NavRoute.NOTIFICATIONS) }
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notification",
                tint = Color.Black
            )
        }

    }
}

@Composable
fun MyNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
    ) {

        navigationBarItems.forEach { item ->
            val isSelected = currentRoute == item.label

            NavigationBarItem(
                selected = isSelected,
                onClick = {

                    if (!isSelected) {
                        navController.navigate(item.label) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
            )
        }
    }
}
