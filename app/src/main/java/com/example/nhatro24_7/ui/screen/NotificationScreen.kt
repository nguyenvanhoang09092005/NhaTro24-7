//package com.example.nhatro24_7.ui.screen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import coil.compose.rememberAsyncImagePainter
//import com.example.nhatro24_7.data.model.Notification
//import com.example.nhatro24_7.viewmodel.NotificationViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationScreen(userId: String,viewModel: NotificationViewModel = viewModel()) {
//    val notifications by viewModel.notifications.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.loadNotifications(userId)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Thông báo") })
//        }
//    ) { paddingValues ->
//        if (notifications.isEmpty()) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("Không có thông báo nào")
//            }
//        } else {
//            LazyColumn(
//                contentPadding = paddingValues,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                items(notifications) { notification ->
//                    NotificationItem(notification) {
//                        viewModel.markAsRead(notification.id)
//                    }
//                }
//            }
//        }
//
//
//    }
//}
//
//@Composable
//fun NotificationItem(notification: Notification, onClick: () -> Unit) {
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .clickable { onClick() }
//    ) {
//        Row(
//            modifier = Modifier
//                .background(if (notification.isRead) Color.LightGray else Color.White)
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if (!notification.imageUrl.isNullOrEmpty()) {
//                Image(
//                    painter = rememberAsyncImagePainter(notification.imageUrl),
//                    contentDescription = null,
//                    modifier = Modifier.size(48.dp)
//                )
//            } else {
//                Icon(
//                    imageVector = Icons.Default.Notifications,
//                    contentDescription = null,
//                    modifier = Modifier.size(48.dp)
//                )
//            }
//            Spacer(modifier = Modifier.width(16.dp))
//            Column {
//                Text(notification.title, style = MaterialTheme.typography.titleMedium)
//                Text(notification.message, style = MaterialTheme.typography.bodyMedium)
//            }
//        }
//    }
//}
