package com.example.nhatro24_7.ui.screen.customer.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nhatro24_7.data.model.Review
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.model.User
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LandlordDetailScreen(
    landlordId: String,
    roomViewModel: RoomViewModel,
    viewModel: AuthViewModel
) {
    val roomsByLandlord = remember { mutableStateListOf<Room>() }
    val allReviews = remember { mutableStateListOf<Review>() }
    val landlordInfo = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(landlordId) {
        // Fetch landlord info
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(landlordId)
            .get()
            .addOnSuccessListener { doc ->
                landlordInfo.value = doc.toObject(User::class.java)
            }

        // Fetch rooms
        roomViewModel.fetchRoomsByOwner(landlordId) { rooms ->
            roomsByLandlord.clear()
            roomsByLandlord.addAll(rooms)

            // Fetch reviews for all rooms
            rooms.forEach { room ->
                roomViewModel.getReviewsByRoomId(room.id) { reviews ->
                    allReviews.addAll(reviews)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        landlordInfo.value?.let { user ->
            Text("Chủ trọ: ${user.fullName}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Email: ${user.email}", fontSize = 16.sp)
            Text("SĐT: ${user.phone}", fontSize = 16.sp)
            Text("Địa chỉ: ${user.currentAddress}", fontSize = 16.sp)
        } ?: Text("Đang tải thông tin chủ trọ...")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Đánh giá từ người thuê", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        if (allReviews.isEmpty()) {
            Text("Chưa có đánh giá nào.")
        } else {
            allReviews.forEach { review ->
                ReviewCard(review)
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("⭐ ${review.rating.toInt()} sao", fontWeight = FontWeight.SemiBold)
            Text(review.comment)
            Text(
                "Ngày: ${SimpleDateFormat("dd/MM/yyyy").format(Date(review.submittedAt))}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
