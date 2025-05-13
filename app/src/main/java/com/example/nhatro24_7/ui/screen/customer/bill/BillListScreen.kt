package com.example.nhatro24_7.ui.screen.customer.bill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nhatro24_7.data.model.Bill
import com.example.nhatro24_7.viewmodel.BillViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BillListScreen(
    userId: String,
    billViewModel: BillViewModel = viewModel()
) {
    val bills = billViewModel.userBills
    val isLoading = billViewModel.isLoading

    LaunchedEffect(Unit) {
        billViewModel.loadUserBills(userId)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hóa đơn đã thanh toán", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (bills.isEmpty()) {
            Text("Bạn chưa có hóa đơn nào.")
        } else {
            LazyColumn {
                items(bills) { bill ->
                    BillItem(bill = bill)
                }
            }
        }
    }
}

@Composable
fun BillItem(bill: Bill) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = formatter.format(Date(bill.timestamp))

    Card(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Phòng: ${bill.roomId}", style = MaterialTheme.typography.titleMedium)
            Text("Số tiền: ${bill.amount} VND")
            Text("Ngày thanh toán: $date")
            Text("Trạng thái: ${bill.status}")
        }
    }
}
