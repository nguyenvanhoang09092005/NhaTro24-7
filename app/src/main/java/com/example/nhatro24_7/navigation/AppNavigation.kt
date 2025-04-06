package com.example.nhatro24_7.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nhatro24_7.ui.screen.Admin.AdminScreen
import com.example.nhatro24_7.ui.screen.Auth.LoginScreen
import com.example.nhatro24_7.ui.screen.Auth.RegisterScreen
import com.example.nhatro24_7.ui.screen.Auth.SplashScreen
import com.example.nhatro24_7.ui.screen.customer.home.CustomerHomeScreen
import com.example.nhatro24_7.ui.screen.customer.profile.*
import com.example.nhatro24_7.ui.screen.landlord.LandlordScreen
import com.example.nhatro24_7.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    isDarkTheme: Boolean,
    selectedLanguage: String,
    onToggleTheme: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = "splash") {

        // Splash
        composable("splash") {
            SplashScreen(
                viewModel = authViewModel,
                onNavigateToHome = { role ->
                    when (role) {
                        "admin" -> navController.navigate("admin") {
                            popUpTo("splash") { inclusive = true }
                        }
                        "landlord" -> navController.navigate("landlord") {
                            popUpTo("splash") { inclusive = true }
                        }
                        else -> navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // Auth
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                isLoading = false,
                onNavigateToHome = { role ->
                    when (role) {
                        "admin" -> navController.navigate("admin")
                        "landlord" -> navController.navigate("landlord")
                        else -> navController.navigate("home")
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(onLoginClick = { navController.navigate("login") })
        }

        // Customer
        composable("home") {
            CustomerHomeScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                viewModel = authViewModel,
                isDarkMode = isDarkTheme,
                selectedLanguage = selectedLanguage,
                onThemeToggle = onToggleTheme,
                onLanguageChange = onLanguageChange
            )
        }

        composable("activity_history") {
            ActivityHistoryScreen(navController = navController)
        }
        composable("profile_detail") {
            UserProfileScreen(viewModel = authViewModel)
        }
        composable("liked_history") {
            LikedHistoryScreen(navController = navController)
        }

//        composable("booking_history") {
//            BookingHistoryScreen(navController = navController)
//        }
//
//        composable("cancel_history") {
//            CancelHistoryScreen(navController = navController)
//        }
//
//        composable("review_history") {
//            ReviewHistoryScreen(navController = navController)
//        }

        // Account settings (dummy screens)
//        composable("change_password") { ChangePasswordScreen(navController = navController) }
//        composable("verify_account") { VerifyAccountScreen(navController = navController) }
//        composable("link_accounts") { LinkAccountsScreen(navController = navController) }
//        composable("delete_account") { DeleteAccountScreen(navController = navController) }

        // Roles
        composable("landlord") {
            LandlordScreen(navController = navController, viewModel = authViewModel)
        }

        composable("admin") {
            AdminScreen(navController = navController, viewModel = authViewModel)
        }
    }
}
