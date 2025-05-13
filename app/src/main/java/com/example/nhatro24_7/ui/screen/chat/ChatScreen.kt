package com.example.nhatro24_7.ui.screen.chat

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.navigation.NavController
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
import java.util.jar.Manifest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    currentUserId: String,
    chatId: String,
    receiverId: String,
    receiverName: String,
    receiverAvatarUrl: String?,
    navController: NavController,
    onBack: () -> Unit = {}
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val messages by viewModel.messages.observeAsState(emptyList())
    var messageText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val selectedMessages = remember { mutableStateListOf<String>() }
    var showAttachmentOptions by remember { mutableStateOf(false) }
    var expandedImageUrl by remember { mutableStateOf<String?>(null) }

    // Định nghĩa các màu cho tin nhắn
    val senderMessageColor = Color(0xFF4776E6)
    val receivedMessageColor = Color(0xFFF3F4F6)
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        showAttachmentOptions = false
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fileUri = uri
        showAttachmentOptions = false
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Thanh tiêu đề cải tiến
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* Có thể thêm chức năng hiển thị hồ sơ */ }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!receiverAvatarUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(receiverAvatarUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = receiverName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "Trực tuyến",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Thêm chức năng gọi video */ }) {
                        Icon(
                            imageVector = Icons.Outlined.VideoCall,
                            contentDescription = "Video Call",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { /* Thêm chức năng gọi điện */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Call,
                            contentDescription = "Call",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(elevation = 4.dp)
            )

            // Nội dung tin nhắn
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    reverseLayout = true,
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(messages.reversed()) { message ->
                        val isSender = message.senderId == currentUserId
                        val messageKey = "${message.senderId}_${message.timestamp}"
                        val isSelected = selectedMessages.contains(messageKey)
                        val time = remember(message.timestamp) {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                        }
                        var showTimestamp by remember { mutableStateOf(false) }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isSender) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = receiverAvatarUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                if (isSelected) selectedMessages.remove(messageKey)
                                                else selectedMessages.add(messageKey)
                                                showTimestamp = !showTimestamp
                                            }
                                        )
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .shadow(
                                            elevation = 1.dp,
                                            shape = RoundedCornerShape(
                                                topStart = if (isSender) 16.dp else 4.dp,
                                                topEnd = if (!isSender) 16.dp else 4.dp,
                                                bottomStart = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        )
                                        .background(
                                            if (isSender) senderMessageColor else receivedMessageColor,
                                            shape = RoundedCornerShape(
                                                topStart = if (isSender) 16.dp else 4.dp,
                                                topEnd = if (!isSender) 16.dp else 4.dp,
                                                bottomStart = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    message.imageUrl?.let { imageUrl ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
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
                                                    .heightIn(max = 240.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { expandedImageUrl = imageUrl }
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    message.fileUrl?.let { fileUrl ->
                                        val context = LocalContext.current
                                        val fileName = message.fileName ?: "Tập tin"

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 4.dp)
                                                .clickable {
                                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                                        setDataAndType(Uri.parse(fileUrl), "*/*")
                                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                    }

                                                    try {
                                                        context.startActivity(intent)
                                                    } catch (e: ActivityNotFoundException) {
                                                        Toast.makeText(context, "Không có ứng dụng phù hợp để mở tệp này", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White.copy(alpha = 0.2f),
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = FileIcon(fileName),
                                                    contentDescription = null,
                                                    tint = if (isSender) Color.White else MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = fileName,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if (isSender) Color.White else MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "Nhấn để mở",
                                                        fontSize = 12.sp,
                                                        color = if (isSender) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }

                                    if (message.content.isNotBlank()) {
                                        Text(
                                            text = message.content,
                                            color = if (isSender) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }

                                    Text(
                                        text = if (isSender) "$time ${if (message.isRead) "✓✓" else "✓"}" else time,
                                        fontSize = 10.sp,
                                        color = if (isSender) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }

                            if (isSender) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }
                }

                // Indicator cho việc đang tải
                if (imageUri != null || fileUri != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 70.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.shadow(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Đang tải lên...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Thanh nhập tin nhắn cải tiến
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    AnimatedVisibility(
                        visible = showAttachmentOptions,
                        enter = fadeIn(animationSpec = tween(200)) +
                                slideInVertically(initialOffsetY = { it }, animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AttachmentOption(
                                icon = Icons.Outlined.Image,
                                label = "Hình ảnh",
                                color = Color(0xFF4CAF50),
                                onClick = { imagePicker.launch("image/*") }
                            )

                            AttachmentOption(
                                icon = Icons.Outlined.AttachFile,
                                label = "Tài liệu",
                                color = Color(0xFF2196F3),
                                onClick = { filePicker.launch("*/*") }
                            )

//                            val context = LocalContext.current
//
//                            AttachmentOption(
//                                icon = Icons.Outlined.LocationOn,
//                                label = "Vị trí",
//                                color = Color(0xFFFF9800),
//                                onClick = {
//                                    fetchCurrentLocation(
//                                        context = context,
//                                        onSuccess = { location ->
//                                            val latitude = location.latitude
//                                            val longitude = location.longitude
//                                            val message = "📍 Vị trí hiện tại: https://maps.google.com/?q=$latitude,$longitude"
//                                            sendMessage(message) // <-- đảm bảo bạn có định nghĩa hàm này
//                                        },
//                                        onFailure = {
//                                            Toast.makeText(context, "Không thể lấy vị trí", Toast.LENGTH_SHORT).show()
//                                        }
//                                    )
//                                }
//                            )


                            AttachmentOption(
                                icon = Icons.Outlined.Contacts,
                                label = "Liên hệ",
                                color = Color(0xFF9C27B0),
                                onClick = { /* Thêm chức năng chia sẻ liên hệ */ }
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        IconButton(
                            onClick = { showAttachmentOptions = !showAttachmentOptions },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = if (showAttachmentOptions) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Gửi tệp",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Nhắn gì đó...") },
                            maxLines = 4,
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .shadow(1.dp, RoundedCornerShape(24.dp))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
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
                                    messageText = ""
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (messageText.isNotBlank())
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.primaryContainer
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Gửi",
                                tint = if (messageText.isNotBlank())
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }



        // Dialog hiển thị hình ảnh đầy đủ
        if (expandedImageUrl != null) {
            Dialog(onDismissRequest = { expandedImageUrl = null }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f))
                        .clickable { expandedImageUrl = null },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = expandedImageUrl,
                        contentDescription = "Full Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .fillMaxHeight(0.8f)
                    )

                    IconButton(
                        onClick = { expandedImageUrl = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }
}

@Composable
fun AttachmentOption(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
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
        uploadResult["secure_url"] as String
    }
}

//fun fetchCurrentLocation(
//    context: Context,
//    onSuccess: (Location) -> Unit,
//    onFailure: () -> Unit
//) {
//    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//    if (ActivityCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        onFailure()
//        return
//    }
//
//    fusedLocationClient.lastLocation
//        .addOnSuccessListener { location: Location? ->
//            if (location != null) {
//                onSuccess(location)
//            } else {
//                onFailure()
//            }
//        }
//}
