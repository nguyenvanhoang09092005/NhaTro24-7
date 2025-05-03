package com.example.nhatro24_7.ui.screen.notification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

data class NotificationItem(
    val id: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(userId: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val notifications = remember { mutableStateListOf<NotificationItem>() }

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, _ ->
                    notifications.clear()
                    snapshots?.forEach { doc ->
                        notifications.add(
                            NotificationItem(
                                id = doc.id,
                                message = doc.getString("message") ?: "",
                                timestamp = doc.getLong("timestamp") ?: 0L
                            )
                        )
                    }
                }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Thông báo", fontSize = 18.sp) })
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Không có thông báo nào.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(notifications) { noti ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(noti.message, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(noti.timestamp)),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
