package com.qlosir.app.ui.createpin

/**
 * UI state for the Create PIN screen.
 *
 * @param pin The current PIN digits entered (max 6 characters)
 * @param confirmPin The confirm PIN digits entered (max 6 characters)
 * @param isConfirmStep Whether the user is in the confirm PIN step
 * @param errorMessage String resource ID for error message (e.g., PIN mismatch)
 * @param isLoading Whether PIN creation is in progress
 */
data class CreatePinUiState(
    val pin: String = "",
    val confirmPin: String = "",
    val isConfirmStep: Boolean = false,
    val errorMessage: Int? = null,
    val isLoading: Boolean = false
) {
    val currentPin: String
        get() = if (isConfirmStep) confirmPin else pin

    val filledDots: Int
        get() = currentPin.length

    companion object {
        const val PIN_LENGTH = 6
    }
}
