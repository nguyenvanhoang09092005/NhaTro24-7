package com.example.nhatro24_7.ui.screen.customer.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhatro24_7.ui.screen.customer.component.BottomNavBar
import com.example.nhatro24_7.ui.screen.customer.component.CustomButton
import com.example.nhatro24_7.viewmodel.AuthViewModel

@Composable
fun CustomerHomeScreen(navController: NavController, viewModel: AuthViewModel) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Chào mừng bạn!", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ứng dụng giúp bạn tìm kiếm nhà trọ dễ dàng và nhanh chóng.", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(32.dp))
                CustomButton(text = "Bắt đầu ngay", onClick = {
                    navController.navigate("roomList")
                })
                Spacer(modifier = Modifier.height(12.dp))
                CustomButton(text = "Đăng xuất", onClick = {
                    viewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                })
                Spacer(modifier = Modifier.height(200.dp))
            }

            items(5) {
                Text("Ứng dụng giúp bạn tìm kiếm nhà trọ dễ dàng và nhanh chóng.", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(200.dp))
            }

            item {
                CustomButton(text = "Đăng xuất", onClick = {
                    viewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                })
            }
        }
    }

}
