package com.example.nhatro24_7.ui.screen.chat

import android.R.id.message
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nhatro24_7.data.model.Message
import com.example.nhatro24_7.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    currentUserId: String,
    chatId: String,
    receiverId: String,
    receiverName: String,
    receiverAvatarUrl: String?,
    onBack: () -> Unit = {}
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val messages by viewModel.messages.observeAsState(emptyList())
    var messageText by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val imageUrl = uploadFileToStorage(it) // TODO: Thay bằng upload Firebase
            viewModel.sendMessage(
                chatId,
                Message(
                    senderId = currentUserId,
                    receiverId = receiverId,
                    imageUrl = imageUrl
                )
            )
        }
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val fileUrl = uploadFileToStorage(it)
            viewModel.sendMessage(
                chatId,
                Message(
                    senderId = currentUserId,
                    receiverId = receiverId,
                    fileUrl = fileUrl,
                    fileName = uri.lastPathSegment ?: "File đính kèm"
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!receiverAvatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = receiverAvatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(receiverName)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = MaterialTheme.colors.surface
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                val isSender = message.senderId == currentUserId
                val time = remember(message.timestamp) {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                }
                Log.d("ChatDebug", "message.senderId = ${message.senderId}, currentUserId = $currentUserId")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    if (!isSender) {
                        AsyncImage(
                            model = receiverAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Column(
                        modifier = Modifier
                            .background(
                                if (isSender) Color(0xFF2196F3) else Color(0xFF9C27B0),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                            .widthIn(max = 250.dp)
                    ) {
                        message.imageUrl?.let {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        message.fileUrl?.let {
                            Text(
                                text = "📎 ${message.fileName ?: "File"}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.clickable {
                                    // TODO: mở file
                                }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        if (message.content.isNotBlank()) {
                            Text(
                                text = message.content,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Text(
                            text = if (isSender) {
                                "$time ${if (message.isRead) "✓✓" else "✓"}"
                            } else {
                                time
                            },
                            fontSize = MaterialTheme.typography.caption.fontSize,
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }

                    if (isSender) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(onClick = { imagePicker.launch("image/*") }) {
                Icon(Icons.Default.Image, contentDescription = "Send Image")
            }

            IconButton(onClick = { filePicker.launch("*/*") }) {
                Icon(Icons.Default.AttachFile, contentDescription = "Send File")
            }

            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhập tin nhắn...") },
                maxLines = 4
            )

            IconButton(onClick = {
                if (messageText.isNotBlank()) {
                    viewModel.sendMessage(
                        chatId,
                        Message(
                            senderId = currentUserId,
                            receiverId = receiverId,
                            content = messageText,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    Log.d("ChatScreen", "Sender: $currentUserId, Receiver: $receiverId")

                    messageText = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Gửi")
            }
        }
    }

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }
}

fun uploadFileToStorage(uri: Uri): String {
    // TODO: Upload file lên Firebase Storage và trả về URL tải về
    return uri.toString()
}
