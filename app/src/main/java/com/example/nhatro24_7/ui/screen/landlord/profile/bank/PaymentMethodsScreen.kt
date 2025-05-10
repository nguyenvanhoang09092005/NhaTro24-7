package com.example.nhatro24_7.ui.screen.landlord.profile.bank


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nhatro24_7.viewmodel.AuthViewModel

@Composable
fun PaymentMethodsScreen( navController: NavController,
                          viewModel: AuthViewModel,) {
    val paymentMethods = listOf("Chuyển khoản ngân hàng", "Tiền mặt", "Ví điện tử (ZaloPay, MoMo)")
    var selectedMethod by remember { mutableStateOf(paymentMethods.first()) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Phương thức thanh toán", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        paymentMethods.forEach { method ->
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = method == selectedMethod,
                    onClick = { selectedMethod = method }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(method)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // TODO: Xử lý lưu phương thức thanh toán
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Xác nhận")
        }
    }
}
