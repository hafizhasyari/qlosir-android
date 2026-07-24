package com.qlosir.app.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qlosir.app.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Store Registration Screen. Handles input state, validations, and navigation events.
 */
class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RegisterNavigationEvent>()
    val navigationEvent: SharedFlow<RegisterNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onStoreNameChanged(value: String) {
        _uiState.update { it.copy(storeName = value, storeNameError = null) }
    }

    fun onOwnerNameChanged(value: String) {
        _uiState.update { it.copy(ownerName = value, ownerNameError = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(RegisterNavigationEvent.NavigateBack)
        }
    }

    fun onSubmit() {
        val currentState = _uiState.value

        val storeError = if (currentState.storeName.isBlank()) R.string.register_error_store_required else null
        val ownerError = if (currentState.ownerName.isBlank()) R.string.register_error_owner_required else null
        val phoneError = if (currentState.phone.isBlank()) R.string.register_error_phone_required else null
        val emailError = if (currentState.email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            R.string.register_error_email_invalid
        } else null
        val passError = if (currentState.password.length < 8) R.string.register_error_password_length else null

        if (storeError != null || ownerError != null || phoneError != null || emailError != null || passError != null) {
            _uiState.update {
                it.copy(
                    storeNameError = storeError,
                    ownerNameError = ownerError,
                    phoneError = phoneError,
                    emailError = emailError,
                    passwordError = passError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Submit logic / API call would take place here
            _navigationEvent.emit(RegisterNavigationEvent.NavigateToDashboard)
        }
    }
}
