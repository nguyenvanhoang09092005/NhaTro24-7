package com.example.nhatro24_7.ui.screen.customer.payment

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.navigation.Routes.qrTransferScreenRoute
import com.example.nhatro24_7.viewmodel.PaymentViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.NumberFormat
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavHostController,
    roomId: String,
//    userId: String,
//    amount: Double,
    bookingRequestId: String,
    roomViewModel: RoomViewModel,
    paymentViewModel: PaymentViewModel = hiltViewModel()
) {
    var room by remember { mutableStateOf<Room?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedPayment by remember { mutableStateOf("Chuyển khoản") }
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    LaunchedEffect(paymentViewModel.paymentSuccess) {
        if (paymentViewModel.paymentSuccess) {
            navController.navigate("payment_success") {
                popUpTo("payment") { inclusive = true } // Xóa khỏi backstack nếu muốn
            }
        }
    }

    // Lấy thông tin phòng
    LaunchedEffect(roomId) {
        roomViewModel.getRoomById(roomId) { fetchedRoom ->
            room = fetchedRoom
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh Toán", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            room?.let { currentRoom ->
                val landlordBankAccount = currentRoom.landlordBankAccount
                val landlordBankName = currentRoom.landlordBankName
                val landlordName = currentRoom.landlordName

                val totalAmount = currentRoom.price
                val transferNote = "ThanhToanPhong-${roomId}"

                // Tạo nội dung chuyển khoản
                val bankTransferContent = generateBankTransferContent(
                    bankName = landlordBankName,
                    accountNumber = landlordBankAccount,
                    accountName = landlordName,
                    amount = totalAmount,
                    note = transferNote
                )

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Ảnh phòng
                    Image(
                        painter = rememberAsyncImagePainter(currentRoom.mainImage),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(16.dp))

                    // Thông tin phòng
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().shadow(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Thông Tin Phòng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(6.dp))
                            Text(currentRoom.title, fontWeight = FontWeight.SemiBold)
                            Text("Vị trí: ${currentRoom.location}")
                            Text("Diện tích: ${currentRoom.area} m²")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Chi tiết thanh toán
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().shadow(2.dp)
                    ) {
                        val total = currentRoom.price
                        Column(Modifier.padding(16.dp)) {
                            Text("Chi Tiết Thanh Toán", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tiền thuê")
                                Text(formatter.format(currentRoom.price))
                            }

                            Divider(Modifier.padding(vertical = 6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tổng cộng", fontWeight = FontWeight.Bold)
                                Text(formatter.format(total), fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Hạn thanh toán: 30/${Calendar.getInstance().get(Calendar.MONTH)+1}/${Calendar.getInstance().get(Calendar.YEAR)}")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Phương thức thanh toán
                    var selectedPayment by remember { mutableStateOf("Thẻ ngân hàng") }
                    val payments = listOf("Thẻ ngân hàng", "Ví điện tử", "Chuyển khoản", "Tiền mặt")

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().shadow(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Phương Thức Thanh Toán", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)

                            payments.forEach { payment ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPayment = payment }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selectedPayment == payment, onClick = { selectedPayment = payment })
                                    Spacer(Modifier.width(8.dp))
                                    Text(payment, style = MaterialTheme.typography.bodyLarge)
                                }
                            }

                            // Nội dung phương thức thanh toán
                            if (selectedPayment in listOf("Thẻ ngân hàng", "Ví điện tử")) {
                                Column(Modifier.padding(top = 8.dp)) {
                                    OutlinedTextField(
                                        value = "", onValueChange = {},
                                        label = { Text("Số thẻ/Tài khoản") },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    OutlinedTextField(
                                        value = "", onValueChange = {},
                                        label = { Text("Tên chủ thẻ") },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Row {
                                        OutlinedTextField(
                                            value = "", onValueChange = {},
                                            label = { Text("Ngày hết hạn") },
                                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        OutlinedTextField(
                                            value = "", onValueChange = {},
                                            label = { Text("CVV") },
                                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Nút thanh toán
                    Button(
                        onClick = {
//                            roomViewModel.updateBookingStatus(bookingRequestId, "paid") { success ->
//                                if (success) {
//                                    navController.navigate("customer_booking_history") {
//                                        popUpTo("customer_booking_history") { inclusive = true }
//                                    }
//                                } else {
//                                    // Xử lý khi cập nhật thất bại
//                                }
//                            }
                            if (selectedPayment == "Chuyển khoản") {
                                // Tạo nội dung chuyển khoản
                                val transferContent = generateBankTransferContent(
                                    bankName = landlordBankName,
                                    accountNumber = landlordBankAccount,
                                    accountName = landlordName,
                                    amount = totalAmount,
                                    note = transferNote
                                )

                                // Chuyển đến màn hình tạo mã QR
                                val amountLong = totalAmount.toLong()
                                val encodedContent = URLEncoder.encode(transferContent, StandardCharsets.UTF_8.toString())
                                val currentUserId = Firebase.auth.currentUser?.uid ?: ""
                                paymentViewModel.makePaymentWithAutoInfo(currentRoom, currentUserId)
                                navController.navigate("qr_transfer_screen/$amountLong/$encodedContent/$bookingRequestId")


                            }
                        },

                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Thanh toán ngay ${formatter.format(totalAmount)}", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Hỗ trợ: 1900 1234 - Nhà Trọ 24/7 luôn bảo mật thông tin!",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(12.dp))
                }
            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Không tìm thấy thông tin phòng!")
            }
        }
    }
}

fun generateBankTransferContent(
    bankName: String,
    accountNumber: String,
    accountName: String,
    amount: Double,
    note: String
): String {
    return """
        ➤ Ngân hàng: $bankName
        ➤ Số tài khoản: $accountNumber
        ➤ Tên tài khoản: $accountName
        ➤ Số tiền: ${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)}
        ➤ Nội dung chuyển khoản: $note
    """.trimIndent()
}

