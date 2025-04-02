package com.example.nhatro24_7.ui.screen.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhatro24_7.R

@Composable
fun HomeScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.logo), // Thay thế bằng logo của ứng dụng
//                contentDescription = "Logo Nhà Trọ 24/7",
//                modifier = Modifier.size(150.dp)
//            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Chào mừng đến với Nhà Trọ 24/7",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Ứng dụng giúp bạn tìm kiếm nhà trọ dễ dàng và nhanh chóng!",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { /* Chuyển hướng đến màn hình chính */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Bắt đầu ngay", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }) {
                Text(text = "Đăng xuất")
            }
        }
    }
}
