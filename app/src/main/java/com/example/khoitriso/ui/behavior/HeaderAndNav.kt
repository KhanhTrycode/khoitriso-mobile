package com.example.khoitriso.ui.behavior

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.khoitriso.R
import com.example.khoitriso.ui.detail.CourseDetailScreen
import com.example.khoitriso.ui.homescreen.HomeScreen
import com.example.khoitriso.ui.learningpath.LearningPathScreen
import com.example.khoitriso.ui.loginscreen.LoginScreen
import com.example.khoitriso.ui.profilescreen.ProfileScreen
import com.example.khoitriso.ui.searchscreen.SearchScreen
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.NavRoute
import com.example.khoitriso.utils.navigationBarItems

@Composable
fun NavHostContainer(navController: NavHostController) {
    val lazyListState = rememberLazyListState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoute.login

    val noHeaderNavScreens = listOf(NavRoute.login)
    val showHeaderNav = currentRoute !in noHeaderNavScreens

    NavHost(
        navController = navController,
        startDestination = NavRoute.login
    ) {
        composable(NavRoute.login) {
            LoginScreen(navController) // navigate("home") sẽ tìm được
        }
        composable(NavRoute.home) {

            HeaderNavScaffold(lazyListState, navController) {
                HomeScreen(lazyListState, navController = navController)
            }
        }
        composable(NavRoute.search) {

            HeaderNavScaffold(lazyListState, navController) {
                SearchScreen(lazyListState)
            }
        }
        composable(NavRoute.profile) {

            HeaderNavScaffold(lazyListState, navController) {
                ProfileScreen(lazyListState)
            }
        }
        composable(NavRoute.learningPath) {

            HeaderNavScaffold(lazyListState, navController) {
                LearningPathScreen(lazyListState)
            }
        }
        composable(
            NavRoute.CourseDetailWithArgs,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) {
            CourseDetailScreen(
                onBack = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}


@SuppressLint("FrequentlyChangingValue")
@Composable
fun HeaderNavScaffold(
    lazyListState: LazyListState,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    var previousScroll by remember { mutableStateOf(0) }
    var headerVisible by remember { mutableStateOf(true) }

    val headerHeight = Constants.HEADER_HEIGHT.dp
    val navHeight = Constants.NAV_HEIGHT.dp
    val scrollThreshold = Constants.THRESHOLD_SCROLL

    // detect scroll
    LaunchedEffect(
        lazyListState.firstVisibleItemIndex,
        lazyListState.firstVisibleItemScrollOffset
    ) {
        val currentScroll =
            lazyListState.firstVisibleItemIndex * 10000 +
                    lazyListState.firstVisibleItemScrollOffset
        val diff = currentScroll - previousScroll
        headerVisible = when {
            diff > scrollThreshold -> false
            diff < -scrollThreshold -> true
            else -> headerVisible
        }
        previousScroll = currentScroll
    }

    val headerOffset by animateDpAsState(
        targetValue = if (headerVisible) 0.dp else -headerHeight,
        animationSpec = tween(300)
    )

    Box(Modifier.fillMaxSize()) {
        // Content
        Box(
            Modifier
                .fillMaxSize()
                .padding(
                    top = if (headerVisible) headerHeight else 0.dp,
                    bottom = navHeight
                )
        ) {
            content()
        }

        // Header
        HeaderScreen(
            avatarUrl = R.drawable.ic_launcher_background,
            displayName = "KhoiTriSo",
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .offset(y = headerOffset)
        )

        // Nav bar ở bottom
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .height(navHeight)
        ) {
            MyNavigationBar(navController)
        }
    }
}


@Composable
fun HeaderScreen(
    avatarUrl: Int,
    displayName: String,
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
        // Avatar
        Image(
            painter = painterResource(id = avatarUrl),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Display Name
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        IconButton(modifier = Modifier, onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Cart",
                tint = Color.Black

            )
        }
        // Notification button
        IconButton(modifier = Modifier, onClick = { }) {
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
                    navController.navigate(item.label) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
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
