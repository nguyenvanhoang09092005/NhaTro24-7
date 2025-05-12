package com.example.nhatro24_7.ui.screen.chat

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.nhatro24_7.data.model.Message
import com.example.nhatro24_7.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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
    val coroutineScope = rememberCoroutineScope()

    val selectedMessages = remember { mutableStateListOf<String>() }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fileUri = uri
    }

    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            val imageUrl = uploadFileToCloudinary(context, uri)
            viewModel.sendMessage(
                chatId = chatId,
                message = Message(
                    senderId = currentUserId,
                    receiverId = receiverId,
                    imageUrl = imageUrl,
                    timestamp = System.currentTimeMillis()
                )
            )
            imageUri = null
        }
    }

    LaunchedEffect(fileUri) {
        fileUri?.let { uri ->
            val fileUrl = uploadFileToCloudinary(context, uri)
            viewModel.sendMessage(
                chatId = chatId,
                message = Message(
                    senderId = currentUserId,
                    receiverId = receiverId,
                    fileUrl = fileUrl,
                    fileName = uri.lastPathSegment ?: "Tập tin đính kèm",
                    timestamp = System.currentTimeMillis()
                )
            )
            fileUri = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!receiverAvatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = receiverAvatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = receiverName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                val isSender = message.senderId == currentUserId
                val messageKey = "${message.senderId}_${message.timestamp}"
                val isSelected = selectedMessages.contains(messageKey)
                var expandedImageUrl by remember { mutableStateOf<String?>(null) }
                val time = remember(message.timestamp) {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
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
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        if (isSelected) selectedMessages.remove(messageKey)
                                        else selectedMessages.add(messageKey)
                                    }
                                )
                            }
                            .background(
                                if (isSender) MaterialTheme.colorScheme.primary
                                else Color(0xE18444FF),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(10.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        message.imageUrl?.let { imageUrl ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .padding(2.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .widthIn(max = 240.dp)
                                        .wrapContentHeight()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { expandedImageUrl = imageUrl }
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

//                        if (expandedImageUrl != null) {
//                            Dialog(onDismissRequest = { expandedImageUrl = null }) {
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .background(Color.Black)
//                                        .clickable { expandedImageUrl = null },
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    AsyncImage(
//                                        model = expandedImageUrl,
//                                        contentDescription = "Full Image",
//                                        contentScale = ContentScale.Fit,
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .wrapContentHeight()
//                                            .padding(16.dp)
//                                    )
//                                }
//                            }
//                        }

                        message.fileUrl?.let { fileUrl ->
                            val context = LocalContext.current
                            val fileName = message.fileName ?: "Tập tin"

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(fileUrl), "*/*")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }

                                    try {
                                        context.startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(context, "Không có ứng dụng phù hợp để mở tệp này", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = FileIcon(fileName),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = fileName,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                        }



                        if (message.content.isNotBlank()) {
                            Text(
                                text = message.content,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        if (isSelected) {
                            Text(
                                text = if (isSender) "$time ${if (message.isRead) "✓✓" else "✓"}" else time,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    if (isSender) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            IconButton(onClick = { imagePicker.launch("image/*") }) {
                Icon(Icons.Default.Image, contentDescription = "Send Image")
            }

            IconButton(onClick = { filePicker.launch("*/*") }) {
                Icon(Icons.Default.AttachFile, contentDescription = "Send File")
            }

            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                placeholder = { Text("Nhập tin nhắn...") },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank() && fileUri != null) {
                        val uriCopy = fileUri
                        fileUri = null
                        coroutineScope.launch {
                            val fileUrl = uploadFileToCloudinary(context, uriCopy!!)
                            val fileName = viewModel.getFileName(context, uriCopy)
                            viewModel.sendMessage(
                                chatId,
                                Message(
                                    senderId = currentUserId,
                                    receiverId = receiverId,
                                    fileUrl = fileUrl,
                                    fileName = fileName,
                                    content = messageText,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            messageText = ""
                        }
                    }
                }

            ) {
                Icon(Icons.Default.Send, contentDescription = "Gửi")
            }
        }
    }

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }
}

@Composable
fun FileIcon(fileName: String) : ImageVector {
    return when {
        fileName.endsWith(".pdf", true) -> Icons.Default.PictureAsPdf
        fileName.endsWith(".doc", true) || fileName.endsWith(".docx", true) -> Icons.Default.Description
        fileName.endsWith(".xls", true) || fileName.endsWith(".xlsx", true) -> Icons.Default.GridOn
        fileName.endsWith(".zip", true) || fileName.endsWith(".rar", true) -> Icons.Default.FolderZip
        fileName.endsWith(".txt", true) -> Icons.Default.Article
        fileName.endsWith(".mp4", true) || fileName.endsWith(".avi", true) -> Icons.Default.Movie
        fileName.endsWith(".mp3", true) -> Icons.Default.MusicNote
        else -> Icons.Default.AttachFile
    }
}


suspend fun uploadFileToCloudinary(context: Context, uri: Uri): String {
    return withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val bytes = inputStream.readBytes()
        inputStream.close()
        val fileName = uri.lastPathSegment ?: "file"
        val cloudinary = Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", "dnkjhbw9m",
                "api_key", "791292363868727",
                "api_secret", "_5aBOAaLNCUabVPcyZMxwH-j1yY"
            )
        )

        val uploadResult = cloudinary.uploader().upload(
            bytes,
            ObjectUtils.asMap(
                "resource_type", "auto",
                "public_id", fileName
            )
        )
//
//        val uploadResult = cloudinary.uploader().upload(bytes, ObjectUtils.emptyMap())
        uploadResult["secure_url"] as String
    }
}