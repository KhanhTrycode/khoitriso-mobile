package com.example.khoitriso.ui.loginscreen

import android.R
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUsecase: AuthUsecase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _token = MutableStateFlow<Authorization?>(null)
    val token: StateFlow<Authorization?> = _token
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // Launcher để Compose truyền vào từ Activity/Composable
    private var googleSignInLauncher: ActivityResultLauncher<Intent>? = null

    fun setGoogleSignInLauncher(launcher: ActivityResultLauncher<Intent>) {
        googleSignInLauncher = launcher
    }

    fun startGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(Constants.GOOGLE_CLIENT_ID)
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher?.launch(signInIntent)
    }

    fun handleGoogleSignInResult(data: Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken ?: return
            viewModelScope.launch {
                isLoading.value = true
                try {
                    authUsecase.authGoogleSDK(idToken, tokenManager)
                } catch (e: Exception) {
                    errorMessage.value = e.message
                } finally {
                    isLoading.value = false
                }
            }
        } catch (e: ApiException) {
            errorMessage.value = e.message
        }
    }

    // Placeholder cho Facebook Sign-In
    fun startFacebookSignIn() {
        // TODO: implement Facebook login flow
        errorMessage.value = "Facebook Sign-In chưa implement"
    }

    fun authGoogleSDK(id: String) {

        viewModelScope.launch {
            _token.value = authUsecase.authGoogleSDK(id, tokenManager)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            authUsecase.refresh(tokenManager)
        }
    }


}