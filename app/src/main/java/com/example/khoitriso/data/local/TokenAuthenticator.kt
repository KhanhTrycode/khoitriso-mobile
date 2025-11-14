package com.example.khoitriso.data.local

import com.example.khoitriso.data.api.AuthApi
import kotlinx.coroutines.CompletableDeferred
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

    private val lock = Mutex()
    private var refreshDeferred: CompletableDeferred<String?>? = null

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking(Dispatchers.IO) {
            val newToken = getUpdatedToken()
            newToken?.let {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $it")
                    .build()
            }
        }
    }

    private suspend fun getUpdatedToken(): String? {
        // Không chạy refresh trong lock, chỉ tạo deferred trong lock
        val deferred = lock.withLock {
            if (refreshDeferred?.isCompleted != false) {
                refreshDeferred = CompletableDeferred()
                refreshDeferred
            } else {
                refreshDeferred
            }
        }

        // Chỉ 1 coroutine sẽ refresh
        if (deferred?.isActive == true && deferred.getCompletedOrNull() == null) {
            val token = refreshToken()
            deferred.complete(token)
        }

        return deferred?.await()
    }

    private suspend fun refreshToken(): String? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null
        return try {
            val response = authApi.refreshToken(refreshToken, "Bearer $refreshToken")
            if (response.isSuccessful) {
                val newAccessToken = response.body()?.Result?.Token ?: return null
                val newRefreshToken = response.body()?.Result?.RefreshToken ?: return null
                tokenManager.saveTokens(newAccessToken, newRefreshToken)
                newAccessToken
            } else {
                tokenManager.handleExpiredRefreshToken()
                null
            }
        } catch (e: Exception) {
            return ""

        }
    }

    private fun CompletableDeferred<String?>.getCompletedOrNull(): String? {
        return if (isCompleted) getCompleted() else null
    }
}