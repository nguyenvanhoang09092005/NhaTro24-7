package com.example.nhatro24_7.ui.screen.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nhatro24_7.R
import com.example.nhatro24_7.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Biến để lưu kết quả từ checkIfLoggedIn()
    var userRole by remember { mutableStateOf<String?>(null) }

    // Gọi checkIfLoggedIn() khi màn hình được dựng lần đầu
    LaunchedEffect(Unit) {
        viewModel.checkIfLoggedIn { role ->
            userRole = role
        }
    }

    // Khi đã nhận được role -> điều hướng sau delay ngắn
    LaunchedEffect(userRole) {
        userRole?.let { role ->
            delay(300) // Tạo cảm giác chuyển mượt mà hơn
            if (role != "guest") {
                onNavigateToHome(role)
            } else {
                onNavigateToLogin()
            }
        }
    }

    // Giao diện loading hiển thị trong khi chờ xác thực
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}
