package com.qlosir.app.ui.splash

import android.app.Application
import com.qlosir.app.data.OnboardingPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `navigates to onboarding when onboarding not completed`() = runTest {
        // Given
        mockkConstructor(OnboardingPreferences::class)
        every { anyConstructed<OnboardingPreferences>().isOnboardingCompleted } returns flowOf(false)

        val application = mockk<Application>(relaxed = true)

        // When
        val viewModel = SplashViewModel(application)

        // Then - collect navigation event
        val events = mutableListOf<SplashNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        advanceTimeBy(3000L)

        assertEquals(1, events.size)
        assertEquals(SplashNavigationEvent.NavigateToOnboarding, events.first())
        job.cancel()
    }

    @Test
    fun `navigates to login when onboarding already completed`() = runTest {
        // Given
        mockkConstructor(OnboardingPreferences::class)
        every { anyConstructed<OnboardingPreferences>().isOnboardingCompleted } returns flowOf(true)

        val application = mockk<Application>(relaxed = true)

        // When
        val viewModel = SplashViewModel(application)

        // Then - collect navigation event
        val events = mutableListOf<SplashNavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        advanceTimeBy(3000L)

        assertEquals(1, events.size)
        assertEquals(SplashNavigationEvent.NavigateToLogin, events.first())
        job.cancel()
    }

    @Test
    fun `SplashNavigationEvent sealed interface has correct variants`() {
        // Verify all navigation event types exist and are distinct
        val events: List<SplashNavigationEvent> = listOf(
            SplashNavigationEvent.NavigateToOnboarding,
            SplashNavigationEvent.NavigateToLogin,
            SplashNavigationEvent.NavigateToDashboard
        )
        assertEquals(3, events.size)
        assertEquals(SplashNavigationEvent.NavigateToOnboarding, events[0])
        assertEquals(SplashNavigationEvent.NavigateToLogin, events[1])
        assertEquals(SplashNavigationEvent.NavigateToDashboard, events[2])
    }
}
