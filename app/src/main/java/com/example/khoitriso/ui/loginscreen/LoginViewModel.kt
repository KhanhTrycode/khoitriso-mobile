package com.example.khoitriso.ui.loginscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.UiState
import com.example.khoitriso.utils.debug
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUsecase: AuthUsecase,
    private val tokenManager: TokenManager,
    private val userManager: com.example.khoitriso.data.local.UserManager,
) : ViewModel() {

    private val _token = MutableStateFlow<UiState<Authorization>?>(null)
    val token: StateFlow<UiState<Authorization>?> = _token

    val errorMessage = mutableStateOf<String?>(null)

    private var googleSignInLauncher: ActivityResultLauncher<Intent>? = null

    init {
        // Check if user is already logged in
        checkExistingToken()
    }

    private suspend fun clearAllAuthData() {
        tokenManager.clearTokens()
        userManager.clearUser()
    }

    private fun checkExistingToken() {
        viewModelScope.launch {
            val accessToken = tokenManager.getAccessToken()
            if (accessToken == null) {
                // No token, need to login
                _token.value = null
                return@launch
            }

            // Check if token is expired
            if (tokenManager.isTokenExpired(accessToken)) {
                // Token expired, try to refresh
                val refreshToken = tokenManager.getRefreshToken()
                if (refreshToken != null) {
                    val refreshed = authUsecase.refresh(tokenManager)
                    if (refreshed != null) {
                        // Save refreshed tokens
                        tokenManager.saveTokens(refreshed.accessToken, refreshed.refresh)
                        // Verify by loading user
                        val userResult = authUsecase.getMe()
                        userResult.fold(
                            onSuccess = { user ->
                                // Token is valid, save user and set success state
                                authUsecase.loadCurrentUserInfo.saveUser(user)
                                _token.value = UiState.Success(refreshed)
                            },
                            onFailure = {
                                // Failed to get user, clear tokens and require login
                                clearAllAuthData()
                                _token.value = null
                            }
                        )
                    } else {
                        // Refresh failed, clear tokens
                        clearAllAuthData()
                        _token.value = null
                    }
                } else {
                    // No refresh token, clear and require login
                    clearAllAuthData()
                    _token.value = null
                }
            } else {
                // Token not expired, verify it by calling /auth/me
                val userResult = authUsecase.getMe()
                userResult.fold(
                    onSuccess = { user ->
                        // Token is valid, save user and set success state
                        authUsecase.loadCurrentUserInfo.saveUser(user)
                        val refreshToken = tokenManager.getRefreshToken()
                        if (refreshToken != null) {
                            _token.value = UiState.Success(
                                Authorization(
                                    accessToken = accessToken,
                                    refresh = refreshToken
                                )
                            )
                        } else {
                            // No refresh token, require login
                            clearAllAuthData()
                            _token.value = null
                        }
                    },
                    onFailure = {
                        // Token is invalid or API call failed, try to refresh
                        val refreshToken = tokenManager.getRefreshToken()
                        if (refreshToken != null) {
                            val refreshed = authUsecase.refresh(tokenManager)
                            if (refreshed != null) {
                                tokenManager.saveTokens(refreshed.accessToken, refreshed.refresh)
                                // Try to get user again with new token
                                val retryUserResult = authUsecase.getMe()
                                retryUserResult.fold(
                                    onSuccess = { user ->
                                        authUsecase.loadCurrentUserInfo.saveUser(user)
                                        _token.value = UiState.Success(refreshed)
                                    },
                                    onFailure = {
                                        // Still failed, clear tokens
                                        clearAllAuthData()
                                        _token.value = null
                                    }
                                )
                            } else {
                                // Refresh failed, clear tokens
                                clearAllAuthData()
                                _token.value = null
                            }
                        } else {
                            // No refresh token, clear and require login
                            clearAllAuthData()
                            _token.value = null
                        }
                    }
                )
            }
        }
    }

    fun setGoogleSignInLauncher(launcher: ActivityResultLauncher<Intent>) {
        googleSignInLauncher = launcher
    }



    fun startGoogleSignIn(context: Context) {
        debug("Sign In Button", "LoginViewModel")
        _token.value = UiState.Loading

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(Constants.GOOGLE_CLIENT_ID)
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher?.launch(signInIntent)


    }

    fun handleGoogleSignInResult(data: Intent?) {
        debug(("handleGoogle"),"LoginViewModel")
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken ?: return
            debug("$idToken", "LoginViewModel")
            viewModelScope.launch {
                val result = authUsecase.authGoogleSDK(idToken)
                result.fold(
                    onSuccess = {
                        _token.value = UiState.Success(it)
                        tokenManager.saveTokens(it.accessToken, it.refresh)
                    },
                    onFailure = {
                        _token.value = UiState.Error(
                            it.message ?: "An unknown error occurred"
                        ) // Đăng nhập thất bại
                    }
                )
            }
        } catch (e: ApiException) {
            errorMessage.value = e.message
            _token.value = UiState.Error(e.message ?: "Google Sign-in failed") // Google SDK lỗi
        }
    }

    // Placeholder cho Facebook Sign-In
    fun startFacebookSignIn() {
        errorMessage.value = "Facebook Sign-In chưa implement"
    }

//    fun refresh() {
//        viewModelScope.launch {
//            authUsecase.refresh(tokenManager)
//        }
//    }
}
