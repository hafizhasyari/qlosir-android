package com.qlosir.app.ui.onboarding

import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingViewModelTest {

    @Test
    fun `initial OnboardingUiState has page 0 and total 3 pages`() {
        val state = OnboardingUiState()
        assertEquals(0, state.currentPage)
        assertEquals(3, state.totalPages)
    }

    @Test
    fun `OnboardingUiState copy updates currentPage correctly`() {
        val state = OnboardingUiState()
        val updated = state.copy(currentPage = 1)
        assertEquals(1, updated.currentPage)
        assertEquals(3, updated.totalPages)

        val updated2 = updated.copy(currentPage = 2)
        assertEquals(2, updated2.currentPage)
    }
}
