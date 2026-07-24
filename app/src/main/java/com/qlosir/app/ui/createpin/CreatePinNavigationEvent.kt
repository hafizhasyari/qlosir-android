package com.qlosir.app.ui.createpin

/**
 * Navigation events emitted by CreatePinViewModel.
 */
sealed interface CreatePinNavigationEvent {
    data object NavigateToDashboard : CreatePinNavigationEvent
    data object NavigateBack : CreatePinNavigationEvent
}
