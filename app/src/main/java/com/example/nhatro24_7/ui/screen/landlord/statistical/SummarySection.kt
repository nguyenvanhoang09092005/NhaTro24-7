package com.example.nhatro24_7.ui.screen.landlord.statistical

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nhatro24_7.data.model.Statistic

@Composable
fun SummarySection(statistic: Statistic) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tổng quan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Grid layout: 2 cột
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "Tổng doanh thu",
                    value = "${statistic.revenue}đ",
                    color = Color(0xFF4CAF50), // Green
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Lượt xem",
                    value = statistic.totalViews.toString(),
                    color = Color(0xFF2196F3), // Blue
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "Lượt đặt",
                    value = statistic.totalBookings.toString(),
                    color = Color(0xFFFF9800), // Orange
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Lượt hủy",
                    value = statistic.totalCancellations.toString(),
                    color = Color(0xFFF44336), // Red
                    modifier = Modifier.weight(1f)
                )
            }

            StatCard(
                title = "Đánh giá trung bình",
                value = String.format("%.1f ★", statistic.averageRating),
                color = Color(0xFF9C27B0), // Purple
                fullWidth = true
            )
        }
    }
}


@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    fullWidth: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(6.dp)
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}


