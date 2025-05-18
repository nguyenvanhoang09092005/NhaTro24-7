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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.pow
import kotlin.math.*

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: RoomViewModel
) {
    val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
    val selectedAreaRange = remember { mutableStateOf<String?>(null) }
    val selectedMinPrice = remember { mutableStateOf(0f) }
    val selectedMaxPrice = remember { mutableStateOf(10_000_000f) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle


    val selectedCoordinates = savedStateHandle?.getLiveData<List<Double>>("selected_coordinates")?.observeAsState()
    val preFilteredRooms = savedStateHandle?.getLiveData<List<Room>>("filtered_rooms")?.observeAsState()
    val rooms = preFilteredRooms?.value ?: viewModel.rooms

    val selectedLatLng = remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(selectedCoordinates?.value) {
        selectedCoordinates?.value?.let { coords ->
            if (coords.size == 2) {
                selectedLatLng.value = coords[0] to coords[1]
            }
            savedStateHandle.remove<List<Double>>("selected_coordinates")
        }
    }
//
//    val rooms = preFilteredRooms.value ?: viewModel.rooms

//    val rooms = viewModel.rooms
    val filteredRooms = remember(
        searchQuery.value.text,
        selectedAreaRange.value,
        selectedMinPrice.value,
        selectedMaxPrice.value,
        selectedLatLng.value,
        rooms
    ) {
        rooms.filter { room ->
            val matchesLocation = if (selectedLatLng.value != null) {
                true
            } else {
                room.location.contains(searchQuery.value.text, ignoreCase = true)
            }

            val matchesArea = when (selectedAreaRange.value) {
                "<20" -> room.area < 20
                "20-30" -> room.area in 20.0..30.0
                ">30" -> room.area > 30
                else -> true
            }

            val matchesPrice = room.price in selectedMinPrice.value..selectedMaxPrice.value

            val withinRadius = selectedLatLng.value?.let { (lat, lon) ->
                haversine(lat, lon, room.latitude, room.longitude) <= 5.0
            } ?: true

            matchesLocation && matchesArea && matchesPrice && withinRadius
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Tìm kiếm",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        navController.navigate("selectSearchLocation")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Tìm kiếm bằng bản đồ",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )




            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AreaFilterSection(selectedAreaRange)
                    PriceFilterSection(selectedMinPrice, selectedMaxPrice)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredRooms.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredRooms) { room ->
                        RoomCardItem(room = room) {
                            navController.navigate("roomDetail/${room.id}")
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp), // đẩy thông báo xuống một chút
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (selectedLatLng.value != null)
                                "Không có phòng nào trong bán kính 5km quanh đây."
                            else
                                "Không tìm thấy phòng phù hợp.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )

                    }
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val areaOptions = listOf(
                "<20" to "Dưới 20m²",
                "20-30" to "20m² - 30m²",
                ">30" to "Trên 30m²"
            )

            areaOptions.forEach { (value, label) ->
                FilterChip(
                    selected = selectedOption.value == value,
                    onClick = {
                        selectedOption.value = if (selectedOption.value == value) null else value
                    },
                    label = { Text(label, fontSize = 13.sp) },
                    shape = RoundedCornerShape(10.dp),
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
    val priceRange = 0f..10_000_000f

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
                modifier = Modifier.fillMaxWidth(),
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
                Text("0đ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("10.000.000đ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
@Composable
fun RoomCardItem(room: Room, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.width(130.dp)) {
                    AsyncImage(
                        model = room.mainImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(110.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(bottomEnd = 8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        val priceInMillions = room.price / 1_000_000f
                        Text(
                            text = "${"%.1f".format(priceInMillions)} triệu",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
        }
    }
}
@Composable
fun InfoRow(
    icon: ImageVector,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 1.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text,
            color = color,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371 // bán kính Trái Đất (km)
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2).pow(2.0) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2).pow(2.0)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return R * c
}


fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Đơn vị: km

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c // Trả về khoảng cách (km)
}