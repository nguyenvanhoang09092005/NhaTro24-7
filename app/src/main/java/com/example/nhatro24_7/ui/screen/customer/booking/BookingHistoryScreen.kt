package com.example.nhatro24_7.ui.screen.customer.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhatro24_7.data.model.BookingRequest
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    navController: NavController,
    roomViewModel: RoomViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val bookingRequests by roomViewModel.getBookingRequestsFlow(userId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Lịch sử đặt phòng", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        if (bookingRequests.isEmpty()) {
            EmptyHistoryContent(padding)
        } else {
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
            ) {
                items(bookingRequests, key = { it.id }) { request ->
                    BookingRequestCard(request, roomViewModel, navController)
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryContent(padding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Bạn chưa có lịch sử đặt phòng nào!",
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BookingRequestCard(
    request: BookingRequest,
    roomViewModel: RoomViewModel,
    navController: NavController
) {
    var roomTitle by remember { mutableStateOf("Đang tải...") }

    LaunchedEffect(request.roomId) {
        roomViewModel.getRoomById(request.roomId) { room ->
            roomTitle = room?.title ?: "Không rõ tên phòng"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = roomTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))
            StatusChip(status = request.status)
            Spacer(Modifier.height(8.dp))

            val formatter = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
            Text(
                text = "Đặt lúc: ${formatter.format(Date(request.timestamp))}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            ActionSection(request, navController)
        }
    }
}

@Composable
fun ActionSection(request: BookingRequest, navController: NavController) {
    when (request.status) {
        "accepted" -> {
            Spacer(Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = {
                        navController.navigate("paymentScreen/${request.id}/${request.roomId}")
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Thanh toán ngay")
                }
            }

        }
        "paid" -> {
            Spacer(Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = "Thanh toán thành công",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }

    }
}

@Composable
fun StatusChip(status: String) {
    val (color, text) = when (status) {
        "pending" -> Color(0xFFFFA000) to "Chờ duyệt"
        "accepted" -> Color(0xFF4CAF50) to "Chấp nhận"
        "rejected" -> Color(0xFFF44336) to "Từ chối"
        "paid" -> Color(0xFF2196F3) to "Đã thanh toán"
        else -> Color.Gray to "Không rõ"
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
