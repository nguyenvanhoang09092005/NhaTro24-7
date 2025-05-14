package com.example.nhatro24_7.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nhatro24_7.viewmodel.NotificationViewModel

@Composable
fun NotificationScreen(viewModel: NotificationViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Nội dung thông báo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.startNotificationWork(context, message)
            Toast.makeText(context, "Đã gửi thông báo!", Toast.LENGTH_SHORT).show()
        }) {
            Text("Gửi thông báo")
        }
    }
}
