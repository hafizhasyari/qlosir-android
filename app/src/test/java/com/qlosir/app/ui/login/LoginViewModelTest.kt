package com.qlosir.app.ui.login

import com.qlosir.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Initial State

    @Test
    fun `initial state has empty fields and no errors`() {
        val state = viewModel.uiState.value
        assertEquals("", state.phone)
        assertEquals("", state.password)
        assertNull(state.phoneError)
        assertNull(state.passwordError)
        assertFalse(state.isPasswordVisible)
        assertFalse(state.isLoading)
    }

    // endregion

    // region Input Changes

    @Test
    fun `onPhoneChanged updates phone and clears error`() {
        viewModel.onPhoneChanged("08123456789")
        assertEquals("08123456789", viewModel.uiState.value.phone)
        assertNull(viewModel.uiState.value.phoneError)
    }

    @Test
    fun `onPasswordChanged updates password and clears error`() {
        viewModel.onPasswordChanged("mypassword")
        assertEquals("mypassword", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `togglePasswordVisibility toggles isPasswordVisible`() {
        assertFalse(viewModel.uiState.value.isPasswordVisible)
        viewModel.togglePasswordVisibility()
        assertTrue(viewModel.uiState.value.isPasswordVisible)
        viewModel.togglePasswordVisibility()
        assertFalse(viewModel.uiState.value.isPasswordVisible)
    }

    // endregion

    // region Validation

    @Test
    fun `onLoginClicked with empty fields shows all required errors`() {
        viewModel.onLoginClicked()
        val state = viewModel.uiState.value
        assertEquals(R.string.login_error_phone_required, state.phoneError)
        assertEquals(R.string.login_error_password_required, state.passwordError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `onLoginClicked with empty phone shows phone error only`() {
        viewModel.onPasswordChanged("password123")
        viewModel.onLoginClicked()
        val state = viewModel.uiState.value
        assertEquals(R.string.login_error_phone_required, state.phoneError)
        assertNull(state.passwordError)
    }

    @Test
    fun `onLoginClicked with empty password shows password error only`() {
        viewModel.onPhoneChanged("08123456789")
        viewModel.onLoginClicked()
        val state = viewModel.uiState.value
        assertNull(state.phoneError)
        assertEquals(R.string.login_error_password_required, state.passwordError)
    }

    @Test
    fun `onLoginClicked with valid data sets isLoading and emits NavigateToDashboard`() = runTest {
        // Given
        viewModel.onPhoneChanged("08123456789")
        viewModel.onPasswordChanged("password123")

        val events = mutableListOf<LoginNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        // When
        viewModel.onLoginClicked()

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
        assertEquals(1, events.size)
        assertEquals(LoginNavigationEvent.NavigateToDashboard, events.first())
        job.cancel()
    }

    // endregion

    // region Navigation

    @Test
    fun `onForgotPasswordClicked emits NavigateToForgotPassword`() = runTest {
        val events = mutableListOf<LoginNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onForgotPasswordClicked()

        assertEquals(1, events.size)
        assertEquals(LoginNavigationEvent.NavigateToForgotPassword, events.first())
        job.cancel()
    }

    @Test
    fun `onRegisterClicked emits NavigateToRegister`() = runTest {
        val events = mutableListOf<LoginNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onRegisterClicked()

        assertEquals(1, events.size)
        assertEquals(LoginNavigationEvent.NavigateToRegister, events.first())
        job.cancel()
    }

    // endregion

    // region Error Clearing

    @Test
    fun `changing phone after validation error clears phone error`() {
        viewModel.onLoginClicked()
        assertEquals(R.string.login_error_phone_required, viewModel.uiState.value.phoneError)

        viewModel.onPhoneChanged("081")
        assertNull(viewModel.uiState.value.phoneError)
        // Password error remains
        assertEquals(R.string.login_error_password_required, viewModel.uiState.value.passwordError)
    }

    @Test
    fun `changing password after validation error clears password error`() {
        viewModel.onLoginClicked()
        assertEquals(R.string.login_error_password_required, viewModel.uiState.value.passwordError)

        viewModel.onPasswordChanged("p")
        assertNull(viewModel.uiState.value.passwordError)
        // Phone error remains
        assertEquals(R.string.login_error_phone_required, viewModel.uiState.value.phoneError)
    }

    // endregion
}
