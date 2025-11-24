package com.example.khoitriso.data.signalr

import android.util.Log
import com.example.khoitriso.data.dto.NotificationDto
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRService @Inject constructor(
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    private var hubConnection: HubConnection? = null
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _notifications = MutableStateFlow<NotificationDto?>(null)
    val notifications: StateFlow<NotificationDto?> = _notifications.asStateFlow()
    
    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        object Connected : ConnectionState()
        object Reconnecting : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }
    
    suspend fun startConnection() {
        try {
            val accessToken = tokenManager.getAccessToken()
            if (accessToken == null) {
                _connectionState.value = ConnectionState.Error("No access token")
                Log.w("SignalR", "No access token available")
                return
            }
            
            // Backend reads token from query string
            val hubUrl = "${Constants.BASE_API_URL}/notificationHub?access_token=$accessToken"
            Log.d("SignalR", "Connecting to: $hubUrl")
            
            hubConnection = HubConnectionBuilder.create(hubUrl)
                .withAutomaticReconnect()
                .build()
            
            // Listen for notifications from server
            hubConnection?.on("ReceiveNotification", { notification ->
                Log.d("SignalR", "Received notification: $notification")
                try {
                    // Parse notification from Map to NotificationDto
                    val notificationDto = parseNotification(notification)
                    notificationDto?.let {
                        _notifications.value = it
                        Log.d("SignalR", "Parsed notification: ${it.Title}")
                    }
                } catch (e: Exception) {
                    Log.e("SignalR", "Error parsing notification", e)
                }
            }, Object::class.java)
            
            // Connection event handlers - using reflection or try-catch for compatibility
            try {
                hubConnection?.onClosed { error ->
                    Log.d("SignalR", "Connection closed: $error")
                    _connectionState.value = ConnectionState.Disconnected
                }
            } catch (e: Exception) {
                Log.w("SignalR", "onClosed not available: ${e.message}")
            }
            
            try {
                // Note: Java SignalR client may have different method names
                // We'll handle connection state through connectionState property
            } catch (e: Exception) {
                Log.w("SignalR", "Reconnection handlers not available: ${e.message}")
            }
            
            // Monitor connection state periodically
            CoroutineScope(Dispatchers.IO).launch {
                while (hubConnection != null) {
                    delay(1000) // Check every second
                    val state = hubConnection?.connectionState
                    when (state) {
                        HubConnectionState.CONNECTED -> {
                            if (_connectionState.value !is ConnectionState.Connected) {
                                _connectionState.value = ConnectionState.Connected
                            }
                        }
                        HubConnectionState.DISCONNECTED -> {
                            if (_connectionState.value !is ConnectionState.Disconnected) {
                                _connectionState.value = ConnectionState.Disconnected
                            }
                        }
                        HubConnectionState.CONNECTING -> {
                            if (_connectionState.value !is ConnectionState.Connecting) {
                                _connectionState.value = ConnectionState.Connecting
                            }
                        }
                        HubConnectionState.RECONNECTING -> {
                            if (_connectionState.value !is ConnectionState.Reconnecting) {
                                _connectionState.value = ConnectionState.Reconnecting
                            }
                        }
                        else -> {}
                    }
                }
            }
            
            _connectionState.value = ConnectionState.Connecting
            
            hubConnection?.start()?.blockingAwait()
            
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                _connectionState.value = ConnectionState.Connected
                Log.d("SignalR", "Connected to SignalR Hub")
            } else {
                _connectionState.value = ConnectionState.Error("Failed to connect")
            }
            
        } catch (e: Exception) {
            Log.e("SignalR", "Error starting connection", e)
            _connectionState.value = ConnectionState.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun stopConnection() {
        try {
            hubConnection?.stop()?.blockingAwait()
            hubConnection = null
            _connectionState.value = ConnectionState.Disconnected
            Log.d("SignalR", "Disconnected from SignalR Hub")
        } catch (e: Exception) {
            Log.e("SignalR", "Error stopping connection", e)
        }
    }
    
    // Join into group (e.g., course, lesson)
    suspend fun joinGroup(groupName: String) {
        try {
            hubConnection?.invoke("JoinGroup", groupName)?.blockingAwait()
            Log.d("SignalR", "Joined group: $groupName")
        } catch (e: Exception) {
            Log.e("SignalR", "Error joining group", e)
        }
    }
    
    // Leave group
    suspend fun leaveGroup(groupName: String) {
        try {
            hubConnection?.invoke("LeaveGroup", groupName)?.blockingAwait()
            Log.d("SignalR", "Left group: $groupName")
        } catch (e: Exception) {
            Log.e("SignalR", "Error leaving group", e)
        }
    }
    
    private fun parseNotification(data: Any?): NotificationDto? {
        return try {
            when (data) {
                is Map<*, *> -> {
                    // Convert Map to NotificationDto using Gson
                    val json = gson.toJson(data)
                    gson.fromJson(json, NotificationDto::class.java)
                }
                is String -> {
                    // Parse JSON string
                    gson.fromJson(data, NotificationDto::class.java)
                }
                else -> null
            }
        } catch (e: Exception) {
            Log.e("SignalR", "Error parsing notification data", e)
            null
        }
    }
}
