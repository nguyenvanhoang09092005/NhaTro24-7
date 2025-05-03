package com.example.nhatro24_7.ui.screen.customer.booking

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun BookingPendingScreen(navController: NavController) {
    val context = LocalContext.current
    val roomViewModel = remember { RoomViewModel() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var bookingStatus by remember { mutableStateOf("pending") }

    LaunchedEffect(userId) {
        while (bookingStatus == "pending") {
            roomViewModel.getLatestBookingStatus(userId) { status ->
                bookingStatus = status
            }
            delay(3000)
        }

        when (bookingStatus) {
            "accepted" -> {
                Toast.makeText(context, "Yêu cầu được chấp nhận!", Toast.LENGTH_SHORT).show()
                navController.navigate("paymentScreen") // Chuyển đến màn thanh toán
            }
            "rejected" -> {
                Toast.makeText(context, "Yêu cầu đã bị từ chối!", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Quay lại trang trước
            }
        }
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("Đang chờ chủ trọ xác nhận...", fontSize = 16.sp)
    }
}
