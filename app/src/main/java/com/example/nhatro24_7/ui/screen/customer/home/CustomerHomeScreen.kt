package com.example.nhatro24_7.ui.screen.customer.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.ui.screen.customer.component.BottomNavBar
import com.example.nhatro24_7.ui.screen.customer.home.components.RoomItem
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    roomViewModel: RoomViewModel = viewModel()
) {
    val rooms by remember { mutableStateOf(roomViewModel.rooms) }

    var selectedRoomType by remember { mutableStateOf("Tất cả") }
    var selectedRoomCategory by remember { mutableStateOf("Tất cả") }

    val filteredRooms = rooms.filter {
        (selectedRoomType == "Tất cả" || it.roomType == selectedRoomType) &&
                (selectedRoomCategory == "Tất cả" || it.roomCategory == selectedRoomCategory)
    }

    val isFilterApplied = selectedRoomType != "Tất cả" || selectedRoomCategory != "Tất cả"


    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        topBar = {
            EnhancedTopAppBar("Nhà Trọ 24/7")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Bộ lọc
            Column(modifier = Modifier.padding(10.dp)) {
                EnhancedFilterSection(
                    title = "Loại tin",
                    options = listOf("Tất cả", "Cho thuê", "Tìm người ở ghép"),
                    selectedOption = selectedRoomType,
                    onOptionSelected = { selectedRoomType = it }
                )
                EnhancedFilterSection(
                    title = "Loại phòng",
                    options = listOf("Tất cả", "Phòng", "Căn hộ", "Căn hộ Mini", "Nguyên căn"),
                    selectedOption = selectedRoomCategory,
                    onOptionSelected = { selectedRoomCategory = it }
                )
            }


            Spacer(modifier = Modifier.height(10.dp))

            if (!isFilterApplied) {
                // Chỉ hiển thị nếu không dùng bộ lọc
                Text(
                    "Phòng mới nhất",
                    modifier = Modifier.padding(start = 10.dp, bottom = 5.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(modifier = Modifier.padding(start = 5.dp)) {
                    items(rooms.sortedByDescending { it.created_at }.take(10)) { room ->
                        RoomItem(
                            room = room,
                            onClick = { navController.navigate("roomDetail/${room.id}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Phòng được yêu thích",
                    modifier = Modifier.padding(start = 10.dp, bottom = 5.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(modifier = Modifier.padding(start = 5.dp)) {
                    items(rooms.shuffled().take(10)) { room ->
                        RoomItem(
                            room = room,
                            onClick = { navController.navigate("roomDetail/${room.id}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Luôn hiển thị danh sách tất cả phòng
            Text(
                "Tất cả các phòng",
                modifier = Modifier.padding(start = 10.dp, bottom = 5.dp),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .heightIn(min = 400.dp, max = 2000.dp), // điều chỉnh để hiển thị tốt
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredRooms) { room ->
                    RoomItem(
                        room = room,
                        onClick = { navController.navigate("roomDetail/${room.id}") }
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) },
                    label = { Text(option, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}

@Composable
fun EnhancedTopAppBar(title: String) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)

            .height(60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home Icon",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EnhancedFilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val iconsMap = mapOf(
        "Tất cả" to Icons.Default.List,
        "Cho thuê" to Icons.Default.Home,
        "Tìm người ở ghép" to Icons.Default.People,
        "Phòng" to Icons.Default.MeetingRoom,
        "Căn hộ" to Icons.Default.Apartment,
        "Căn hộ Mini" to Icons.Default.Apartment,
        "Nguyên căn" to Icons.Default.Home
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options.size) { index ->
                val option = options[index]
                val isSelected = option == selectedOption
                val backgroundColor by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    label = "backgroundColor"
                )
                val contentColor by animateColorAsState(
                    if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "contentColor"
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = if (isSelected) 8.dp else 2.dp,
                    modifier = Modifier
                        .height(40.dp)
                        .toggleable(
                            value = isSelected,
                            onValueChange = { onOptionSelected(option) }
                        ),
                    color = backgroundColor
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = iconsMap[option] ?: Icons.Default.List,
                            contentDescription = option,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = option,
                            color = contentColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
