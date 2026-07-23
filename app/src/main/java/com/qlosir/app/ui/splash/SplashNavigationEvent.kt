package com.qlosir.app.ui.splash

/**
 * Navigation events emitted by SplashViewModel.
 */
sealed interface SplashNavigationEvent {
    data object NavigateToOnboarding : SplashNavigationEvent
    data object NavigateToLogin : SplashNavigationEvent
    data object NavigateToDashboard : SplashNavigationEvent
}
