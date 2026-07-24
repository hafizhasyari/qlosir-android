package com.qlosir.app.ui.createpin

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
 * ViewModel for the Create PIN screen.
 * Handles PIN digit entry, confirmation step, validation, and navigation events.
 */
class CreatePinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePinUiState())
    val uiState: StateFlow<CreatePinUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<CreatePinNavigationEvent>()
    val navigationEvent: SharedFlow<CreatePinNavigationEvent> = _navigationEvent.asSharedFlow()

    /**
     * Appends a digit to the current PIN (create or confirm step).
     * Automatically advances to confirm step when 6 digits are entered,
     * or validates and completes when confirm PIN reaches 6 digits.
     */
    fun onDigitPressed(digit: Int) {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        if (currentState.isConfirmStep) {
            if (currentState.confirmPin.length >= CreatePinUiState.PIN_LENGTH) return

            val newConfirmPin = currentState.confirmPin + digit.toString()
            _uiState.update { it.copy(confirmPin = newConfirmPin, errorMessage = null) }

            if (newConfirmPin.length == CreatePinUiState.PIN_LENGTH) {
                validateAndSubmit(currentState.pin, newConfirmPin)
            }
        } else {
            if (currentState.pin.length >= CreatePinUiState.PIN_LENGTH) return

            val newPin = currentState.pin + digit.toString()
            _uiState.update { it.copy(pin = newPin, errorMessage = null) }

            if (newPin.length == CreatePinUiState.PIN_LENGTH) {
                _uiState.update { it.copy(isConfirmStep = true) }
            }
        }
    }

    /**
     * Deletes the last digit from the current PIN input.
     */
    fun onDeletePressed() {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        if (currentState.isConfirmStep) {
            if (currentState.confirmPin.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        confirmPin = it.confirmPin.dropLast(1),
                        errorMessage = null
                    )
                }
            }
        } else {
            if (currentState.pin.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        pin = it.pin.dropLast(1),
                        errorMessage = null
                    )
                }
            }
        }
    }

    /**
     * Navigates back. If in confirm step, goes back to create step.
     * If in create step, emits NavigateBack event.
     */
    fun onBackPressed() {
        val currentState = _uiState.value

        if (currentState.isConfirmStep) {
            _uiState.update {
                it.copy(
                    isConfirmStep = false,
                    confirmPin = "",
                    errorMessage = null
                )
            }
        } else {
            viewModelScope.launch {
                _navigationEvent.emit(CreatePinNavigationEvent.NavigateBack)
            }
        }
    }

    private fun validateAndSubmit(pin: String, confirmPin: String) {
        if (pin != confirmPin) {
            _uiState.update {
                it.copy(
                    confirmPin = "",
                    errorMessage = R.string.create_pin_error_mismatch
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // PIN storage would go here (e.g., encrypted DataStore)
            _navigationEvent.emit(CreatePinNavigationEvent.NavigateToDashboard)
        }
    }
}
