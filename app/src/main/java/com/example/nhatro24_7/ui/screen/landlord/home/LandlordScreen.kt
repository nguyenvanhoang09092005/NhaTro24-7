package com.example.nhatro24_7.ui.screen.landlord.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel

@Composable
fun LandlordScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    roomViewModel: RoomViewModel = viewModel()
) {
//    val rooms by roomViewModel.rooms.collectAsState() // nếu là StateFlow/LiveData

    Scaffold(bottomBar = {
        com.example.nhatro24_7.ui.screen.landlord.component.BottomNavBar(navController = navController)
    }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Chào mừng chủ trọ!", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Dưới đây là danh sách phòng trọ mới nhất.", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(24.dp))
                com.example.nhatro24_7.ui.screen.customer.component.CustomButton(text = "Đăng xuất") {
                    viewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }


        }
    }
}
