package com.example.khoitriso.data.local

import com.example.khoitriso.data.api.AuthApi
import com.example.khoitriso.utils.debug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi
) : Authenticator {

    // Mutex để đảm bảo chỉ 1 luồng được refresh tại 1 thời điểm
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        debug("Authenticate", "TokenAuthenticator: 401 Detected")

        // Authenticator chạy trên background thread của OkHttp, nhưng là hàm đồng bộ.
        // Ta dùng runBlocking để chờ code coroutine chạy xong.
        return runBlocking(Dispatchers.IO) {
            val currentToken = tokenManager.getAccessToken() // Lấy cực nhanh từ RAM

            // 1. KIỂM TRA SƠ BỘ:
            // Lấy token đang nằm trong request bị lỗi (header cũ)
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")

            // Nếu token trong RAM khác với token trong request lỗi,
            // chứng tỏ luồng khác đã refresh xong rồi. Ta chỉ cần lấy token mới xài luôn.
            if (currentToken != null && currentToken != requestToken) {
                return@runBlocking response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // 2. VÀO KHÓA (CRITICAL SECTION):
            // Nếu token vẫn cũ, ta cần refresh. Dùng mutex để chặn các luồng khác đợi.
            mutex.withLock {
                // 3. KIỂM TRA KỸ (DOUBLE-CHECK):
                // Sau khi chờ khóa mở, có thể luồng trước đó đã refresh xong rồi.
                // Kiểm tra lại lần nữa cho chắc.
                val updatedToken = tokenManager.getAccessToken()
                if (updatedToken != null && updatedToken != requestToken) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $updatedToken")
                        .build()
                }

                // 4. THỰC SỰ REFRESH:
                // Bây giờ chắc chắn là cần refresh mới.
                val newAccessToken = getNewToken()

                if (newAccessToken != null) {
                    // Thành công -> Retry request với token mới
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    // Thất bại -> Return null để Retrofit ngừng retry (tránh lặp vô tận)
                    return@withLock null
                }
            }
        }
    }

    private suspend fun getNewToken(): String? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        return try {
            val response = authApi.refreshToken(refreshToken, "Bearer $refreshToken")

            if (response.isSuccessful && response.body()?.Result != null) {
                val result = response.body()!!.Result
                val newAccess = result?.Token
                val newRefresh = result?.RefreshToken

                if (newAccess != null && newRefresh != null) {
                    // Lưu vào TokenManager (nó sẽ tự update RAM và Disk)
                    tokenManager.saveTokens(newAccess, newRefresh)
                    return newAccess
                }
            }

            // Nếu refresh thất bại (Refresh token hết hạn hoặc lỗi server 400/403)
            // Xóa token đi để user bị logout
            tokenManager.clearTokens()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null // Quan trọng: Return null để không retry
        }
    }
}