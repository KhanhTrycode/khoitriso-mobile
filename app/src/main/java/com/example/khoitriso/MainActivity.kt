package com.example.khoitriso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.khoitriso.ui.loginscreen.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.khoitriso.ui.homescreen.HeaderScreen
import com.example.khoitriso.ui.homescreen.HomeScreen
import com.example.khoitriso.ui.homescreen.NavigationBar
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.NavRoute


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}


@Composable
fun MyApp() {

    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHostContainer(navController)
    }
}


@Composable
fun NavHostContainer(navController: androidx.navigation.NavHostController) {
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
            if (showHeaderNav) {
                HeaderNavScaffold(lazyListState) {
                    HomeScreen(lazyListState)
                }
            } else {
                HomeScreen(lazyListState)
            }
        }
    }
}


@Composable
fun HeaderNavScaffold(
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    content: @Composable () -> Unit
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

    val navOffset by animateDpAsState(
        targetValue = if (headerVisible) headerHeight else 0.dp,
        animationSpec = tween(300)
    )

    Box(Modifier.fillMaxSize()) {
        // Content
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = headerHeight + navHeight)
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

        // Nav
        NavigationBar(
            modifier = Modifier
                .height(navHeight)
                .offset(y = navOffset)
        )
    }
}


