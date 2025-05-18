package com.example.nhatro24_7.ui.screen.customer.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.nhatro24_7.data.model.Review
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.model.User
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandlordDetailScreen(
    landlordId: String,
    navController: NavController,
    roomViewModel: RoomViewModel
) {
    val roomsByLandlord = remember { mutableStateListOf<Room>() }
    val landlordReviews = remember { mutableStateListOf<Review>() }
    val landlordInfo = remember { mutableStateOf<User?>(null) }
    var infoExpanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(landlordId) {
        // Lấy thông tin chủ trọ
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(landlordId)
            .get()
            .addOnSuccessListener { doc ->
                landlordInfo.value = doc.toObject(User::class.java)
            }

        // Lấy danh sách phòng (kể cả phòng bị ẩn)
        roomViewModel.fetchRoomsByOwner(landlordId) { rooms ->
            roomsByLandlord.clear()
            roomsByLandlord.addAll(rooms)

            // Lấy danh sách đánh giá từ các phòng đó
            val roomIds = rooms.map { it.id }
            if (roomIds.isNotEmpty()) {
                FirebaseFirestore.getInstance()
                    .collection("reviews")
                    .whereIn("roomId", roomIds)
                    .get()
                    .addOnSuccessListener { result ->
                        landlordReviews.clear()
                        landlordReviews.addAll(result.toObjects(Review::class.java))
                    }
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trang cá nhân", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        landlordInfo.value?.let { user ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(user.avatarUrl),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user.fullName ?: "Chưa có tên",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { infoExpanded = !infoExpanded }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Thông tin cá nhân",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = if (infoExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (infoExpanded) {
                            Column(modifier = Modifier.padding(start = 32.dp, end = 16.dp, bottom = 16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("SĐT: ${user.phone.ifEmpty { "Chưa cung cấp" }}", fontSize = 14.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Địa chỉ: ${user.currentAddress ?: "Chưa cung cấp"}", fontSize = 14.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Email: ${user.email.ifEmpty { "Chưa cung cấp" }}", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Đánh giá") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Phòng đã đăng") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    val reviewsByRoom = landlordReviews.groupBy { it.roomId }

                    if (landlordReviews.isEmpty()) {
                        Text("Chủ trọ này chưa có đánh giá nào.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        reviewsByRoom.forEach { (roomId, reviews) ->

                            val room = roomViewModel.rooms.find { it.id == roomId }

                            val averageRating = if (reviews.isNotEmpty()) {
                                reviews.map { it.rating }.average()
                            } else 0.0

                            val reviewCount = reviews.size

                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Phòng: ${room?.title ?: "Không xác định"}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Star",
                                            tint = Color(0xFFFFC107),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = String.format("%.1f", averageRating),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "($reviewCount đánh giá)",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Scrollable list of reviews for each room
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 100.dp, max = 260.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    reviews.forEachIndexed { index, review ->
                                        val reviewer = roomViewModel.users.find { it.id == review.userId }

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        AsyncImage(
                                                            model = reviewer?.avatarUrl,
                                                            contentDescription = "Avatar",
                                                            modifier = Modifier
                                                                .size(40.dp)
                                                                .clip(CircleShape)
                                                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = reviewer?.username ?: "Người dùng ẩn danh",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp
                                                        )
                                                    }

                                                    Text(
                                                        text = SimpleDateFormat("dd/MM/yyyy").format(Date(review.submittedAt)),
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(6.dp))

                                                // Dòng sao đánh giá
                                                Row {
                                                    repeat(review.rating.toInt()) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = "Star",
                                                            tint = Color(0xFFFFC107),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                // Nội dung bình luận
                                                Text(
                                                    text = review.comment,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }

                                        // Divider dưới mỗi đánh giá, trừ cái cuối
//                                        if (index < reviews.size - 1) {
//                                            Divider(
//                                                modifier = Modifier.padding(horizontal = 8.dp),
//                                                color = MaterialTheme.colorScheme.outlineVariant,
//                                                thickness = 1.dp
//                                            )
//                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }


                } else {
                    if (roomsByLandlord.isEmpty()) {
                        Text("Chủ trọ này chưa đăng phòng nào.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        roomsByLandlord.filter { it.isAvailable }.forEach { room ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            navController.navigate("roomDetail/${room.id}")
                                        }
                                ) {
                                    Box(modifier = Modifier.size(100.dp)) {
                                        AsyncImage(
                                            model = room.mainImage,
                                            contentDescription = "Ảnh phòng",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        )

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomEnd = 8.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${"%.1f".format(room.price / 1_000_000)} triệu",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically)
                                    ) {
                                        Text(
                                            text = room.title ?: "Tin đăng mới",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Place,
                                                contentDescription = "Địa chỉ",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = room.location ?: "Địa chỉ không rõ",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.SquareFoot,
                                                contentDescription = "Diện tích",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${room.area} m²",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Apartment,
                                                contentDescription = "Loại phòng",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = room.roomCategory ?: "Cho thuê",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            }
                        }

                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

