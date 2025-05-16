package com.example.nhatro24_7.ui.screen.landlord.profile.bank

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.R

data class Bank(val name: String, val logoRes: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBankAccountScreen(navController: NavController, viewModel: AuthViewModel) {
    var accountNumber by remember { mutableStateOf("") }
    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    var accountHolder by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val banks = listOf(
        Bank("Vietcombank", R.drawable.vietcombank),
        Bank("BIDV", R.drawable.bidv),
        Bank("Agribank", R.drawable.agribank),
        Bank("Techcombank", R.drawable.techcombank),
        Bank("VPBank", R.drawable.vpbank),
        Bank("Sacombank", R.drawable.sacombank),
        Bank("TPBank", R.drawable.tpbank),
        Bank("OCB", R.drawable.ocb),
        Bank("MB", R.drawable.mb),
        Bank("VIB", R.drawable.vib),
        Bank("VietCapital Bank", R.drawable.vietcapital),
        Bank("Saigonbank", R.drawable.saigonbank),
        Bank("Viẹtinbank", R.drawable.vietinbank),
        Bank("HDBank", R.drawable.hdbank),
        Bank("SeABank", R.drawable.seabank),
        Bank("ABBANK", R.drawable.abbank),
        Bank("BAC A BANK", R.drawable.bacabank),
        Bank("VRB", R.drawable.vrb),
        Bank("GPBank", R.drawable.gpbank),
        Bank("PG Bank", R.drawable.pgbank),
        Bank("OceanBank", R.drawable.oceanbank),
        Bank("PVcomBank", R.drawable.pvcombank),
        Bank("ACB", R.drawable.acb),
        Bank("VietBank", R.drawable.vietbank)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Tiêu đề
        Text(
            text = "Thêm tài khoản ngân hàng",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chủ tài khoản
        OutlinedTextField(
            value = accountHolder,
            onValueChange = { accountHolder = it },
            label = { Text("Chủ tài khoản") },
            placeholder = { Text("Nhập tên chủ tài khoản") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Số tài khoản
        OutlinedTextField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = { Text("Số tài khoản") },
            placeholder = { Text("Nhập số tài khoản") },
            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Chọn ngân hàng", fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Grid ngân hàng
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 180.dp)
        ) {
            items(banks) { bank ->
                val isSelected = selectedBank == bank
                Column(
                    modifier = Modifier
                        .height(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedBank = bank }
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = bank.logoRes),
                        contentDescription = bank.name,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = bank.name,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // Thông báo lỗi nếu có
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Nút lưu
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = {
                    if (accountNumber.isEmpty() || selectedBank == null || accountHolder.isEmpty()) {
                        errorMessage = "Vui lòng điền đầy đủ thông tin."
                        return@Button
                    }

                    isLoading = true
                    errorMessage = ""

                    viewModel.addBankAccount(
                        accountNumber,
                        selectedBank!!.name,
                        accountHolder
                    ) { success ->
                        isLoading = false
                        if (success) navController.popBackStack()
                        else errorMessage = "Lỗi khi lưu tài khoản. Vui lòng thử lại."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lưu tài khoản", fontSize = 16.sp)
            }
        }
    }

}


