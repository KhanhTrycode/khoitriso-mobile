package com.example.khoitriso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.khoitriso.ui.loginscreen.LoginScreen
import com.example.khoitriso.ui.theme.KhoiTriSoTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
                LoginScreen()

        }
    }
}
