package com.example.nhatro24_7.ui.screen.customer.search

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.ui.screen.customer.component.BottomNavBar
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: RoomViewModel
) {
    val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
    val selectedAreaRange = remember { mutableStateOf<String?>(null) }
    val selectedMinPrice = remember { mutableStateOf(0f) }
    val selectedMaxPrice = remember { mutableStateOf(5_000_000f) }

    val rooms = viewModel.rooms
    val filteredRooms = remember(
        searchQuery.value.text,
        selectedAreaRange.value,
        selectedMinPrice.value,
        selectedMaxPrice.value,
        rooms
    ) {
        rooms.filter { room ->
            val matchesLocation = room.location.contains(searchQuery.value.text, ignoreCase = true)
            val matchesArea = when (selectedAreaRange.value) {
                "<20" -> room.area < 20
                "20-30" -> room.area in 20.0..30.0
                ">30" -> room.area > 30
                else -> true
            }
            val matchesPrice = room.price in selectedMinPrice.value..selectedMaxPrice.value

            matchesLocation && matchesArea && matchesPrice
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                placeholder = {
                    Text(
                        text = "Tìm kiếm theo địa chỉ...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary, // Chỉnh màu biểu tượng khi có focus
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant // Chỉnh màu biểu tượng khi không có focus
                ),
                singleLine = true
            )


            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    AreaFilterSection(selectedAreaRange)
                    PriceFilterSection(selectedMinPrice, selectedMaxPrice)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredRooms.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRooms) { room ->
                        RoomCardItem(room = room) {
                            navController.navigate("roomDetail/${room.id}")
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không tìm thấy phòng phù hợp.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AreaFilterSection(selectedOption: MutableState<String?>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Diện tích",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val areaOptions = listOf(
                "<20" to "Dưới 20m",
                "20-30" to "20m - 30m",
                ">30" to "Trên 30m"
            )

            areaOptions.forEach { (value, label) ->
                FilterChip(
                    selected = selectedOption.value == value,
                    onClick = {
                        selectedOption.value = if (selectedOption.value == value) null else value
                    },
                    label = { Text(label) },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun PriceFilterSection(
    selectedMinPrice: MutableState<Float>,
    selectedMaxPrice: MutableState<Float>
) {
    val priceRange = 0f..5_000_000f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Giá thành (VND)",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Từ ${"%,d".format(selectedMinPrice.value.toInt())}đ",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Đến ${"%,d".format(selectedMaxPrice.value.toInt())}đ",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            RangeSlider(
                value = selectedMinPrice.value..selectedMaxPrice.value,
                onValueChange = { range ->
                    selectedMinPrice.value = range.start
                    selectedMaxPrice.value = range.endInclusive
                },
                valueRange = priceRange,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline,
                    activeTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    inactiveTickColor = MaterialTheme.colorScheme.outline
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "0đ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "5.000.000đ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun RoomCardItem(room: Room, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(130.dp)) {
                AsyncImage(
                    model = room.mainImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomEnd = 8.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    val priceInMillions = room.price / 1_000_000f
                    Text(
                        text = "${"%.1f".format(priceInMillions)} triệu",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = room.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                InfoRow(icon = Icons.Default.Place, text = room.location)
                InfoRow(icon = Icons.Default.Straighten, text = "${room.area} m²")
                InfoRow(icon = Icons.Default.MeetingRoom, text = room.roomType)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Divider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = color, style = MaterialTheme.typography.bodySmall)
    }
}
