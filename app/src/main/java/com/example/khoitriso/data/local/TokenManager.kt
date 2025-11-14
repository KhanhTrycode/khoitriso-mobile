package com.example.khoitriso.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_tokens")


class TokenManager @Inject constructor(@ApplicationContext val context: Context) {
    private val dataStore = context.dataStore
    private val lock = Mutex()
    private val latestAccessToken =
        AtomicReference<String?>(null) // Ensures thread-safe updates to avoid race conditions

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        lock.withLock {
            latestAccessToken.set(accessToken)
            dataStore.edit {
                it[stringPreferencesKey("access_token")] = accessToken
                it[stringPreferencesKey("refresh_token")] = refreshToken
            }
        }
    }

    suspend fun getAccessToken(): String? {
        return lock.withLock {
            latestAccessToken.get() ?: dataStore.data.first()[stringPreferencesKey("access_token")]
        }
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[stringPreferencesKey("refresh_token")]
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val decodedJWT: DecodedJWT = JWT.decode(token)
            decodedJWT.expiresAt?.before(Date()) ?: true
        } catch (e: Exception) {
            true
        }
    }

    suspend fun handleExpiredRefreshToken() {
        lock.withLock {
            dataStore.edit {
                it.remove(stringPreferencesKey("access_token"))
                it.remove(stringPreferencesKey("refresh_token"))
            }
            latestAccessToken.set(null)
        }
        // Redirect user to login screen
    }
}