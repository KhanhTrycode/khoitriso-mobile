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


class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi
) : Authenticator {

    private val lock = Mutex()
    private lateinit var refreshDeferred: CompletableDeferred<String?>

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking(Dispatchers.IO) { // Blocks only inside authenticate()
            val newToken = getUpdatedToken()
            newToken?.let {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $it")
                    .build()
            }
        }
    }

    private suspend fun getUpdatedToken(): String? {
        lock.withLock {
            if (!::refreshDeferred.isInitialized || refreshDeferred.isCompleted) {
                refreshDeferred = CompletableDeferred()
                refreshDeferred.complete(refreshToken()) // Start refresh
            }
        }
        return refreshDeferred.await() // All requests will wait for the refreshed token
    }

    private suspend fun refreshToken(): String? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null
        return try {
            val response = authApi.refreshToken(refreshToken)
            if (response.isSuccessful) {
                val newAccessToken = response.body()?.result?.token ?: return null
                val newRefreshToken = response.body()?.result?.token ?: return null
                tokenManager.saveTokens(newAccessToken, newRefreshToken)
                newAccessToken
            } else {
                tokenManager.handleExpiredRefreshToken()
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            lock.withLock { refreshDeferred.complete(null) } // Ensure waiting calls get a result
        }
    }
}