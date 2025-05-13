package com.example.nhatro24_7.ui.screen.chat

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val userRole by authViewModel.userRole.collectAsState(initial = "customer")

    LaunchedEffect(currentUserId) {
        chatViewModel.loadChatList(currentUserId)
        authViewModel.fetchUserRole(currentUserId)
    }



    Scaffold(
        topBar = {
            EnhancedTopAppBarChat("Tin nhắn")
        },
        bottomBar = {
            if (userRole == "landlord") {
                com.example.nhatro24_7.ui.screen.landlord.component.BottomNavBar(navController = navController)
            } else {
                com.example.nhatro24_7.ui.screen.customer.component.BottomNavBar(navController = navController)
            }
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
//
//                            val route = Routes.customerChatRoute(chatId, receiverId, receiverName, receiverAvatarUrl)

//                            Log.d("ChatNavigation", "Navigating to: $route")
//                            Log.d("ChatNavigation", "Navigate to: customer_chat/${chat.chatId}/${chat.otherUserId}/${chat.otherUsername}/${chat.otherAvatarUrl}")

                            val route = if (userRole == "landlord") {
                                Routes.landlordChatRoute(chatId, receiverId, receiverName, receiverAvatarUrl)
                            } else {
                                Routes.customerChatRoute(chatId, receiverId, receiverName, receiverAvatarUrl)
                            }

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
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (chat.otherAvatarUrl.isNotBlank()) {
                AsyncImage(
                    model = chat.otherAvatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(54.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar placeholder",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.otherUsername,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = formatTimestamp(chat.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Top)
            )
        }
    }
}

@Composable
fun EnhancedTopAppBarChat(title: String) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4FC3F7), // Light Blue
            Color(0xFF2689F1)  // Darker Blue
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chat Icon",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

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
