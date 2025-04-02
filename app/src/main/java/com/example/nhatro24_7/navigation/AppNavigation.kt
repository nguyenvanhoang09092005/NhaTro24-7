package com.example.nhatro24_7.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nhatro24_7.ui.screen.Admin.AdminScreen
import com.example.nhatro24_7.ui.screen.Auth.LoginScreen
import com.example.nhatro24_7.ui.screen.Auth.RegisterScreen
import com.example.nhatro24_7.ui.screen.customer.HomeScreen
import com.example.nhatro24_7.ui.screen.landlord.LandlordScreen
import com.example.nhatro24_7.viewmodel.AuthViewModel

@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "login") {
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
                onNavigateToRegister = { navController.navigate("register") },
//                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable("register") {
            RegisterScreen(
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("home") { HomeScreen(navController = navController) }
        composable("landlord") { LandlordScreen(navController = navController) }
        composable("admin") { AdminScreen(navController = navController) }
    }
}

