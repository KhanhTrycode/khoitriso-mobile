package com.example.khoitriso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import android.content.Context
import com.example.khoitriso.data.local.LanguageManager
import com.example.khoitriso.data.local.ThemeManager
import com.example.khoitriso.ui.behavior.NavHostContainer
import com.example.khoitriso.ui.theme.KhoiTriSoTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeManager: ThemeManager
    
    @Inject
    lateinit var languageManager: LanguageManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply language on startup
        val currentLanguage = languageManager.getCurrentLanguage()
        languageManager.updateLocale(currentLanguage)
        
        // Apply theme on startup
        val currentTheme = themeManager.getCurrentTheme()
        themeManager.applyTheme(currentTheme)
        
        setContent {
            val darkTheme = when (currentTheme.value) {
                AppCompatDelegate.MODE_NIGHT_YES -> true
                AppCompatDelegate.MODE_NIGHT_NO -> false
                else -> isSystemInDarkTheme()
            }
            
            KhoiTriSoTheme(darkTheme = darkTheme) {
                MyApp()
            }
        }
    }
    
    override fun attachBaseContext(newBase: Context) {
        val languageManager = EntryPointAccessors.fromApplication(
            newBase.applicationContext,
            LanguageEntryPoint::class.java
        ).languageManager()
        super.attachBaseContext(languageManager.getContextWrapper(newBase))
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(SingletonComponent::class)
interface LanguageEntryPoint {
    fun languageManager(): LanguageManager
}


@Composable
fun MyApp() {

    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHostContainer(navController)
    }
}




