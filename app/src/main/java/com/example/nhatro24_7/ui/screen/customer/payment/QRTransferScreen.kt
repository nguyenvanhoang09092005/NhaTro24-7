//package com.example.nhatro24_7.ui.screen.customer.payment
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.example.nhatro24_7.data.model.User
//import com.example.nhatro24_7.util.generateQrCode
//import java.text.NumberFormat
//import java.util.*
//
//import androidx.compose.ui.graphics.asImageBitmap
//import android.graphics.Bitmap
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun QRTransferScreen(amount: Long, account: User, onBack: () -> Unit) {
//    val qrContent = """
//        STK: ${account.landlordBankAccount}
//        Chủ TK: ${account.fullName}
//        Ngân hàng: ${account.landlordBankName}
//        Số tiền: $amount VND
//        Nội dung: Thanh toán tiền phòng
//    """.trimIndent()
//
//    // Tạo mã QR và chuyển Bitmap thành ImageBitmap
//    val qrBitmap = remember { generateQrCode(qrContent) } // kiểu android.graphics.Bitmap
//    val qrImageBitmap = qrBitmap.asImageBitmap()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Chuyển khoản qua QR") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text("Quét mã QR bằng app ngân hàng", fontWeight = FontWeight.Medium)
//            Spacer(Modifier.height(16.dp))
//            Image(bitmap = qrImageBitmap, contentDescription = null, modifier = Modifier.size(300.dp))
//            Spacer(Modifier.height(8.dp))
//            Text("Số tiền: ${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)}")
//        }
//    }
//}
