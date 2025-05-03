@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nhatro24_7.ui.screen.landlord.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class ActivityItem(
    val time: String,
    val date: String,
    val action: String
)

@Composable
fun ActivityHistoryScreen(navController: NavController) {
    val activities = listOf(
        ActivityItem("15:30", "04/04/2024", "Đã thích trọ Quận 1"),
        ActivityItem("10:05", "03/04/2024", "Đặt phòng Trọ A"),
        ActivityItem("18:45", "01/04/2024", "Đã xem Trọ B"),
        ActivityItem("09:20", "30/03/2024", "Hủy đặt phòng Trọ C")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử hoạt động") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ){ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(activities) { activity ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "• ${activity.time} | ${activity.date} - ${activity.action}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Divider(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}
