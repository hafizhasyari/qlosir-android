package com.qlosir.app.ui.login

/**
 * Navigation events emitted by LoginViewModel.
 */
sealed interface LoginNavigationEvent {
    data object NavigateToRegister : LoginNavigationEvent
    data object NavigateToForgotPassword : LoginNavigationEvent
    data object NavigateToDashboard : LoginNavigationEvent
}
