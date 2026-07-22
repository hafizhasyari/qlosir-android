package com.qlosir.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qlosir.app.ui.splash.SplashNavigationEvent
import com.qlosir.app.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
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
            // Placeholder for Onboarding flow screens (Onboarding 1, 2, 3)
        }

        composable(Screen.Login.route) {
            // Placeholder for Login screen
        }

        composable(Screen.Dashboard.route) {
            // Placeholder for Dashboard screen
        }
    }
}
