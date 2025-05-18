package com.example.nhatro24_7.ui.screen.customer.payment

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.nhatro24_7.util.QRCodeGenerator
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeScreen(
    navController: NavHostController,
    amount: Long,
    transferContent: String,
    bookingRequestId: String,
    roomViewModel: RoomViewModel,
    userViewModel: AuthViewModel = hiltViewModel()
) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(bookingRequestId) {
        isProcessing = true
        roomViewModel.getLandlordInfoByBookingRequest(bookingRequestId) { landlord ->
            isProcessing = false
            if (landlord != null) {
                val content = """
                Số tiền: $amount VND
                Nội dung: $transferContent
                Ngân hàng: ${landlord.landlordBankName}
                Chủ tài khoản: ${landlord.username}
                Số tài khoản: ${landlord.landlordBankAccount}
            """.trimIndent()

                qrBitmap = QRCodeGenerator.generate(content)
            } else {
                // Hiển thị thông báo lỗi nếu cần
            }
        }
    }

//    LaunchedEffect(amount, transferContent) {
//        val content = "$amount\n$transferContent"
//        qrBitmap = QRCodeGenerator.generate(content)
//    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mã QR Thanh Toán") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (qrBitmap != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Quét mã QR để thanh toán", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(20.dp))

                Image(bitmap = qrBitmap!!.asImageBitmap(), contentDescription = null)
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        isProcessing = true
                        roomViewModel.updateBookingStatus(bookingRequestId, "paid") { success ->
                            isProcessing = false
                            if (success) {
                                navController.navigate("customer_booking_history") {
                                    popUpTo("customer_booking_history") { inclusive = true }
                                }
                            } else {
                                // Có thể hiển thị thông báo lỗi bằng Snackbar hoặc Dialog
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Xác nhận đã thanh toán", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

