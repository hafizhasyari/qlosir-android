package com.qlosir.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qlosir.app.ui.onboarding.OnboardingNavigationEvent
import com.qlosir.app.ui.onboarding.OnboardingScreen
import com.qlosir.app.ui.register.RegisterNavigationEvent
import com.qlosir.app.ui.register.RegisterScreen
import com.qlosir.app.ui.splash.SplashNavigationEvent
import com.qlosir.app.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Register : Screen("register")
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
}

@Composable
fun QlosirNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = { event ->
                    when (event) {
                        is SplashNavigationEvent.NavigateToOnboarding -> {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        is SplashNavigationEvent.NavigateToLogin -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        is SplashNavigationEvent.NavigateToDashboard -> {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigate = { event ->
                    when (event) {
                        is OnboardingNavigationEvent.NavigateToLogin -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                        is OnboardingNavigationEvent.NavigateToRegister -> {
                            navController.navigate(Screen.Register.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                        is OnboardingNavigationEvent.NavigateToDashboard -> {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigate = { event ->
                    when (event) {
                        is RegisterNavigationEvent.NavigateBack -> {
                            navController.popBackStack()
                        }
                        is RegisterNavigationEvent.NavigateToLogin -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                        is RegisterNavigationEvent.NavigateToDashboard -> {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            // Placeholder for Login screen
        }

        composable(Screen.Dashboard.route) {
            // Placeholder for Dashboard screen
        }
    }
}
