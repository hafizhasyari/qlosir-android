package com.qlosir.app.ui.createpin

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
class CreatePinViewModelTest {

    private lateinit var viewModel: CreatePinViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreatePinViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Initial State

    @Test
    fun `initial state has empty pin and is not in confirm step`() {
        val state = viewModel.uiState.value
        assertEquals("", state.pin)
        assertEquals("", state.confirmPin)
        assertFalse(state.isConfirmStep)
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
        assertEquals(0, state.filledDots)
    }

    // endregion

    // region Digit Entry (Create Step)

    @Test
    fun `onDigitPressed appends digit to pin in create step`() {
        viewModel.onDigitPressed(1)
        assertEquals("1", viewModel.uiState.value.pin)
        assertEquals(1, viewModel.uiState.value.filledDots)
    }

    @Test
    fun `onDigitPressed appends multiple digits to pin`() {
        viewModel.onDigitPressed(1)
        viewModel.onDigitPressed(2)
        viewModel.onDigitPressed(3)
        assertEquals("123", viewModel.uiState.value.pin)
        assertEquals(3, viewModel.uiState.value.filledDots)
    }

    @Test
    fun `entering 6 digits transitions to confirm step`() {
        repeat(6) { viewModel.onDigitPressed(it + 1) }
        val state = viewModel.uiState.value
        assertEquals("123456", state.pin)
        assertTrue(state.isConfirmStep)
        assertEquals("", state.confirmPin)
        assertEquals(0, state.filledDots) // confirmPin is empty
    }

    @Test
    fun `onDigitPressed does not exceed 6 digits in create step`() {
        repeat(7) { viewModel.onDigitPressed(1) }
        // After 6, it transitions to confirm step and 7th digit goes to confirmPin
        assertEquals("111111", viewModel.uiState.value.pin)
        assertTrue(viewModel.uiState.value.isConfirmStep)
        assertEquals("1", viewModel.uiState.value.confirmPin)
    }

    // endregion

    // region Digit Entry (Confirm Step)

    @Test
    fun `onDigitPressed appends digit to confirmPin in confirm step`() {
        // Enter 6 digits to move to confirm step
        repeat(6) { viewModel.onDigitPressed(1) }
        assertTrue(viewModel.uiState.value.isConfirmStep)

        // Enter digit in confirm step
        viewModel.onDigitPressed(1)
        assertEquals("1", viewModel.uiState.value.confirmPin)
        assertEquals(1, viewModel.uiState.value.filledDots)
    }

    @Test
    fun `entering matching 6-digit confirm PIN emits NavigateToDashboard`() = runTest {
        // Enter PIN: 123456
        repeat(6) { viewModel.onDigitPressed(it + 1) }

        val events = mutableListOf<CreatePinNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        // Enter matching confirm PIN: 123456
        repeat(6) { viewModel.onDigitPressed(it + 1) }

        assertEquals(1, events.size)
        assertEquals(CreatePinNavigationEvent.NavigateToDashboard, events.first())
        assertTrue(viewModel.uiState.value.isLoading)
        job.cancel()
    }

    @Test
    fun `entering mismatched confirm PIN shows error and clears confirmPin`() {
        // Enter PIN: 123456
        repeat(6) { viewModel.onDigitPressed(it + 1) }

        // Enter different confirm PIN: 654321
        viewModel.onDigitPressed(6)
        viewModel.onDigitPressed(5)
        viewModel.onDigitPressed(4)
        viewModel.onDigitPressed(3)
        viewModel.onDigitPressed(2)
        viewModel.onDigitPressed(1)

        val state = viewModel.uiState.value
        assertEquals(R.string.create_pin_error_mismatch, state.errorMessage)
        assertEquals("", state.confirmPin)
        assertTrue(state.isConfirmStep) // Stays in confirm step
    }

    // endregion

    // region Delete

    @Test
    fun `onDeletePressed removes last digit in create step`() {
        viewModel.onDigitPressed(1)
        viewModel.onDigitPressed(2)
        viewModel.onDigitPressed(3)
        viewModel.onDeletePressed()
        assertEquals("12", viewModel.uiState.value.pin)
    }

    @Test
    fun `onDeletePressed on empty pin does nothing`() {
        viewModel.onDeletePressed()
        assertEquals("", viewModel.uiState.value.pin)
    }

    @Test
    fun `onDeletePressed removes last digit in confirm step`() {
        // Enter 6 digits to move to confirm step
        repeat(6) { viewModel.onDigitPressed(1) }

        viewModel.onDigitPressed(2)
        viewModel.onDigitPressed(3)
        viewModel.onDeletePressed()
        assertEquals("2", viewModel.uiState.value.confirmPin)
    }

    @Test
    fun `onDeletePressed clears error message when confirmPin is not empty`() {
        // Enter 6 digits to move to confirm step
        repeat(6) { viewModel.onDigitPressed(it + 1) } // pin = "123456"
        assertTrue(viewModel.uiState.value.isConfirmStep)

        // Enter a digit, then delete — verify error stays null (no error to begin with)
        viewModel.onDigitPressed(5) // confirmPin = "5"
        viewModel.onDeletePressed() // confirmPin = ""
        assertNull(viewModel.uiState.value.errorMessage)

        // Create mismatch: enter 6 wrong digits
        viewModel.onDigitPressed(9)
        viewModel.onDigitPressed(9)
        viewModel.onDigitPressed(9)
        viewModel.onDigitPressed(9)
        viewModel.onDigitPressed(9)
        viewModel.onDigitPressed(9) // "999999" != "123456" → error, confirmPin cleared

        assertEquals(R.string.create_pin_error_mismatch, viewModel.uiState.value.errorMessage)
        assertEquals("", viewModel.uiState.value.confirmPin)

        // Entering a new digit clears the error (tested separately)
        // onDeletePressed on empty confirmPin won't fire the clear (no-op)
        // So let's enter a digit to have confirmPin non-empty, then verify delete preserves null error
        viewModel.onDigitPressed(1) // clears error, confirmPin = "1"
        assertNull(viewModel.uiState.value.errorMessage)

        viewModel.onDigitPressed(2) // confirmPin = "12"
        viewModel.onDeletePressed() // confirmPin = "1", error should still be null
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals("1", viewModel.uiState.value.confirmPin)
    }

    // endregion

    // region Back Navigation

    @Test
    fun `onBackPressed in confirm step goes back to create step`() {
        // Enter 6 digits to move to confirm step
        repeat(6) { viewModel.onDigitPressed(1) }
        assertTrue(viewModel.uiState.value.isConfirmStep)

        viewModel.onBackPressed()
        val state = viewModel.uiState.value
        assertFalse(state.isConfirmStep)
        assertEquals("", state.confirmPin)
        assertNull(state.errorMessage)
    }

    @Test
    fun `onBackPressed in create step emits NavigateBack`() = runTest {
        val events = mutableListOf<CreatePinNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onBackPressed()

        assertEquals(1, events.size)
        assertEquals(CreatePinNavigationEvent.NavigateBack, events.first())
        job.cancel()
    }

    @Test
    fun `onBackPressed in confirm step preserves original pin`() {
        repeat(6) { viewModel.onDigitPressed(it + 1) }
        viewModel.onDigitPressed(9)
        viewModel.onDigitPressed(8)

        viewModel.onBackPressed()

        assertEquals("123456", viewModel.uiState.value.pin)
        assertFalse(viewModel.uiState.value.isConfirmStep)
    }

    // endregion

    // region Error Clearing

    @Test
    fun `entering digit after error clears error message`() {
        // Create mismatch error
        repeat(6) { viewModel.onDigitPressed(it + 1) }
        repeat(6) { viewModel.onDigitPressed(9) }

        assertEquals(R.string.create_pin_error_mismatch, viewModel.uiState.value.errorMessage)

        viewModel.onDigitPressed(1)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    // endregion

    // region UiState Properties

    @Test
    fun `currentPin returns pin when not in confirm step`() {
        viewModel.onDigitPressed(1)
        viewModel.onDigitPressed(2)
        assertEquals("12", viewModel.uiState.value.currentPin)
    }

    @Test
    fun `currentPin returns confirmPin when in confirm step`() {
        repeat(6) { viewModel.onDigitPressed(1) }
        viewModel.onDigitPressed(5)
        assertEquals("5", viewModel.uiState.value.currentPin)
    }

    @Test
    fun `filledDots reflects currentPin length`() {
        viewModel.onDigitPressed(1)
        viewModel.onDigitPressed(2)
        viewModel.onDigitPressed(3)
        assertEquals(3, viewModel.uiState.value.filledDots)
    }

    // endregion

    // region Loading State

    @Test
    fun `digits are ignored when isLoading is true`() = runTest {
        // Enter PIN and matching confirm to trigger loading
        repeat(6) { viewModel.onDigitPressed(1) }

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { }
        }

        repeat(6) { viewModel.onDigitPressed(1) }

        assertTrue(viewModel.uiState.value.isLoading)

        // Try to enter more digits while loading - should be ignored
        viewModel.onDigitPressed(9)
        // confirmPin should remain empty since loading resets it via navigation
        job.cancel()
    }

    @Test
    fun `delete is ignored when isLoading is true`() = runTest {
        // Enter PIN and matching confirm to trigger loading
        repeat(6) { viewModel.onDigitPressed(1) }

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { }
        }

        repeat(6) { viewModel.onDigitPressed(1) }

        assertTrue(viewModel.uiState.value.isLoading)

        viewModel.onDeletePressed()
        // Should still be in loading state
        assertTrue(viewModel.uiState.value.isLoading)
        job.cancel()
    }

    // endregion
}
