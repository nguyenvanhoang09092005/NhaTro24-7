package com.example.nhatro24_7.ui.screen.landlord.room

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.viewmodel.RoomViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreen(navController: NavController, roomViewModel: RoomViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var currentStep by remember { mutableStateOf(0) }

    var roomType by remember { mutableStateOf("Cho thuê") }
    var roomCategory by remember { mutableStateOf("Phòng") }
    var address by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    val selectedAmenities = remember { mutableStateListOf<String>() }
    val imageList = remember { mutableStateListOf<String>() }

    val scrollState = rememberScrollState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Đăng tin") })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StepIndicator(currentStep = currentStep)

            when (currentStep) {
                0 -> InfoStep(roomType, { roomType = it }, roomCategory, { roomCategory = it },
                    address, { address = it }, price, { price = it }, area, { area = it }, selectedAmenities)
                1 -> ImageUploadStep(imageList)
                2 -> ConfirmStep(roomType, roomCategory, address, price, area, selectedAmenities, imageList)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (currentStep < 2) {
                        currentStep++
                    } else {
                        val room = Room(
                            title = "Tin đăng mới",
                            description = "Tin đăng tự động",
                            price = price.toDoubleOrNull() ?: 0.0,
                            area = area.toDoubleOrNull() ?: 0.0,
                            location = address,
                            roomType = roomType,
                            roomCategory = roomCategory,
                            amenities = selectedAmenities.toList(),
                            owner_id = 1,
                            created_at = System.currentTimeMillis()
                        )

                        scope.launch {
                            val success = roomViewModel.addRoom(room)
                            if (success) {
                                Toast.makeText(context, "Đăng tin thành công", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Thất bại. Vui lòng thử lại", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = address.isNotBlank() && price.isNotBlank() && area.isNotBlank()
            ) {
                Text(if (currentStep < 2) "Tiếp theo" else "Xác nhận & Đăng")
            }
        }
    }
}

private fun RoomViewModel.addRoomToFirebase(room: Room) {}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InfoStep(
    roomType: String, onRoomTypeChange: (String) -> Unit,
    roomCategory: String, onRoomCategoryChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    price: String, onPriceChange: (String) -> Unit,
    area: String, onAreaChange: (String) -> Unit,
    selectedAmenities: SnapshotStateList<String>
) {
    val amenities = listOf(
        "Wifi", "WC riêng", "Giữ xe", "Điện",
        "Tự do", "Điều hoà", "Tủ lạnh", "Máy giặt"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Loại tin", fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Cho thuê", "Tìm người ở ghép").forEach {
                FilterChip(
                    selected = roomType == it,
                    onClick = { onRoomTypeChange(it) },
                    label = { Text(it) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Text("Loại phòng", fontSize = 14.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Phòng", "Căn hộ", "Căn hộ Mini", "Nguyên căn").forEach {
                FilterChip(
                    selected = roomCategory == it,
                    onClick = { onRoomCategoryChange(it) },
                    label = { Text(it) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Địa chỉ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { /* Xử lý chọn địa điểm trên bản đồ */ }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Chọn vị trí", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Chọn vị trí",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Nhập địa chỉ") }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = { Text("Giá phòng (VND)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = area,
                onValueChange = onAreaChange,
                label = { Text("Diện tích (m²)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Text("Tiện ích phòng", fontSize = 14.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            amenities.forEach { item ->
                val icon = when (item) {
                    "Wifi" -> Icons.Default.Wifi
                    "WC riêng" -> Icons.Default.Bathtub
                    "Giữ xe" -> Icons.Default.DirectionsCar
                    "Điện" -> Icons.Default.FlashOn
                    "Tự do" -> Icons.Default.Face
                    "Điều hoà" -> Icons.Default.AcUnit
                    "Tủ lạnh" -> Icons.Default.Kitchen
                    "Máy giặt" -> Icons.Default.LocalLaundryService
                    else -> Icons.Default.Check
                }

                FilterChip(
                    selected = selectedAmenities.contains(item),
                    onClick = {
                        if (selectedAmenities.contains(item)) selectedAmenities.remove(item)
                        else selectedAmenities.add(item)
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = item, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(item)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}


@Composable
fun ImageUploadStep(imageList: SnapshotStateList<String>) {
    Text("Thêm hình ảnh", fontSize = 16.sp)
    Button(onClick = {
        imageList.add("https://picsum.photos/200/300") // ảnh mẫu
    }) {
        Text("Thêm ảnh mẫu")
    }
    imageList.forEachIndexed { index, url ->
        Text("Ảnh ${index + 1}: $url")
    }
}

@Composable
fun ConfirmStep(
    roomType: String, roomCategory: String, address: String,
    price: String, area: String, selectedAmenities: List<String>, imageList: List<String>
) {
    Text("Xác nhận lại thông tin", fontSize = 16.sp)
    Text("Loại tin: $roomType")
    Text("Loại phòng: $roomCategory")
    Text("Địa chỉ: $address")
    Text("Giá: $price VND")
    Text("Diện tích: $area m²")
    Text("Tiện ích: ${selectedAmenities.joinToString()}")
    Text("Số ảnh: ${imageList.size}")
}

@Composable
fun StepIndicator(currentStep: Int) {
    val steps = listOf("Thông tin", "Hình ảnh", "Xác nhận")
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        steps.forEachIndexed { index, label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.dp, if (index <= currentStep) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
                        .background(if (index == currentStep) MaterialTheme.colorScheme.primary.copy(0.2f) else Color.Transparent, CircleShape)
                ) {
                    Text("${index + 1}", fontSize = 12.sp)
                }
                Text(label, fontSize = 12.sp)
            }
            if (index < steps.lastIndex) {
                Spacer(Modifier.width(8.dp))
                Divider(Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
