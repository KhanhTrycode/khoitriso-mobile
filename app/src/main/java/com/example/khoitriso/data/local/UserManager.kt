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
import kotlinx.coroutines.flow.Flow
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

    // (Cải tiến) Tạo một Flow để quan sát người dùng
    val userFlow: Flow<User?> = dataStore.data.map { prefs ->
        val id = prefs[userIdKey]?.toIntOrNull()
        val fullName = prefs[userFullNameKey]
        val email = prefs[userEmailKey]

        // Chỉ tạo User object nếu các trường bắt buộc tồn tại
        if (id != null && fullName != null && email != null) {
            User(
                id = id,
                fullName = fullName,
                email = email,
                avatar = prefs[userAvatarKey] ?: "",
                authProvider = "", // Có thể lưu và lấy giá trị này từ DataStore nếu cần
                role = 1 // Tương tự, có thể lưu và lấy role
            )
        } else {
            null
        }
    }


    suspend fun saveUser(user: User) {
        lock.withLock {
            dataStore.edit {
                it[userFullNameKey] = user.fullName
                it[userUsernameKey] = user.email.split("@")[0] // Fallback
                it[userAvatarKey] = user.avatar ?: ""
                it[userIdKey] = user.id.toString()
                it[userEmailKey] = user.email
            }
        }
    }

    // Hàm của bạn đã tốt, nhưng giờ nó có thể dùng userFlow
    suspend fun getCurrentUser(): User? {
        // Đọc giá trị một lần từ flow đã được định nghĩa ở trên
        return userFlow.first()
    }

    // Các hàm khác cũng có thể được đơn giản hóa
    suspend fun getUserName(): String? {
        return userFlow.first()?.fullName
    }



    suspend fun getUserAvatar(): String? {
        return userFlow.first()?.avatar
    }


    suspend fun clearUser() {
        lock.withLock {
            dataStore.edit {
                it.clear() // Xóa tất cả các preferences trong DataStore này
            }
        }
    }

    // getUserNameFromToken không thay đổi
    suspend fun getUserNameFromToken(tokenManager: TokenManager): String? {
        val token = tokenManager.getAccessToken() ?: return null
        return try {
            val jwt = JWT.decode(token)
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
