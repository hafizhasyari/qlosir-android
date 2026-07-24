package com.qlosir.app.ui.register

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
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Initial State

    @Test
    fun `initial state has empty fields and no errors`() {
        val state = viewModel.uiState.value
        assertEquals("", state.storeName)
        assertEquals("", state.ownerName)
        assertEquals("", state.phone)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isPasswordVisible)
        assertNull(state.storeNameError)
        assertNull(state.ownerNameError)
        assertNull(state.phoneError)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertFalse(state.isLoading)
    }

    // endregion

    // region Input Changes

    @Test
    fun `onStoreNameChanged updates storeName and clears error`() {
        viewModel.onStoreNameChanged("Warung Jaya")
        assertEquals("Warung Jaya", viewModel.uiState.value.storeName)
        assertNull(viewModel.uiState.value.storeNameError)
    }

    @Test
    fun `onOwnerNameChanged updates ownerName and clears error`() {
        viewModel.onOwnerNameChanged("Budi")
        assertEquals("Budi", viewModel.uiState.value.ownerName)
        assertNull(viewModel.uiState.value.ownerNameError)
    }

    @Test
    fun `onPhoneChanged updates phone and clears error`() {
        viewModel.onPhoneChanged("08123456789")
        assertEquals("08123456789", viewModel.uiState.value.phone)
        assertNull(viewModel.uiState.value.phoneError)
    }

    @Test
    fun `onEmailChanged updates email and clears error`() {
        viewModel.onEmailChanged("budi@email.com")
        assertEquals("budi@email.com", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun `onPasswordChanged updates password and clears error`() {
        viewModel.onPasswordChanged("password123")
        assertEquals("password123", viewModel.uiState.value.password)
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
    fun `onSubmit with all empty fields shows all required errors`() {
        viewModel.onSubmit()
        val state = viewModel.uiState.value
        assertEquals(R.string.register_error_store_required, state.storeNameError)
        assertEquals(R.string.register_error_owner_required, state.ownerNameError)
        assertEquals(R.string.register_error_phone_required, state.phoneError)
        assertEquals(R.string.register_error_password_length, state.passwordError)
        assertNull(state.emailError) // email is optional
    }

    @Test
    fun `onSubmit with short password shows password error`() {
        viewModel.onStoreNameChanged("Warung")
        viewModel.onOwnerNameChanged("Budi")
        viewModel.onPhoneChanged("08123456789")
        viewModel.onPasswordChanged("short")

        viewModel.onSubmit()

        assertEquals(R.string.register_error_password_length, viewModel.uiState.value.passwordError)
    }

    @Test
    fun `onSubmit with valid data sets isLoading true and emits NavigateToLogin`() = runTest {
        // Given - email left empty to avoid android.util.Patterns dependency in JVM tests
        viewModel.onStoreNameChanged("Warung Jaya")
        viewModel.onOwnerNameChanged("Budi Setiawan")
        viewModel.onPhoneChanged("08123456789")
        viewModel.onPasswordChanged("password123")

        val events = mutableListOf<RegisterNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        // When
        viewModel.onSubmit()

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
        assertEquals(1, events.size)
        assertEquals(RegisterNavigationEvent.NavigateToLogin, events.first())
        job.cancel()
    }

    @Test
    fun `onSubmit with valid data and empty email passes validation`() = runTest {
        viewModel.onStoreNameChanged("Warung Jaya")
        viewModel.onOwnerNameChanged("Budi")
        viewModel.onPhoneChanged("08123456789")
        viewModel.onPasswordChanged("password123")
        // email left empty — should be fine

        val events = mutableListOf<RegisterNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onSubmit()

        assertNull(viewModel.uiState.value.emailError)
        assertEquals(RegisterNavigationEvent.NavigateToLogin, events.first())
        job.cancel()
    }

    // endregion

    // region Navigation

    @Test
    fun `onBackClicked emits NavigateBack event`() = runTest {
        val events = mutableListOf<RegisterNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onBackClicked()

        assertEquals(1, events.size)
        assertEquals(RegisterNavigationEvent.NavigateBack, events.first())
        job.cancel()
    }

    // endregion

    // region Error Clearing

    @Test
    fun `changing input after validation error clears that specific error`() {
        // Trigger validation errors
        viewModel.onSubmit()
        assertEquals(R.string.register_error_store_required, viewModel.uiState.value.storeNameError)

        // Fix store name — only store error should clear
        viewModel.onStoreNameChanged("Warung")
        assertNull(viewModel.uiState.value.storeNameError)
        // Other errors remain
        assertEquals(R.string.register_error_owner_required, viewModel.uiState.value.ownerNameError)
    }

    // endregion
}
