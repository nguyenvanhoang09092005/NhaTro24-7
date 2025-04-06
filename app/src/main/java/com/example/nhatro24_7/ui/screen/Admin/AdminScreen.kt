package com.example.nhatro24_7.ui.screen.Admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhatro24_7.ui.screen.customer.component.CustomButton
import com.example.nhatro24_7.viewmodel.AuthViewModel

@Composable
fun AdminScreen(navController: NavController, viewModel: AuthViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Chào mừng Admin!", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Đăng xuất",
            onClick = {
                viewModel.signOut()
                navController.navigate("login") {
                    popUpTo("admin") { inclusive = true }

                }
            }
        )
    }
}
