package com.example.nhatro24_7.ui.screen.customer.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhatro24_7.data.model.Review
import com.example.nhatro24_7.viewmodel.ReviewViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun ReviewScreen(
    roomId: String,
    bookingId: String,
    navController: NavController,
    reviewViewModel: ReviewViewModel
) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val canReview = remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(userId, bookingId) {
        if (userId.isNotEmpty() && bookingId.isNotEmpty()) {
            reviewViewModel.hasUserReviewedBooking(userId, bookingId) { alreadyReviewed ->
                canReview.value = !alreadyReviewed
            }
        } else canReview.value = false
    }

    when (canReview.value) {
        null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        false -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Bạn đã đánh giá booking này rồi.",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Quay lại")
                }
            }
        }
        true -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Đánh giá phòng",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Chọn số sao",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 1..5) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "Star $i",
                                    tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable { rating = i }
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Nhận xét",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { Text("Viết nhận xét của bạn...") },
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (rating > 0) {
                                    val review = Review(
                                        id = UUID.randomUUID().toString(),
                                        roomId = roomId,
                                        userId = userId,
                                        bookingId = bookingId,
                                        rating = rating.toFloat(),
                                        comment = comment
                                    )
                                    reviewViewModel.submitReview(review) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show()
                                            canReview.value = false
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, "Gửi đánh giá thất bại!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = rating > 0
                        ) {
                            Text("Gửi đánh giá", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

