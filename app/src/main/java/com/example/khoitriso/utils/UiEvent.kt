package com.example.khoitriso.utils

interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    object NavigateBack : UiEvent
}