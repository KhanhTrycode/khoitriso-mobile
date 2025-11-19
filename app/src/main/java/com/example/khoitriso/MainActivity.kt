package com.example.khoitriso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.khoitriso.ui.behavior.NavHostContainer
import com.example.khoitriso.ui.theme.KhoiTriSoTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KhoiTriSoTheme {
                MyApp()
            }
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




