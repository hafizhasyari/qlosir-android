package com.qlosir.app.ui.register

import androidx.annotation.StringRes

/**
 * UI State data class for the Store Registration Screen.
 */
data class RegisterUiState(
    val storeName: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    @StringRes val storeNameError: Int? = null,
    @StringRes val ownerNameError: Int? = null,
    @StringRes val phoneError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    val isLoading: Boolean = false
)
