package com.qlosir.app.ui.onboarding

/**
 * Navigation events emitted by OnboardingViewModel.
 */
sealed interface OnboardingNavigationEvent {
    data object NavigateToLogin : OnboardingNavigationEvent
    data object NavigateToRegister : OnboardingNavigationEvent
    data object NavigateToDashboard : OnboardingNavigationEvent
}
