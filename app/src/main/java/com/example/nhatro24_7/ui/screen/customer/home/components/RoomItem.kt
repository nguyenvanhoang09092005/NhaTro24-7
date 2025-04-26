package com.example.nhatro24_7.ui.screen.customer.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nhatro24_7.data.model.Room
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RoomItem(room: Room) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateText = dateFormat.format(Date(room.created_at))

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = room.title, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = room.description, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Giá thuê: ${room.price.toInt()} VNĐ")
            Text(text = "Địa chỉ: ${room.location}")
            Text(text = "Ngày đăng: $dateText")
        }
    }
}
