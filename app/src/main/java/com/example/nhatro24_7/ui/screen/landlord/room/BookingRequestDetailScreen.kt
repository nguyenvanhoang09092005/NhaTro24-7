package com.example.nhatro24_7.ui.screen.landlord.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import coil.request.ImageRequest
import com.example.nhatro24_7.data.model.BookingRequest
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.model.User
import com.example.nhatro24_7.ui.screen.customer.home.AmenityChip
import com.example.nhatro24_7.ui.screen.customer.home.InfoChip
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingRequestDetailScreen(
    bookingRequestId: String,
    navController: NavController,
    roomViewModel: RoomViewModel
) {
    var room by remember { mutableStateOf<Room?>(null) }
    var user by remember { mutableStateOf<User?>(null) }

    val context = LocalContext.current

    LaunchedEffect(bookingRequestId) {
        FirebaseFirestore.getInstance().collection("booking_requests").document(bookingRequestId).get()
            .addOnSuccessListener { bookingDoc ->
                val booking = bookingDoc.toObject(BookingRequest::class.java)
                if (booking != null) {
                    FirebaseFirestore.getInstance().collection("rooms").document(booking.roomId).get()
                        .addOnSuccessListener { doc ->
                            room = doc.toObject(Room::class.java)?.copy(id = doc.id)
                        }

                    FirebaseFirestore.getInstance().collection("users").document(booking.userId).get()
                        .addOnSuccessListener { doc ->
                            user = doc.toObject(User::class.java)?.copy(id = doc.id)
                        }
                }
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chi tiết yêu cầu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            room?.let {
                // Main room image
                AsyncImage(
                    model = it.mainImage,
                    contentDescription = "Ảnh phòng",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                )

                Spacer(Modifier.height(12.dp))
// Thư viện ảnh khác
                if (room?.images?.isNotEmpty() == true) {
                    Text(
                        text = "Hình ảnh khác",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(room!!.images.size) { index ->
                            val imageUrl = room!!.images[index]
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Ảnh phụ",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    text = it.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(8.dp))

                // Basic info
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoChip(Icons.Default.AttachMoney, "${it.price.toInt()} VNĐ/tháng")
                    InfoChip(Icons.Default.SquareFoot, "${it.area} m²")
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoChip(Icons.Default.Category, it.roomType)
                    InfoChip(Icons.Default.Home, it.roomCategory)
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(it.location)
                }

                Spacer(Modifier.height(16.dp))

                // Amenities
                Text(
                    text = "Tiện ích",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    it.amenities.forEach { a -> AmenityChip(a) }
                }

                // Description
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text("Mô tả", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(6.dp))
                    Text(it.description, textAlign = TextAlign.Justify)
                }

                Spacer(Modifier.height(16.dp))
            } ?: LoadingSection("Đang tải thông tin phòng...")

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // User info
            user?.let {
                Text(
                    text = "Thông tin người đặt",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    AsyncImage(
                        model = it.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(32.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(it.fullName.ifBlank { "Không rõ" }, fontWeight = FontWeight.Bold)
                        Text("(${it.role})", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                InfoBlock(Icons.Default.Email, "Email", it.email)
                InfoBlock(Icons.Default.Phone, "SĐT", it.phone.ifBlank { "Chưa cập nhật" })
                InfoBlock(Icons.Default.Cake, "Ngày sinh", it.birthDate.ifBlank { "Chưa cập nhật" })
                InfoBlock(Icons.Default.LocationCity, "Địa chỉ hiện tại", it.currentAddress.ifBlank { "Chưa cập nhật" })
                InfoBlock(Icons.Default.Flag, "Quê quán", it.hometown.ifBlank { "Chưa cập nhật" })
            } ?: LoadingSection("Đang tải thông tin người đặt...")
        }
    }
}

@Composable
fun InfoBlock(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 13.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun LoadingSection(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, color = Color.Gray)
        }
    }
}
