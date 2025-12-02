package com.example.khoitriso.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.khoitriso.utils.debug
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

// Định nghĩa DataStore (nên để top-level hoặc trong file module)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_tokens")

@Singleton // Quan trọng: Đảm bảo chỉ có 1 instance tồn tại
class TokenManager @Inject constructor(@ApplicationContext val context: Context) {

    private val dataStore = context.dataStore

    // Scope riêng cho TokenManager để chạy các tác vụ nền
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Cache trên RAM: AtomicReference đảm bảo an toàn khi đọc/ghi từ nhiều luồng
    private val cachedAccessToken = AtomicReference<String?>(null)
    private val cachedRefreshToken = AtomicReference<String?>(null)

    // Key định nghĩa sẵn để tránh gõ sai
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    init {
        // TỰ ĐỘNG LẮNG NGHE: Ngay khi class được khởi tạo, nó sẽ theo dõi DataStore
        // Nếu DataStore thay đổi, cache trên RAM sẽ tự cập nhật theo.
        scope.launch {
            dataStore.data.map { preferences ->
                preferences[KEY_ACCESS_TOKEN]
            }.collect { token ->
                cachedAccessToken.set(token)
            }
        }

        scope.launch {
            dataStore.data.map { preferences ->
                preferences[KEY_REFRESH_TOKEN]
            }.collect { token ->
                cachedRefreshToken.set(token)
            }
        }
    }


    fun getAccessToken(): String? {
        // 1. Thử lấy từ RAM trước (Cực nhanh)
        val ramToken = cachedAccessToken.get()
        if (ramToken != null) return ramToken

        // 2. Nếu RAM null (có thể do vừa mở app chưa load kịp),
        // ta dùng runBlocking để ép đọc từ DataStore NGAY LẬP TỨC.
        // Lưu ý: Chỉ Interceptor mới nên dùng cách này vì nó chạy trên Background Thread.
        return runBlocking {
            val diskToken = dataStore.data.first()[KEY_ACCESS_TOKEN]
            debug("TokenManager: $diskToken", "TokenManager")
            // Cập nhật ngược lại vào RAM để lần sau không phải đọc disk nữa
            if (diskToken != null) {
                cachedAccessToken.set(diskToken)
            }
            diskToken
        }
    }

    fun getRefreshToken(): String? {
        val ramToken = cachedRefreshToken.get()
        if (ramToken != null) return ramToken

        return runBlocking {
            val diskToken = dataStore.data.first()[KEY_REFRESH_TOKEN]
            if (diskToken != null) cachedRefreshToken.set(diskToken)
            diskToken
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        // Lưu vào DataStore (Cache RAM sẽ tự động cập nhật nhờ block init ở trên)
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            debug("TokenManager: $accessToken", "TokenManager")
            preferences[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
        }
        // Cache RAM cũng sẽ tự động về null
    }

    // --- UTILS ---

    fun isTokenExpired(token: String): Boolean {
        return try {
            val decodedJWT: DecodedJWT = JWT.decode(token)
            // Lấy thời gian hết hạn, nếu null coi như hết hạn cho an toàn
            val expiresAt = decodedJWT.expiresAt ?: return true
            // So sánh với thời gian hiện tại
            expiresAt.before(Date())
        } catch (e: Exception) {
            true // Nếu lỗi decode -> coi như hết hạn
        }
    }
}