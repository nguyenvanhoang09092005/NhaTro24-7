package com.example.nhatro24_7.ui.screen.landlord.statistical

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

@Composable
fun RevenueBarChart(revenueByMonth: Map<String, Long>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Doanh thu theo tháng",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (revenueByMonth.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có dữ liệu doanh thu",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            val maxRevenue = (revenueByMonth.values.maxOrNull() ?: 1L).toFloat()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                revenueByMonth.forEach { (month, value) ->
                    Column {
                        Text(
                            text = month,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .background(
                                    Color.LightGray.copy(alpha = 0.3f),
                                    shape = MaterialTheme.shapes.small
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = (value / maxRevenue).coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .background(
                                        Color(0xFF4CAF50),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "${value}đ",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


