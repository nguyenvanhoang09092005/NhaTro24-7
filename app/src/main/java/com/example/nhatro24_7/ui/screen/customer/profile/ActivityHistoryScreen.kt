package com.example.nhatro24_7.ui.screen.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nhatro24_7.data.model.ActivityLog
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.viewmodel.ActivityLogViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHistoryScreen(
    navController: NavController,
    activityLogViewModel: ActivityLogViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel()
) {
    val logs by activityLogViewModel.activityLogs.collectAsState()
    val userId = Firebase.auth.currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            activityLogViewModel.loadActivityLogsForUser(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử đã xem") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Bạn chưa xem phòng nào.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(logs) { log ->
                    ViewedRoomItem(log = log, roomViewModel = roomViewModel) {
                        log.roomId?.let {
                            navController.navigate("roomDetail/$it")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewedRoomItem(
    log: ActivityLog,
    roomViewModel: RoomViewModel,
    onClick: () -> Unit
) {
    var room by remember { mutableStateOf<Room?>(null) }

    LaunchedEffect(log.roomId) {
        log.roomId?.let { id ->
            roomViewModel.getRoomById(id) {
                room = it
            }
        }
    }

    room?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() },
            elevation = CardDefaults.cardElevation(8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                // Ảnh phòng
                Image(
                    painter = rememberImagePainter(it.mainImage),
                    contentDescription = "Ảnh phòng",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Thông tin phòng
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = it.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Giá: ${it.price.toInt()} VNĐ/tháng", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Địa chỉ: ${it.location}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Xem lúc: ${formatTimestamp(log.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
