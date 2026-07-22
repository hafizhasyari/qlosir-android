package com.qlosir.app.ui.onboarding

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        viewModel = OnboardingViewModel()
    }

    @Test
    fun `initial state has page 0 and total 3 pages`() {
        val state = viewModel.uiState.value
        assertEquals(0, state.currentPage)
        assertEquals(3, state.totalPages)
    }

    @Test
    fun `onPageChanged updates current page state`() {
        viewModel.onPageChanged(1)
        assertEquals(1, viewModel.uiState.value.currentPage)

        viewModel.onPageChanged(2)
        assertEquals(2, viewModel.uiState.value.currentPage)
    }

    @Test
    fun `onNextClicked increments current page when not on last page`() {
        viewModel.onNextClicked()
        assertEquals(1, viewModel.uiState.value.currentPage)

        viewModel.onNextClicked()
        assertEquals(2, viewModel.uiState.value.currentPage)
    }
}
