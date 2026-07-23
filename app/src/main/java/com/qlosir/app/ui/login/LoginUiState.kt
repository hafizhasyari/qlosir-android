package com.qlosir.app.ui.login

/**
 * UI state for the Login screen.
 */
data class LoginUiState(
    val phone: String = "",
    val password: String = "",
    val phoneError: Int? = null,
    val passwordError: Int? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false
)
