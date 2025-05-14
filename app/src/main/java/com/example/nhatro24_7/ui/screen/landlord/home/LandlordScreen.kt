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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
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
import com.example.nhatro24_7.data.model.SavedRoom
import com.example.nhatro24_7.ui.screen.customer.home.components.RoomItem
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.launch
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Thống kê tổng quan", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        // Dữ liệu biểu đồ cột
        val barData = listOf(
            "Đặt" to statistic.totalBookings,
            "Hủy" to statistic.totalCancellations,
            "Trả" to statistic.totalCheckouts,
            "Đã TT" to statistic.paidRoomCount,
        )

        BarChartView(data = barData)

        Spacer(modifier = Modifier.height(24.dp))

        Text("📊 Doanh thu: ${statistic.revenue} VNĐ", fontSize = 16.sp)
        Text("👁 Tổng lượt xem: ${statistic.totalViews}")
        Text("⭐ Đánh giá trung bình: ${"%.1f".format(statistic.averageRating)}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("🔝 Top 5 phòng được xem nhiều nhất:", fontWeight = FontWeight.SemiBold)
        statistic.topViewedRooms.forEachIndexed { index, (roomName, count) ->
            Text("${index + 1}. $roomName - $count lượt")
        }
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
fun BarChartView(data: List<Pair<String, Int>>) {
    val entries = data.mapIndexed { index, (_, value) ->
        index to value.toFloat()
    }

    val chartModel = entryModelOf(*entries.toTypedArray())

    Chart(
        chart = columnChart(), // dùng mặc định
        model = chartModel,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}