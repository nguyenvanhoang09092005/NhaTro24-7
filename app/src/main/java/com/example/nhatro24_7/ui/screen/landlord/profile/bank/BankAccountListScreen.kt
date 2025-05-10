    package com.example.nhatro24_7.ui.screen.landlord.profile.bank


    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import com.example.nhatro24_7.data.model.User
    import com.example.nhatro24_7.viewmodel.AuthViewModel

    @Composable
    fun BankAccountListScreen(navController: NavController, viewModel: AuthViewModel) {
        // Đảm bảo việc sử dụng remember để lưu trữ trạng thái của bankAccounts và isLoading đúng cách.
        val bankAccounts = remember { mutableStateOf<List<User>>(emptyList()) }
        val isLoading = remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            // Gọi hàm để lấy danh sách tài khoản ngân hàng
            viewModel.getBankAccounts { accounts ->
                bankAccounts.value = accounts // Cập nhật giá trị vào MutableState
                isLoading.value = false
            }
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text("Danh sách tài khoản ngân hàng", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                if (bankAccounts.value.isEmpty()) {
                    Text("Không có tài khoản ngân hàng nào.")
                } else {
                    bankAccounts.value.forEach { account ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Chủ tài khoản: ${account.landlordName}")
                                Text("Số tài khoản: ${account.landlordBankAccount}")
                                Text("Ngân hàng: ${account.landlordBankName}")
                            }
                        }
                    }
                }
            }
        }
    }


