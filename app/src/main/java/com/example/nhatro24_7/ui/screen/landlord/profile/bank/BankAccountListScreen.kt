package com.example.nhatro24_7.ui.screen.landlord.profile.bank


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nhatro24_7.data.model.User
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.R

@Composable
fun BankAccountListScreen(navController: NavController, viewModel: AuthViewModel) {
    val bankAccounts = remember { mutableStateOf<List<User>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.getBankAccounts { accounts ->
            bankAccounts.value = accounts
            isLoading.value = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()) // tránh tai thỏ
            .padding(16.dp)
    ) {
        Text(
            "Danh sách tài khoản ngân hàng",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (bankAccounts.value.isEmpty()) {
                Text("Không có tài khoản ngân hàng nào.")
            } else {
                bankAccounts.value.forEach { account ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Giả định bạn có map tên ngân hàng -> logo tương ứng
                            val bankLogoMap = mapOf(
                                "Vietcombank" to R.drawable.vietcombank,
                                "BIDV" to R.drawable.bidv,
                                "Agribank" to R.drawable.agribank,
                                "Techcombank" to R.drawable.techcombank,
                                "VPBank" to R.drawable.vpbank,
                                "Sacombank" to R.drawable.sacombank,
                                "TPBank" to R.drawable.tpbank,
                                "OCB" to R.drawable.ocb,
                                "MB" to R.drawable.mb,
                                "VIB" to R.drawable.vib,
                                "VietCapital Bank" to R.drawable.vietcapital,
                                "Saigonbank" to R.drawable.saigonbank,
                                "Viẹtinbank" to R.drawable.vietinbank,
                                "HDBank" to R.drawable.hdbank,
                                "SeABank" to R.drawable.seabank,
                                "ABBANK" to R.drawable.abbank,
                                "BAC A BANK" to R.drawable.bacabank,
                                "VRB" to R.drawable.vrb,
                                "GPBank" to R.drawable.gpbank,
                                "PG Bank" to R.drawable.pgbank,
                                "OceanBank" to R.drawable.oceanbank,
                                "PVcomBank" to R.drawable.pvcombank,
                                "ACB" to R.drawable.acb,
                                "VietBank" to R.drawable.vietbank
                            )
                            val logoResId = bankLogoMap[account.landlordBankName] ?: R.drawable.avatar



                            Image(
                                painter = painterResource(id = logoResId),
                                contentDescription = account.landlordBankName,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 16.dp)
                            )

                            Column {
                                Text(
                                    "Chủ tài khoản: ${account.landlordName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Số tài khoản: ${account.landlordBankAccount}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Ngân hàng: ${account.landlordBankName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



