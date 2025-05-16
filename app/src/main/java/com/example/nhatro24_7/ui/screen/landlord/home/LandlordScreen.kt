package com.example.nhatro24_7.ui.screen.landlord.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Visibility
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.model.Statistic
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.example.nhatro24_7.viewmodel.StatisticViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale

@Composable
fun LandlordScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    roomViewModel: RoomViewModel = viewModel(),
    statisticViewModel: StatisticViewModel = viewModel()
) {
    val landlordId = FirebaseAuth.getInstance().currentUser?.uid ?: return


    val rooms by roomViewModel.roomsByLandlord.collectAsState()
    val statistic by statisticViewModel.statistic.collectAsState()
    val isLoading by statisticViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        roomViewModel.getRoomsByLandlord(landlordId)
        statisticViewModel.fetchStatistic(landlordId)
    }

    Scaffold(
        bottomBar = {
            com.example.nhatro24_7.ui.screen.landlord.component.BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Text("Chào mừng chủ trọ!", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Dưới đây là tổng quan thống kê và danh sách phòng của bạn.", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    StatisticSection(statistic = statistic)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Danh sách phòng", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val topViewedRooms = rooms.sortedByDescending { it.viewCount }.take(3)

            item {
                Text("🔥 Phòng được xem nhiều nhất", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (topViewedRooms.isNotEmpty()) {


                        items(topViewedRooms) { room ->
                            RoomCardItem(
                                room = room,
                                onClick = {
                                    val userId = viewModel.getCurrentUserId()
                                    if (userId != null) {
                                        roomViewModel.logViewRoom(userId, room.id)
                                        roomViewModel.incrementRoomViewCount(room.id)
                                    }
                                    navController.navigate("room_detail_landlord/${room.id}")
                                },
                                onToggleSave = {
                                    val userId = viewModel.getCurrentUserId()

                                }
                            )
                        }

                    }
                }

            }


//            if (topViewedRooms.isNotEmpty()) {
//                item {
//                    Text("🔥 Phòng được xem nhiều nhất", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//
//                items(topViewedRooms) { room ->
//                    RoomCardItem(
//                        room = room,
//                        onClick = {
//                            val userId = viewModel.getCurrentUserId()
//                            if (userId != null) {
//                                roomViewModel.logViewRoom(userId, room.id)
//                                roomViewModel.incrementRoomViewCount(room.id)
//                            }
//                            navController.navigate("roomDetail/${room.id}")
//                        },
//                        onToggleSave = {
//                            val userId = viewModel.getCurrentUserId()
//
//                        }
//                    )
//                }
//
//
//                item {
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text("📋 Tất cả phòng", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }

            item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("📋 Tất cả phòng", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

            val otherRooms = rooms.filterNot { topViewedRooms.contains(it) }
            items(otherRooms) { room ->
                RoomItem(room = room, navController = navController)
            }


        }
    }
}

@Composable
fun StatisticSection(statistic: Statistic) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Thống kê tổng quan",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticCard(title = "Đặt phòng", value = "${statistic.totalBookings}")
            StatisticCard(title = "Huỷ", value = "${statistic.totalCancellations}")
            StatisticCard(title = "Trả phòng", value = "${statistic.totalCheckouts}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticCard(title = "Doanh thu", value = "${statistic.revenue}đ")
            StatisticCard(title = "Lượt xem", value = "${statistic.totalViews}")
            StatisticCard(title = "Đánh giá", value = "${"%.1f".format(statistic.averageRating)}★")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Doanh thu theo tháng",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BarChart(data = statistic.revenueByMonth.mapValues { it.value.toFloat() })

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Lượt xem theo ngày",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LineChart(data = statistic.viewsByDay.mapValues { it.value.toFloat() })
    }
}


@Composable
fun RoomItem(room: Room, navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .clickable {
                navController.navigate("room_detail_landlord/${room.id}")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    navController.navigate("room_detail_landlord/${room.id}")
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
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(bottomEnd = 8.dp)
                        )
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
                    text = room.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.location,
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
                        contentDescription = null,
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
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.roomCategory,
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

@Composable
fun RoomCardItem( room: Room,
              onClick: () -> Unit,
              onToggleSave: () -> Unit)
{
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateText = dateFormat.format(Date(room.created_at))

    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(170.dp)
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box {
                AsyncImage(
                    model = room.mainImage,
                    contentDescription = "Room Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )



                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            ),
                            shape = RoundedCornerShape(topStart  = 16.dp, topEnd  = 16.dp)
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${room.price.toInt()} VNĐ",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Lượt xem",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${room.viewCount}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {



                    Text(
                        text = room.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis // Thêm overflow vào đây
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = room.location,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis // Thêm overflow vào đây
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SquareFoot,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${room.area} m²",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis // Thêm overflow vào đây
                        )
                    }
                }

                Text(
                    text = "Ngày đăng: $dateText",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // Thêm overflow vào đây
                )
            }
        }
    }
}

@Composable
fun StatisticCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(90.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun BarChart(data: Map<String, Float>) {
    val maxVal = data.values.maxOrNull() ?: 0f
    val barWidth = 24.dp
    val barSpacing = 8.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .height((value / maxVal * 100).dp.coerceAtLeast(10.dp))
                        .width(barWidth)
                        .background(Color(0xFF64B5F6))
                )
                Text(label, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.width(barSpacing))
        }
    }
}

@Composable
fun LineChart(data: Map<String, Float>) {
    val sortedData = data.toSortedMap()
    val points = sortedData.values.toList()

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
    ) {
        val space = size.width / (points.size + 1)
        val maxVal = points.maxOrNull() ?: 1f

        for (i in 1 until points.size) {
            val x1 = space * i
            val y1 = size.height - (points[i - 1] / maxVal * size.height)
            val x2 = space * (i + 1)
            val y2 = size.height - (points[i] / maxVal * size.height)

            drawLine(
                color = Color(0xFF4DB6AC),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 4f
            )
        }
    }
}
