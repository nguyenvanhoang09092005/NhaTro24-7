package com.example.nhatro24_7.ui.screen.landlord.room

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nhatro24_7.data.model.BookingRequest
import com.example.nhatro24_7.viewmodel.NotificationViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingRequestsForLandlordScreen(
    navController: NavController,
    roomViewModel: RoomViewModel
) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()

    val context = LocalContext.current
    val landlordId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val bookingRequests = remember { mutableStateListOf<BookingRequest>() }
    val userNames = remember { mutableStateMapOf<String, String>() }
    val roomTitles = remember { mutableStateMapOf<String, String>() }

    // Lọc trạng thái
    val bookingStatusOptions = listOf("Tất cả", "pending", "accepted", "rejected")
    var selectedBookingStatus by remember { mutableStateOf("Tất cả") }

    fun refreshRequests() {
        roomViewModel.getBookingRequestsForLandlord(landlordId) { requests ->
            val filtered = if (selectedBookingStatus == "Tất cả") requests
            else requests.filter { it.status == selectedBookingStatus }
            bookingRequests.clear()
            bookingRequests.addAll(filtered)

            filtered.forEach { request ->
                if (!roomTitles.containsKey(request.roomId)) {
                    FirebaseFirestore.getInstance().collection("rooms")
                        .document(request.roomId)
                        .get()
                        .addOnSuccessListener { doc ->
                            val title = doc.getString("title") ?: "Không có tiêu đề"
                            roomTitles[request.roomId] = title
                        }
                }

                // Lấy tên người dùng
                if (!userNames.containsKey(request.userId)) {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(request.userId)
                        .get()
                        .addOnSuccessListener { doc ->
                            val name = doc.getString("username") ?: "Người dùng"
                            userNames[request.userId] = name
                        }
                        .addOnFailureListener {
                            userNames[request.userId] = "Không tìm thấy"
                        }
                }
            }
        }
    }

    LaunchedEffect(landlordId, selectedBookingStatus) {
        refreshRequests()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Yêu cầu đặt phòng", fontSize = 18.sp) })
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            EnhancedFilterSection(
                title = "Trạng thái yêu cầu",
                options = bookingStatusOptions,
                selectedOption = selectedBookingStatus,
                onOptionSelected = {
                    selectedBookingStatus = it
                    refreshRequests()
                }
            )

            if (bookingRequests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có yêu cầu nào.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(bookingRequests) { request ->

                        val roomTitle = roomTitles[request.roomId] ?: "Đang tải tên phòng..."
                        val userName by remember(request.userId) {
                            derivedStateOf {
                                userNames[request.userId] ?: "Đang tải người đặt..."
                            }
                        }


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(modifier = Modifier
                                .padding(16.dp)
                                .clickable {
                                    navController.navigate("booking_detail/${request.id}")
                                }) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text("Phòng: $roomTitle", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("Người đặt: $userName", fontSize = 14.sp)
                                    }

                                    val statusColor = when (request.status) {
                                        "accepted" -> Color(0xFF4CAF50)
                                        "rejected" -> Color.Red
                                        else -> MaterialTheme.colorScheme.primary
                                    }

                                    Surface(
                                        color = statusColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Text(
                                            text = request.status.replaceFirstChar { it.uppercaseChar() },
                                            color = statusColor,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Thời gian",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(request.timestamp)),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (request.status == "pending") {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Button(
                                            onClick = {
                                                roomViewModel.updateBookingStatus(request.id, "accepted") { success ->
                                                    if (success) {
                                                        roomViewModel.markRoomAsUnavailable(request.roomId)
                                                        Toast.makeText(context, "Đã chấp nhận", Toast.LENGTH_SHORT).show()

                                                        // Thông báo cho chủ trọ
                                                        notificationViewModel.showNotification(
                                                            context,
                                                            title = "Yêu cầu đặt phòng",
                                                            message = "Bạn đã chấp nhận yêu cầu của $userName cho phòng $roomTitle"
                                                        )

                                                        // Thông báo cho khách hàng
                                                        notificationViewModel.showNotification(
                                                            context,
                                                            title = "Yêu cầu đặt phòng",
                                                            message = "Chủ trọ đã chấp nhận yêu cầu của bạn cho phòng $roomTitle"
                                                        )

                                                        refreshRequests()
                                                    } else {
                                                        Toast.makeText(context, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                        ) {
                                            Text("Chấp nhận", color = Color.White)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                roomViewModel.updateBookingStatus(request.id, "rejected") { success ->
                                                    if (success) {
                                                        Toast.makeText(context, "Đã từ chối", Toast.LENGTH_SHORT).show()

                                                        // Thông báo cho chủ trọ
                                                        notificationViewModel.showNotification(
                                                            context,
                                                            title = "Yêu cầu bị từ chối",
                                                            message = "Bạn đã từ chối yêu cầu đặt phòng của $userName"
                                                        )

                                                        // Thông báo cho khách hàng
                                                        notificationViewModel.showNotification(
                                                            context,
                                                            title = "Yêu cầu bị từ chối",
                                                            message = "Yêu cầu đặt phòng của bạn đã bị từ chối bởi chủ trọ."
                                                        )

                                                        refreshRequests()
                                                    } else {
                                                        Toast.makeText(context, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                        ) {
                                            Text("Từ chối")
                                        }

                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedFilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val iconsMap = mapOf(
        "Tất cả" to Icons.Default.List,
        "pending" to Icons.Default.Schedule,
        "accepted" to Icons.Default.CheckCircle,
        "rejected" to Icons.Default.Cancel
    )


    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options.size) { index ->
                val option = options[index]
                val isSelected = option == selectedOption

                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    label = "backgroundColor"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "contentColor"
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = if (isSelected) 6.dp else 2.dp,
                    modifier = Modifier
                        .height(40.dp)
                        .toggleable(
                            value = isSelected,
                            onValueChange = { onOptionSelected(option) }
                        ),
                    color = backgroundColor
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = iconsMap[option] ?: Icons.Default.List,
                            contentDescription = option,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = option.replaceFirstChar { it.uppercaseChar() },
                            color = contentColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
