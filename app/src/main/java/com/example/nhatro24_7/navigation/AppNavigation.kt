package com.example.nhatro24_7.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nhatro24_7.data.model.User
import com.example.nhatro24_7.ui.screen.Admin.AdminScreen
import com.example.nhatro24_7.ui.screen.Auth.LoginScreen
import com.example.nhatro24_7.ui.screen.Auth.RegisterScreen
import com.example.nhatro24_7.ui.screen.Auth.SplashScreen
import com.example.nhatro24_7.ui.screen.chat.ChatListScreen
import com.example.nhatro24_7.ui.screen.chat.ChatScreen
import com.example.nhatro24_7.ui.screen.customer.ActivityHistoryScreen
import com.example.nhatro24_7.ui.screen.customer.booking.BookingHistoryScreen
import com.example.nhatro24_7.ui.screen.customer.booking.BookingPendingScreen
import com.example.nhatro24_7.ui.screen.customer.detail.LandlordDetailScreen
import com.example.nhatro24_7.ui.screen.customer.home.CustomerHomeScreen
import com.example.nhatro24_7.ui.screen.customer.home.RoomDetailScreen
import com.example.nhatro24_7.ui.screen.customer.payment.PaymentScreen
import com.example.nhatro24_7.ui.screen.customer.policy.TermsAndPolicyScreen
//import com.example.nhatro24_7.ui.screen.customer.payment.QRTransferScreen
import com.example.nhatro24_7.ui.screen.customer.profile.*
import com.example.nhatro24_7.ui.screen.customer.profile.account.ChangePasswordScreen
import com.example.nhatro24_7.ui.screen.customer.profile.account.DeleteAccountScreen
import com.example.nhatro24_7.ui.screen.customer.profile.account.LinkAccountsScreen
import com.example.nhatro24_7.ui.screen.customer.profile.account.VerifyAccountScreen
import com.example.nhatro24_7.ui.screen.customer.saveroom.SavedRoomScreen
import com.example.nhatro24_7.ui.screen.customer.search.SearchScreen
import com.example.nhatro24_7.ui.screen.landlord.home.LandlordScreen
import com.example.nhatro24_7.ui.screen.landlord.profile.LandlordProfileScreen
import com.example.nhatro24_7.ui.screen.landlord.profile.ProfileLandlordScreen
import com.example.nhatro24_7.ui.screen.landlord.profile.bank.AddBankAccountScreen
import com.example.nhatro24_7.ui.screen.landlord.profile.bank.BankAccountListScreen
import com.example.nhatro24_7.ui.screen.landlord.profile.bank.PaymentMethodsScreen
import com.example.nhatro24_7.ui.screen.landlord.room.AddRoomScreen
import com.example.nhatro24_7.ui.screen.landlord.room.BookingRequestDetailScreen
import com.example.nhatro24_7.ui.screen.landlord.room.BookingRequestsForLandlordScreen
import com.example.nhatro24_7.ui.screen.landlord.room.RoomListScreen
import androidx.compose.runtime.getValue
import com.example.nhatro24_7.navigation.Routes.LANDLORD_STATISTIC_ROUTE
import com.example.nhatro24_7.ui.screen.customer.payment.QRCodeScreen
import com.example.nhatro24_7.ui.screen.landlord.LandlordStatisticScreen
import com.example.nhatro24_7.ui.screen.landlord.room.RoomDetailLandlord


import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import java.net.URLDecoder
import com.example.nhatro24_7.viewmodel.ChatViewModel
import com.example.nhatro24_7.viewmodel.PaymentViewModel
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    romViewModel: RoomViewModel,
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

//        composable(
//            route = "bookingDetailHistory/{roomId}/{bookingId}",
//            arguments = listOf(
//                navArgument("roomId") { type = NavType.StringType },
//                navArgument("bookingId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
//            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
//            BookingDetailHistoryScreen(
//                navController = navController,
//                roomId = roomId,
//                bookingId = bookingId
//            )
//        }

        composable(Routes.CUSTOMER_SAVED) {
            SavedRoomScreen(navController, authViewModel, RoomViewModel())
        }


        composable(Routes.BOOKING_PENDING) {
            BookingPendingScreen(navController)
        }

        composable(
            route = Routes.PAYMENT_SCREEN_WITH_ARGS,
            arguments = listOf(
                navArgument("bookingRequestId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingRequestId = backStackEntry.arguments?.getString("bookingRequestId") ?: ""
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""

            val roomViewModel: RoomViewModel = viewModel()
            val paymentViewModel: PaymentViewModel = viewModel()

            PaymentScreen(
                navController = navController,
                roomId = roomId,
                bookingRequestId = bookingRequestId,
                roomViewModel = roomViewModel,
                paymentViewModel = paymentViewModel
            )
        }

        composable(
            route = "qr_transfer_screen/{amount}/{transferContent}",
            arguments = listOf(
                navArgument("amount") { type = NavType.LongType },
                navArgument("transferContent") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getLong("amount") ?: 0L
            val encodedTransferContent = backStackEntry.arguments?.getString("transferContent") ?: ""
            val transferContent = URLDecoder.decode(encodedTransferContent, StandardCharsets.UTF_8.toString())

            QRCodeScreen(
                navController = navController,
                amount = amount,
                transferContent = transferContent, // dùng đúng tên
                onBack = { navController.popBackStack() }
            )
        }



        composable("landlord_profile/{landlordId}") { backStackEntry ->
            val landlordId = backStackEntry.arguments?.getString("landlordId") ?: ""
            LandlordDetailScreen(
                landlordId = landlordId,
                navController = navController,
                roomViewModel = RoomViewModel()
            )
        }

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
//
        composable(Routes.CUSTOMER_HISTORY) {
            ActivityHistoryScreen(navController = navController)
        }


        composable(Routes.CUSTOMER_PROFILE_DETAIL) {
            UserProfileScreen(viewModel = authViewModel)
        }
        composable(Routes.CUSTOMER_LIKED_HISTORY) {
            LikedHistoryScreen(navController = navController)
        }
        composable(Routes.CUSTOMER_BOOKING_HISTORY) {
            BookingHistoryScreen(navController = navController, roomViewModel = RoomViewModel())
        }
        //search
        composable(Routes.CUSTOMER_SEARCH) {
            SearchScreen(navController = navController, viewModel = RoomViewModel())
        }
        composable(
            "qr_transfer_screen/{amount}/{userJson}",
            arguments = listOf(
                navArgument("amount") { type = NavType.LongType },
                navArgument("userJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
//            val amount = backStackEntry.arguments?.getLong("amount") ?: 0L
//            val userJson = backStackEntry.arguments?.getString("userJson") ?: ""
//            val user = Gson().fromJson(URLDecoder.decode(userJson, "UTF-8"), User::class.java)
//
//            QRTransferScreen(
//                amount = amount,
//                account = user,
//                onBack = { navController.popBackStack() }
//            )
        }
        composable(Routes.TERMANDPOLICY) {
            TermsAndPolicyScreen(navController = navController)
        }


        // Landlord
        composable(Routes.LANDLORD_HOME) {
            LandlordScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Routes.LANDLORD_ADD_POST) {
            AddRoomScreen(navController = navController, roomViewModel = RoomViewModel())
        }
        composable(Routes.LANDLORD_PROFILE) {
            ProfileLandlordScreen(
                navController = navController,
                viewModel = authViewModel,
                isDarkMode = isDarkTheme,
                selectedLanguage = selectedLanguage,
                onThemeToggle = onToggleTheme,
                onLanguageChange = onLanguageChange
            )
        }
        composable(route = Routes.LANDLORD_PROFILE_DETAIL) { navBackStackEntry ->
            LandlordProfileScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }

        composable(Routes.LANDLORD_BOOKING_REQUESTS) {
            val roomViewModel: RoomViewModel = viewModel()
            BookingRequestsForLandlordScreen(
                navController = navController,
                roomViewModel = roomViewModel
            )
        }

        composable(route = Routes.ROOM_LIST) {
            val roomViewModel: RoomViewModel = hiltViewModel()
            val roomsByLandlord by roomViewModel.roomsByLandlord.collectAsState()

            LaunchedEffect(key1 = true) {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserId != null) {
                    roomViewModel.getRoomsByLandlord(currentUserId)
                }
            }

            RoomListScreen(
                navController = navController,
                roomViewModel = roomViewModel,
                roomsByLandlord = roomsByLandlord
            )
        }

        composable("room_detail_landlord/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            val roomViewModel: RoomViewModel = hiltViewModel()
            RoomDetailLandlord(roomId = roomId, navController = navController, roomViewModel = roomViewModel)
        }

        composable("payment_methods") {
            PaymentMethodsScreen(viewModel = authViewModel,
                navController = navController)
        }

        composable("list_bank_accounts") {
            BankAccountListScreen(viewModel = authViewModel,
                navController = navController)
        }

        composable("add_bank_account") {
            AddBankAccountScreen(viewModel = authViewModel,
                navController = navController)
        }


//        composable(Routes.LANDLORD_NOTIFY) {
//            NotificationScreen()
//        }


        composable("booking_detail/{bookingRequestId}") { backStackEntry ->
            val bookingRequestId = backStackEntry.arguments?.getString("bookingRequestId") ?: ""
            BookingRequestDetailScreen(bookingRequestId, navController, RoomViewModel())
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

        composable(
            route = "$LANDLORD_STATISTIC_ROUTE/{landlordId}",
            arguments = listOf(navArgument("landlordId") { type = NavType.StringType })
        ) { backStackEntry ->
            val landlordId = backStackEntry.arguments?.getString("landlordId") ?: ""
            LandlordStatisticScreen(landlordId)
        }

        //chat
        composable(Routes.CHAT_LIST) {
            ChatListScreen(
                navController = navController,
                chatViewModel = chatViewModel,
                authViewModel = authViewModel
//                roomViewModel = RoomViewModel()
            )
        }

        composable(
            route = Routes.CUSTOMER_CHAT,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("receiverId") { type = NavType.StringType },
                navArgument("receiverName") { type = NavType.StringType },
                navArgument("receiverAvatarUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: return@composable
//            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: "Người dùng"
            val receiverName = Uri.decode(backStackEntry.arguments?.getString("receiverName") ?: "Người dùng")
            val receiverAvatarUrl = Uri.decode(backStackEntry.arguments?.getString("receiverAvatarUrl") ?: "")

            val currentUserId = authViewModel.currentUser.value?.id ?: ""

            ChatScreen(
                viewModel = chatViewModel,
                currentUserId = currentUserId,
                chatId = chatId,
                receiverId = receiverId,
                receiverName = receiverName,
                receiverAvatarUrl = receiverAvatarUrl,
                navController = navController,
            )
        }

        composable(
            route = Routes.LANDLORD_CHAT,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("receiverId") { type = NavType.StringType },
                navArgument("receiverName") { type = NavType.StringType },
                navArgument("receiverAvatarUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: return@composable
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: "Người dùng"
            val receiverAvatarUrl = backStackEntry.arguments?.getString("receiverAvatarUrl") ?: ""
            val currentUserId = authViewModel.currentUser.value?.id ?: ""

            ChatScreen(
                viewModel = chatViewModel,
                currentUserId = currentUserId,
                chatId = chatId,
                receiverId = receiverId,
                receiverName = receiverName,
                receiverAvatarUrl = receiverAvatarUrl,
                navController = navController,
            )
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
