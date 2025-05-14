package com.example.nhatro24_7.ui.screen.customer.booking

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nhatro24_7.data.model.BookingRequest
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nhatro24_7.navigation.Routes
import com.example.nhatro24_7.viewmodel.NotificationViewModel


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
            EnhancedTopAppBar(
                title = "Lịch sử đặt phòng",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
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
    var roomImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(request.roomId) {
        roomViewModel.getRoomById(request.roomId) { room ->
            roomTitle = room?.roomCategory ?: "Không rõ tên phòng"
            roomImage = room?.mainImage
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                roomViewModel.getRoomById(request.roomId) { room ->
                    room?.let {
                        navController.navigate("bookingDetailHistory/${request.roomId}/${request.id}")
                    }
                }
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)


    ) {
        Column {

            // Ảnh phòng nằm phía trên cùng của Card
            roomImage?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Ảnh phòng",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Phần thông tin bên dưới
            Column(modifier = Modifier.padding(16.dp)) {

                // Chip hiển thị tiêu đề phòng (có thể nhiều dòng)
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 6.dp)
//                ) {
//                    Surface(
//                        shape = RoundedCornerShape(50),
//                        color = Color(0xFFB2EBF2),
//                        tonalElevation = 2.dp
//                    ) {
//                        Text(
//                            text = roomTitle,
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.Black,
//                            modifier = Modifier
//                                .padding(horizontal = 12.dp, vertical = 6.dp)
//                        )
//                    }
//                }

                Spacer(Modifier.height(6.dp))

                StatusChipWithIcon(status = request.status)

                Spacer(Modifier.height(8.dp))

                val formatter = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
                Text(
                    text = "Đặt lúc: ${formatter.format(Date(request.timestamp))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                ActionSection(request, navController, roomTitle)

            }
        }
    }
}

@Composable
fun ActionSection(request: BookingRequest, navController: NavController, roomTitle: String) {

    val roomViewModel: RoomViewModel = viewModel()
    val context = LocalContext.current
    val notificationViewModel: NotificationViewModel = hiltViewModel()

    val returnSuccessMap by roomViewModel.returnSuccessMap.collectAsState()
    val cancelSuccessMap by roomViewModel.cancelSuccessMap.collectAsState()

    val returnSuccess = returnSuccessMap[request.id] == true
    val cancelSuccess = cancelSuccessMap[request.id] == true

    if (returnSuccess) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Trả phòng thành công", color = Color.Blue, fontWeight = FontWeight.Medium)
            Button(
                onClick = { navController.navigate("roomDetail/${request.roomId}") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Đặt lại")
            }
        }
        return
    }


    if (cancelSuccess) {
        Text("Hủy phòng thành công", color = Color.Red, fontWeight = FontWeight.Medium)
        Button(
            onClick = { navController.navigate("roomDetail/${request.roomId}") },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Đặt lại")
        }
        return
    }

    if (cancelSuccess == true) {
        Text("Hủy phòng thành công", color = Color.Red, fontWeight = FontWeight.Medium)
        Button(
            onClick = { navController.navigate("roomDetail/${request.roomId}") },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Đặt lại")
        }
        return
    }

    when (request.status) {
        "pending", "accepted" -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (request.status == "accepted") {
                    Button(
                        onClick = {
                            navController.navigate(Routes.paymentScreenRoute(request.id, request.roomId))
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Thanh toán ngay")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = {
                        roomViewModel.cancelBooking(request.id, request.roomId)
                        notificationViewModel.showNotification(
                            context = context,
                            title = "Huỷ đặt phòng",
                            message = "Bạn đã huỷ phòng $roomTitle thành công."
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Hủy phòng")
                }

            }
        }

        "paid" -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Thanh toán thành công",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
                OutlinedButton(
                    onClick = {
                        roomViewModel.returnRoom(request.id, request.roomId)
                        notificationViewModel.showNotification(
                            context = context,
                            title = "Trả phòng thành công",
                            message = "Cảm ơn bạn đã sử dụng dịch vụ phòng $roomTitle."
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Blue)
                ) {
                    Text("Trả phòng")
                }

            }
        }

        "cancelled" -> {
            Text("Đã hủy", color = Color.Gray, fontWeight = FontWeight.Medium)
        }

        "returned" -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Đã trả phòng",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Button(
                    onClick = {
                        navController.navigate("roomDetail/${request.roomId}")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Đặt lại")
                }
            }
        }

    }
}

@Composable
fun StatusChipWithIcon(status: String) {
    val (color, text, icon) = (
            when (status) {
                "pending" -> Triple(Color(0xFFFFA000), "Chờ duyệt", Icons.Default.Schedule)
                "accepted" -> Triple(Color(0xFF4CAF50), "Chấp nhận", Icons.Default.CheckCircle)
                "rejected" -> Triple(Color(0xFFF44336), "Từ chối", Icons.Default.Cancel)
                "paid" -> Triple(Color(0xFF2196F3), "Đã thanh toán", Icons.Default.Payments)
                "cancelled" -> Triple(Color.Gray, "Đã hủy", Icons.Default.Delete)
                "returned" -> Triple(Color(0xFF607D8B), "Đã trả phòng", Icons.Default.Home)
                else -> Triple(Color.Gray, "Không rõ", Icons.Default.Info)
            }
            )


    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EnhancedTopAppBar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4FC3F7),
            Color(0xFF2689F1)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .statusBarsPadding()
            .height(60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationIcon?.invoke()

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
