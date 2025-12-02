package com.example.khoitriso.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class AppTheme(val value: Int, val displayName: String) {
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO, "Sáng"),
    DARK(AppCompatDelegate.MODE_NIGHT_YES, "Tối"),
    SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, "Theo hệ thống");

    companion object {
        fun fromValue(value: Int): AppTheme {
            return entries.find { it.value == value } ?: SYSTEM
        }
    }
}

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME = "theme_mode"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getCurrentTheme(): AppTheme {
        val themeValue = sharedPreferences.getInt(KEY_THEME, AppTheme.SYSTEM.value)
        return AppTheme.fromValue(themeValue)
    }

    fun setTheme(theme: AppTheme) {
        sharedPreferences.edit {
            putInt(KEY_THEME, theme.value)
        }
        applyTheme(theme)
    }

    fun applyTheme(theme: AppTheme) {
        AppCompatDelegate.setDefaultNightMode(theme.value)
    }

    init {
        applyTheme(getCurrentTheme())
    }
}

