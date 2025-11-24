package com.example.khoitriso.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.jwt.JWT
import com.example.khoitriso.domain.models.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

@Singleton
class UserManager @Inject constructor(@ApplicationContext val context: Context) {
    private val dataStore = context.userDataStore
    private val lock = Mutex()
    
    private val userFullNameKey = stringPreferencesKey("user_full_name")
    private val userUsernameKey = stringPreferencesKey("user_username")
    private val userAvatarKey = stringPreferencesKey("user_avatar")
    private val userIdKey = stringPreferencesKey("user_id")
    private val userEmailKey = stringPreferencesKey("user_email")

    suspend fun saveUser(user: User) {
        lock.withLock {
            dataStore.edit {
                it[userFullNameKey] = user.fullName
                it[userUsernameKey] = user.email.split("@")[0] // Fallback to email prefix if no username
                it[userAvatarKey] = user.avatar ?: ""
                it[userIdKey] = user.id.toString()
                it[userEmailKey] = user.email
            }
        }
    }

    suspend fun getUserName(): String? {
        return lock.withLock {
            try {
                dataStore.data.first()[userFullNameKey] ?: dataStore.data.first()[userUsernameKey]
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getUserAvatar(): String? {
        return lock.withLock {
            try {
                dataStore.data.first()[userAvatarKey]
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getCurrentUser(): User? {
        return lock.withLock {
            try {
                val prefs = dataStore.data.first()
                val fullName = prefs[userFullNameKey] ?: return@lock null
                val avatar = prefs[userAvatarKey] ?: ""
                val id = prefs[userIdKey]?.toIntOrNull() ?: return@lock null
                val email = prefs[userEmailKey] ?: return@lock null
                val username = prefs[userUsernameKey] ?: email.split("@")[0]
                
                User(
                    id = id,
                    fullName = fullName,
                    email = email,
                    avatar = avatar,
                    authProvider = "",
                    role = 1
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun clearUser() {
        lock.withLock {
            dataStore.edit {
                it.remove(userFullNameKey)
                it.remove(userUsernameKey)
                it.remove(userAvatarKey)
                it.remove(userIdKey)
                it.remove(userEmailKey)
            }
        }
    }

    // Try to get userName from JWT token if available
    suspend fun getUserNameFromToken(tokenManager: TokenManager): String? {
        val token = tokenManager.getAccessToken() ?: return null
        return try {
            val jwt = JWT.decode(token)
            // Try common JWT claim names for name/username
            jwt.getClaim("FullName").asString() 
                ?: jwt.getClaim("fullName").asString()
                ?: jwt.getClaim("Name").asString()
                ?: jwt.getClaim("name").asString()
                ?: jwt.getClaim("Username").asString()
                ?: jwt.getClaim("username").asString()
                ?: jwt.getClaim("Email").asString()?.split("@")?.get(0)
        } catch (e: Exception) {
            null
        }
    }
}

