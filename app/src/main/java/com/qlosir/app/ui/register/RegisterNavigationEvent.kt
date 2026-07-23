package com.qlosir.app.ui.register

/**
 * Navigation events emitted by RegisterViewModel.
 */
sealed interface RegisterNavigationEvent {
    data object NavigateBack : RegisterNavigationEvent
    data object NavigateToLogin : RegisterNavigationEvent
    data object NavigateToDashboard : RegisterNavigationEvent
}
