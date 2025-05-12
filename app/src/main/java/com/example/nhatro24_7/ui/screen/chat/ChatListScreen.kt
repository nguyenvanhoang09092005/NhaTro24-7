package com.example.nhatro24_7.ui.screen.chat

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nhatro24_7.data.model.ChatItem
import com.example.nhatro24_7.navigation.Routes
import com.example.nhatro24_7.util.formatTimestamp
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel
) {
//    val currentUserId = authViewModel.currentUser.value?.id ?: return
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val chatItems by chatViewModel.chatList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        chatViewModel.loadChatList(currentUserId)
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tin nhắn") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (chatItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có cuộc trò chuyện nào.")
                }
            } else {
                LazyColumn {
                    items(chatItems) { chat ->
                        ChatListItem(chat = chat) {
                            val chatId = Uri.encode(chat.chatId)
                            val receiverId = Uri.encode(chat.otherUserId)
                            val receiverName = Uri.encode(chat.otherUsername)
                            val receiverAvatarUrl = Uri.encode(chat.otherAvatarUrl)

                            val route = Routes.customerChatRoute(chatId, receiverId, receiverName, receiverAvatarUrl)

                            Log.d("ChatNavigation", "Navigating to: $route")
                            Log.d("ChatNavigation", "Navigate to: customer_chat/${chat.chatId}/${chat.otherUserId}/${chat.otherUsername}/${chat.otherAvatarUrl}")


                            navController.navigate(route)
                        }

                        Divider()
                    }
                }

            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chat.otherAvatarUrl.isNotBlank()) {
            AsyncImage(
                model = chat.otherAvatarUrl,
                contentDescription = "avatar",
                modifier = Modifier.size(48.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "avatar placeholder",
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.otherUsername,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Text(
            text = formatTimestamp(chat.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
