package com.example.khoitriso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.khoitriso.ui.loginscreen.LoginScreen
import com.example.khoitriso.ui.theme.KhoiTriSoTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.composable
import com.example.khoitriso.ui.homescreen.HomeScreen
import com.example.khoitriso.ui.homescreen.PreviewScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
                Nav()

        }
    }
}

@Composable
fun Nav(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "loginScreen"
    ){
        composable("loginScreen"){
            LoginScreen(navController)
        }
        composable("homeScreen"){
            PreviewScreen()
        }

    }
}
