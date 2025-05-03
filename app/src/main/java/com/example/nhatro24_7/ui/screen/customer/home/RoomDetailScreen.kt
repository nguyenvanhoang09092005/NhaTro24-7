package com.example.nhatro24_7.ui.screen.customer.home

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nhatro24_7.data.model.Review
import com.example.nhatro24_7.navigation.Routes
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(roomId: String?, navController: NavController, roomViewModel: RoomViewModel) {
    val room = roomViewModel.rooms.find { it.id == roomId }

    // Đánh giá từ người dùng
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current
    val reviews = remember { mutableStateListOf<Review>() }

    LaunchedEffect(roomId) {
        roomViewModel.getReviewsByRoomId(roomId ?: "") {
            reviews.clear()
            reviews.addAll(it)
        }
    }

    val canReview = remember { mutableStateOf(false) }

    LaunchedEffect(roomId, userId) {
        if (userId.isNotEmpty() && roomId != null) {
            roomViewModel.hasUserBookedRoom(userId, roomId) {
                canReview.value = it
            }
        }
    }

    val hasRequested = remember { mutableStateOf(false) }

    LaunchedEffect(room?.id, userId) {
        if (!userId.isNullOrBlank() && room != null) {
            roomViewModel.hasPendingBookingRequest(userId, room.id) { alreadyRequested ->
                hasRequested.value = alreadyRequested
            }
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chi tiết phòng", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                actions = {
                    if (!hasRequested.value) {
                        TextButton(
                            onClick = {
                                room?.let {
                                    roomViewModel.sendBookingRequest(
                                        roomId = it.id,
                                        userId = userId,
                                        landlordId = it.owner_id.toString()
                                    ) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Yêu cầu đặt phòng đã gửi!", Toast.LENGTH_SHORT).show()
                                            hasRequested.value = true
                                            navController.navigate(Routes.BOOKING_PENDING)
                                        } else {
                                            Toast.makeText(context, "Gửi yêu cầu thất bại!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "Đặt phòng",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        TextButton(onClick = {
                            Toast.makeText(context, "Bạn đã gửi yêu cầu đặt phòng cho phòng này rồi.", Toast.LENGTH_SHORT).show()
                        }) {
                            Text(
                                text = "Đã yêu cầu",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }


                }
            )
        }
,
                bottomBar = {
            Button(
                onClick = { /* Hành động đặt phòng hoặc liên hệ chủ phòng */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Liên hệ đặt phòng", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        room?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                // Hình ảnh chính
                AsyncImage(
                    model = room.mainImage,
                    contentDescription = "Room Main Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Thư viện ảnh phòng
                Text(
                    "Hình ảnh khác",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(room.images.size) { index ->
                        AsyncImage(
                            model = room.images[index],
                            contentDescription = "Room Image $index",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Tiêu đề phòng
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Giá và diện tích
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip(Icons.Default.AttachMoney, "${room.price.toInt()} VNĐ/tháng")
                    InfoChip(Icons.Default.SquareFoot, "${room.area} m²")
                }


                Spacer(modifier = Modifier.height(8.dp))

                // Loại phòng và danh mục
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    InfoChip(Icons.Default.Category, room.roomType)
                    InfoChip(Icons.Default.Home, room.roomCategory)
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Địa chỉ
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp,),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tiện ích phòng
                Text(
                    "Tiện ích",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                FlowRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    room.amenities.forEach { amenity ->
                        AmenityChip(amenity = amenity)
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Mô tả chi tiết
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Mô tả",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = room.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        } ?: run {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    "Không tìm thấy thông tin phòng!",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp
                )
            }
        }


        if (canReview.value) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Đánh giá phòng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Số sao:")
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 0f..5f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Đánh giá: ${rating.toInt()} sao")


                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Viết bình luận") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val review = Review(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            roomId = room?.id ?: "",
                            rating = rating,
                            comment = comment
                        )
                        roomViewModel.submitReview(review) { success ->
                            if (success) {
                                Toast.makeText(context, "Đã gửi đánh giá!", Toast.LENGTH_SHORT).show()
                                rating = 0f
                                comment = ""
                            }
                        }
                    },
                    enabled = userId.isNotEmpty() && rating > 0
                ) {
                    Text("Gửi đánh giá")
                }
            }
        } else if (userId.isNotEmpty()) {
            Text(
                "Bạn cần đặt phòng trước khi có thể đánh giá.",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        }


        Column(modifier = Modifier.padding(16.dp)) {
            Text("Đánh giá của người dùng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            if (reviews.isEmpty()) {
                Text("Chưa có đánh giá nào.")
            } else {
                reviews.forEach { review ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("⭐ ${review.rating.toInt()} sao", fontWeight = FontWeight.SemiBold)
                            Text(review.comment)
                            Text("Ngày: ${SimpleDateFormat("dd/MM/yyyy").format(Date(review.submittedAt))}",
                                fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }


    }
}

@Composable
fun InfoChip(icon: ImageVector, info: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(info, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun AmenityChip(amenity: String) {
    val icon = when (amenity) {
        "Wifi" -> Icons.Default.Wifi
        "WC riêng" -> Icons.Default.Bathtub
        "Giữ xe" -> Icons.Default.DirectionsCar
        "Điện" -> Icons.Default.FlashOn
        "Tự do" -> Icons.Default.Face
        "Điều hoà" -> Icons.Default.AcUnit
        "Tủ lạnh" -> Icons.Default.Kitchen
        "Máy giặt" -> Icons.Default.LocalLaundryService
        else -> Icons.Default.CheckCircle
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = amenity,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = amenity,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
