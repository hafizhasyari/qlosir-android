package com.qlosir.app.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qlosir.app.data.OnboardingPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val onboardingPreferences = OnboardingPreferences(application)

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        startSplashTimer()
    }

    private fun startSplashTimer() {
        viewModelScope.launch {
            // Standard splash display duration of 2.5 seconds
            delay(2500L)

            // Check if onboarding has been completed before
            val onboardingCompleted = onboardingPreferences.isOnboardingCompleted.first()

            if (onboardingCompleted) {
                // User has already seen onboarding, go straight to Login
                _navigationEvent.emit(SplashNavigationEvent.NavigateToLogin)
            } else {
                // First time user, show onboarding
                _navigationEvent.emit(SplashNavigationEvent.NavigateToOnboarding)
            }
        }
    }
}
