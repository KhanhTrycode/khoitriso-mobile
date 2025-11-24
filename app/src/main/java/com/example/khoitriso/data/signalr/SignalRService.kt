package com.example.khoitriso.data.signalr

import android.util.Log
import com.example.khoitriso.data.dto.NotificationDto
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.utils.Constants
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRService @Inject constructor(
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    private var hubConnection: HubConnection? = null
    private val isManuallyStopping = AtomicBoolean(false) // Cờ để kiểm tra nếu người dùng chủ động dừng
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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

    fun startConnection() {
        // Tránh khởi động lại nếu đang kết nối hoặc đã kết nối
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED || hubConnection?.connectionState == HubConnectionState.CONNECTING) {
            return
        }

        serviceScope.launch {
            isManuallyStopping.set(false)
            connect()
        }
    }

    private suspend fun connect() {
        try {
            val accessToken = tokenManager.getAccessToken()
            if (accessToken == null) {
                _connectionState.value = ConnectionState.Error("No access token")
                Log.w("SignalR", "No access token available")
                return
            }

            val hubUrl = "${Constants.BASE_API_URL}/notificationHub?access_token=$accessToken"
            Log.d("SignalR", "Connecting to: $hubUrl")

            // Bỏ .withAutomaticReconnect() vì nó không tồn tại
            hubConnection = HubConnectionBuilder.create(hubUrl)
                .build()

            // Lắng nghe sự kiện mất kết nối để tự kết nối lại
            hubConnection?.onClosed { error ->
                Log.w("SignalR", "Connection closed. Error: $error")

                // Chỉ kết nối lại nếu không phải do người dùng chủ động dừng
                if (!isManuallyStopping.get()) {
                    serviceScope.launch {
                        reconnect()
                    }
                } else {
                    _connectionState.value = ConnectionState.Disconnected
                }
            }

            // Đăng ký các sự kiện từ server
            registerHubEvents()

            // Bắt đầu kết nối
            _connectionState.value = ConnectionState.Connecting
            hubConnection?.start()?.blockingAwait()

            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                _connectionState.value = ConnectionState.Connected
                Log.d("SignalR", "Connected to SignalR Hub. Connection ID: ${hubConnection?.connectionId}")
            } else {
                _connectionState.value = ConnectionState.Error("Failed to connect initially")
            }

        } catch (e: Exception) {
            Log.e("SignalR", "Error starting connection", e)
            _connectionState.value = ConnectionState.Error(e.message ?: "Unknown error")
            // Thử kết nối lại sau một khoảng thời gian nếu có lỗi
            if (!isManuallyStopping.get()) {
                serviceScope.launch { reconnect() }
            }
        }
    }

    // Hàm để xử lý logic kết nối lại
    private suspend fun reconnect() {
        _connectionState.value = ConnectionState.Reconnecting
        Log.d("SignalR", "Attempting to reconnect...")

        val retryDelays = longArrayOf(2000, 5000, 10000, 30000) // Thời gian chờ: 2s, 5s, 10s, 30s
        for (delayMs in retryDelays) {
            delay(delayMs)
            Log.d("SignalR", "Retrying connection after ${delayMs}ms...")
            try {
                // Phải tạo lại connection object với token mới nếu cần
                // Nhưng ở đây ta thử start lại connection cũ trước
                hubConnection?.start()?.blockingAwait()
                if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                    _connectionState.value = ConnectionState.Connected
                    Log.i("SignalR", "Reconnected successfully!")
                    return // Thoát khỏi vòng lặp khi kết nối thành công
                }
            } catch (e: Exception) {
                Log.e("SignalR", "Reconnect attempt failed", e)
            }
        }

        Log.e("SignalR", "Could not reconnect after all retries.")
        _connectionState.value = ConnectionState.Disconnected
    }


    private fun registerHubEvents() {
        hubConnection?.on("ReceiveNotification", { notification ->
            Log.d("SignalR", "Received notification: $notification")
            try {
                val notificationDto = parseNotification(notification)
                notificationDto?.let {
                    _notifications.value = it
                    Log.d("SignalR", "Parsed notification: ${it.Title}")
                }
            } catch (e: Exception) {
                Log.e("SignalR", "Error parsing notification", e)
            }
        }, Object::class.java)
    }

    fun stopConnection() {
        serviceScope.launch {
            if (hubConnection != null) {
                isManuallyStopping.set(true)
                try {
                    hubConnection?.stop()?.blockingAwait()
                    Log.d("SignalR", "Disconnected from SignalR Hub")
                } catch (e: Exception) {
                    Log.e("SignalR", "Error stopping connection", e)
                } finally {
                    hubConnection = null
                    _connectionState.value = ConnectionState.Disconnected
                }
            }
        }
    }

    // ... các hàm còn lại giữ nguyên

    suspend fun joinGroup(groupName: String) {
        if(hubConnection?.connectionState != HubConnectionState.CONNECTED) return
        try {
            hubConnection?.invoke("JoinGroup", groupName)?.blockingAwait()
            Log.d("SignalR", "Joined group: $groupName")
        } catch (e: Exception) {
            Log.e("SignalR", "Error joining group", e)
        }
    }

    suspend fun leaveGroup(groupName: String) {
        if(hubConnection?.connectionState != HubConnectionState.CONNECTED) return
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
                    val json = gson.toJson(data)
                    gson.fromJson(json, NotificationDto::class.java)
                }
                is String -> {
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
