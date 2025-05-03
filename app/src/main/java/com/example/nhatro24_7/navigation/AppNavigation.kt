package com.example.nhatro24_7.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nhatro24_7.ui.screen.Admin.AdminScreen
import com.example.nhatro24_7.ui.screen.Auth.LoginScreen
import com.example.nhatro24_7.ui.screen.Auth.RegisterScreen
import com.example.nhatro24_7.ui.screen.Auth.SplashScreen
import com.example.nhatro24_7.ui.screen.customer.booking.BookingPendingScreen
import com.example.nhatro24_7.ui.screen.customer.home.CustomerHomeScreen
import com.example.nhatro24_7.ui.screen.customer.home.RoomDetailScreen
import com.example.nhatro24_7.ui.screen.customer.payment.PaymentScreen
import com.example.nhatro24_7.ui.screen.customer.profile.*
import com.example.nhatro24_7.ui.screen.customer.profile.account.ChangePasswordScreen
import com.example.nhatro24_7.ui.screen.customer.profile.account.DeleteAccountScreen
import com.example.nhatro24_7.ui.screen.customer.profile.account.LinkAccountsScreen
import com.example.nhatro24_7.ui.screen.customer.profile.account.VerifyAccountScreen
import com.example.nhatro24_7.ui.screen.customer.saveroom.SavedRoomScreen
import com.example.nhatro24_7.ui.screen.landlord.home.LandlordScreen
import com.example.nhatro24_7.ui.screen.landlord.room.AddRoomScreen
import com.example.nhatro24_7.ui.screen.landlord.room.BookingRequestDetailScreen
import com.example.nhatro24_7.ui.screen.landlord.room.BookingRequestsForLandlordScreen
import com.example.nhatro24_7.ui.screen.notification.NotificationScreen
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    isDarkTheme: Boolean,
    selectedLanguage: String,
    onToggleTheme: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // Splash
        composable(Routes.SPLASH) {
            SplashScreen(
                viewModel = authViewModel,
                onNavigateToHome = { role ->
                    when (role) {
                        "admin" -> navController.navigate(Routes.ADMIN_HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                        "landlord" -> navController.navigate(Routes.LANDLORD_HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                        else -> navController.navigate(Routes.CUSTOMER_HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Auth
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                isLoading = false,
                onNavigateToHome = { role ->
                    when (role) {
                        "admin" -> navController.navigate(Routes.ADMIN_HOME)
                        "landlord" -> navController.navigate(Routes.LANDLORD_HOME)
                        else -> navController.navigate(Routes.CUSTOMER_HOME)
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(onLoginClick = { navController.navigate(Routes.LOGIN) })
        }

        // Admin
        composable(Routes.ADMIN_HOME) {
            AdminScreen(navController = navController, viewModel = authViewModel)
        }

        // Customer
        composable(Routes.CUSTOMER_HOME) {
            CustomerHomeScreen(navController = navController, viewModel = authViewModel)
        }

        composable(Routes.ROOM_DETAIL) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            RoomDetailScreen(roomId, navController, RoomViewModel())
        }

        composable(Routes.CUSTOMER_SAVED) {
            SavedRoomScreen(navController, authViewModel, RoomViewModel())
        }


        composable(Routes.BOOKING_PENDING) {
            BookingPendingScreen(navController)
        }

        composable(Routes.PAYMENT_SCREEN) {
            PaymentScreen(navController = navController)
        }

//        composable(Routes.CUSTOMER_NOTIFICATIONS) {
//            NotificationScreen()
//        }


        composable(Routes.CUSTOMER_PROFILE) {
            ProfileScreen(
                navController = navController,
                viewModel = authViewModel,
                isDarkMode = isDarkTheme,
                selectedLanguage = selectedLanguage,
                onThemeToggle = onToggleTheme,
                onLanguageChange = onLanguageChange
            )
        }
        composable(Routes.CUSTOMER_HISTORY) {
            ActivityHistoryScreen(navController = navController)
        }
        composable(Routes.CUSTOMER_PROFILE_DETAIL) {
            UserProfileScreen(viewModel = authViewModel)
        }
        composable(Routes.CUSTOMER_LIKED_HISTORY) {
            LikedHistoryScreen(navController = navController)
        }

        // Landlord
        composable(Routes.LANDLORD_HOME) {
            LandlordScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Routes.LANDLORD_ADD_POST) {
            AddRoomScreen(navController = navController, roomViewModel = RoomViewModel())
        }

        composable(Routes.LANDLORD_BOOKING_REQUESTS) {
            BookingRequestsForLandlordScreen(
                navController = navController,
                roomViewModel = RoomViewModel()
            )

        }

        composable(Routes.LANDLORD_NOTIFY) {
            val landlordId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            NotificationScreen(userId = landlordId)
        }

        composable("booking_detail/{roomId}/{userId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            BookingRequestDetailScreen(roomId, userId, navController, RoomViewModel())
        }




        // Account Settings
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }
        composable(Routes.VERIFY_ACCOUNT) {
            VerifyAccountScreen(navController = navController)
        }
        composable(Routes.LINK_ACCOUNTS) {
            LinkAccountsScreen(navController = navController)
        }
        composable(Routes.DELETE_ACCOUNT) {
            DeleteAccountScreen(navController = navController)
        }
    }
}


//@Composable
//fun AppNavigation(
//    navController: NavHostController,
//    authViewModel: AuthViewModel,
//    isDarkTheme: Boolean,
//    selectedLanguage: String,
//    onToggleTheme: (Boolean) -> Unit,
//    onLanguageChange: (String) -> Unit
//) {
//    NavHost(navController = navController, startDestination = "splash") {
//
//        // Splash
//        composable("splash") {
//            SplashScreen(
//                viewModel = authViewModel,
//                onNavigateToHome = { role ->
//                    when (role) {
//                        "admin" -> navController.navigate("admin") {
//                            popUpTo("splash") { inclusive = true }
//                        }
//                        "landlord" -> navController.navigate("landlord") {
//                            popUpTo("splash") { inclusive = true }
//                        }
//                        else -> navController.navigate("home") {
//                            popUpTo("splash") { inclusive = true }
//                        }
//                    }
//                },
//                onNavigateToLogin = {
//                    navController.navigate("login") {
//                        popUpTo("splash") { inclusive = true }
//                    }
//                }
//            )
//        }
//
//        // Auth
//        composable("login") {
//            LoginScreen(
//                viewModel = authViewModel,
//                isLoading = false,
//                onNavigateToHome = { role ->
//                    when (role) {
//                        "admin" -> navController.navigate("admin")
//                        "landlord" -> navController.navigate("landlord")
//                        else -> navController.navigate("home")
//                    }
//                },
//                onNavigateToRegister = { navController.navigate("register") }
//            )
//        }
//
//        composable("register") {
//            RegisterScreen(onLoginClick = { navController.navigate("login") })
//        }
//        // Splash
//        composable("splash") {
//            SplashScreen(
//                viewModel = authViewModel,
//                onNavigateToHome = { role ->
//                    when (role) {
//                        "admin" -> navController.navigate("admin") {
//                            popUpTo("splash") { inclusive = true }
//                        }
//                        "landlord" -> navController.navigate("landlord") {
//                            popUpTo("splash") { inclusive = true }
//                        }
//                        else -> navController.navigate("home") {
//                            popUpTo("splash") { inclusive = true }
//                        }
//                    }
//                },
//                onNavigateToLogin = {
//                    navController.navigate("login") {
//                        popUpTo("splash") { inclusive = true }
//                    }
//                }
//            )
//        }
//
//        // Auth
//        composable("login") {
//            LoginScreen(
//                viewModel = authViewModel,
//                isLoading = false,
//                onNavigateToHome = { role ->
//                    when (role) {
//                        "admin" -> navController.navigate("admin")
//                        "landlord" -> navController.navigate("landlord")
//                        else -> navController.navigate("home")
//                    }
//                },
//                onNavigateToRegister = { navController.navigate("register") }
//            )
//        }
//
//        composable("register") {
//            RegisterScreen(onLoginClick = { navController.navigate("login") })
//        }
//
//        // Customer
//        composable("home") {
//            CustomerHomeScreen(
//                navController = navController,
//                viewModel = authViewModel
//            )
//        }
//
//        composable("profile") {
//            ProfileScreen(
//                navController = navController,
//                viewModel = authViewModel,
//                isDarkMode = isDarkTheme,
//                selectedLanguage = selectedLanguage,
//                onThemeToggle = onToggleTheme,
//                onLanguageChange = onLanguageChange
//            )
//        }
//
//        composable("activity_history") {
//            ActivityHistoryScreen(navController = navController)
//        }
//        composable("profile_detail") {
//            UserProfileScreen(viewModel = authViewModel)
//        }
//        composable("liked_history") {
//            LikedHistoryScreen(navController = navController)
//        }
//
//
////        composable("booking_history") {
////            BookingHistoryScreen(navController = navController)
////        }
////
////        composable("cancel_history") {
////            CancelHistoryScreen(navController = navController)
////        }
////
////        composable("review_history") {
////            ReviewHistoryScreen(navController = navController)
////        }
//
//        // Account settings (dummy screens)
//        composable("change_password") { ChangePasswordScreen(navController = navController) }
//        composable("verify_account") { VerifyAccountScreen(navController = navController) }
//        composable("link_accounts") { LinkAccountsScreen(navController = navController) }
//        composable("delete_account") { DeleteAccountScreen(navController = navController) }
//
//        // Roles
//        composable("landlord") {
//            LandlordScreen(navController = navController, viewModel = authViewModel)
//        }
//
//        composable("admin") {
//            AdminScreen(navController = navController, viewModel = authViewModel)
//        }
//
//        // Customer
//        composable("home") {
//            CustomerHomeScreen(
//                navController = navController,
//                viewModel = authViewModel
//            )
//        }
//
//        composable("profile") {
//            ProfileScreen(
//                navController = navController,
//                viewModel = authViewModel,
//                isDarkMode = isDarkTheme,
//                selectedLanguage = selectedLanguage,
//                onThemeToggle = onToggleTheme,
//                onLanguageChange = onLanguageChange
//            )
//        }
//
//        composable("activity_history") {
//            ActivityHistoryScreen(navController = navController)
//        }
//        composable("profile_detail") {
//            UserProfileScreen(viewModel = authViewModel)
//        }
//        composable("liked_history") {
//            LikedHistoryScreen(navController = navController)
//        }
//
//
////        composable("booking_history") {
////            BookingHistoryScreen(navController = navController)
////        }
////
////        composable("cancel_history") {
////            CancelHistoryScreen(navController = navController)
////        }
////
////        composable("review_history") {
////            ReviewHistoryScreen(navController = navController)
////        }
//
//        // Account settings (dummy screens)
//        composable("change_password") { ChangePasswordScreen(navController = navController) }
//        composable("verify_account") { VerifyAccountScreen(navController = navController) }
//        composable("link_accounts") { LinkAccountsScreen(navController = navController) }
//        composable("delete_account") { DeleteAccountScreen(navController = navController) }
//
//        // Roles
//        composable("landlord") {
//            LandlordScreen(navController = navController, viewModel = authViewModel)
//        }
//
//        composable("admin") {
//            AdminScreen(navController = navController, viewModel = authViewModel)
//        }
//    }
//}
