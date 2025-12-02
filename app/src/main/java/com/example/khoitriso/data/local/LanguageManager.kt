package com.example.khoitriso.data.local

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class AppLanguage(val code: String, val displayName: String) {
    VIETNAMESE("vi", "Tiếng Việt"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: VIETNAMESE
        }
    }
}

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "language_prefs"
        private const val KEY_LANGUAGE = "language_code"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getCurrentLanguage(): AppLanguage {
        val languageCode = sharedPreferences.getString(KEY_LANGUAGE, AppLanguage.VIETNAMESE.code)
        return AppLanguage.fromCode(languageCode ?: AppLanguage.VIETNAMESE.code)
    }

    fun setLanguage(language: AppLanguage) {
        sharedPreferences.edit {
            putString(KEY_LANGUAGE, language.code)
        }
        updateLocale(language)
    }

    fun updateLocale(language: AppLanguage) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun getLocale(): Locale {
        return Locale(getCurrentLanguage().code)
    }
    
    fun getContextWrapper(base: Context): ContextWrapper {
        val locale = getLocale()
        val config = Configuration(base.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            return ContextWrapper(base.createConfigurationContext(config))
        } else {
            @Suppress("DEPRECATION")
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            base.resources.updateConfiguration(config, base.resources.displayMetrics)
            return ContextWrapper(base)
        }
    }
}

