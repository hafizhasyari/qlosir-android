package com.qlosir.app.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface SplashNavigationEvent {
    data object NavigateToOnboarding : SplashNavigationEvent
    data object NavigateToLogin : SplashNavigationEvent
    data object NavigateToDashboard : SplashNavigationEvent
}

class SplashViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        startSplashTimer()
    }

    private fun startSplashTimer() {
        viewModelScope.launch {
            // Standard splash display duration of 2.5 seconds
            delay(2500L)
            // Navigate to onboarding flow for first time users
            _navigationEvent.emit(SplashNavigationEvent.NavigateToOnboarding)
        }
    }
}
