package com.example.nhatro24_7.ui.screen.customer.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Thanh toán", fontSize = 18.sp) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Thông tin thanh toán", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

            // Thêm chi tiết hóa đơn hoặc phương thức thanh toán ở đây
            Text("Số tiền: 2.000.000 VNĐ")
            Button(onClick = {
                // Xử lý thanh toán
                navController.popBackStack()
            }) {
                Text("Xác nhận thanh toán")
            }
        }
    }
}
