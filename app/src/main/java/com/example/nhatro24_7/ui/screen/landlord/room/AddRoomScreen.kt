package com.example.nhatro24_7.ui.screen.landlord.room

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.navigation.Routes
import com.example.nhatro24_7.util.uploadImageToCloudinary
import com.example.nhatro24_7.viewmodel.RoomViewModel
import com.google.firebase.auth.FirebaseAuth
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
    val mainImage = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var description by remember { mutableStateOf("") }
    val shouldRefresh = remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefresh.value) {
        if (shouldRefresh.value) {
            roomViewModel.fetchRooms()
            shouldRefresh.value = false
        }
    }



    // Nhận dữ liệu địa chỉ trả về từ màn hình chọn vị trí
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    // Nhận địa chỉ
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("selected_address")
        ?.observeAsState()
        ?.value?.let { addr ->
            address = addr
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selected_address")
        }

// Nhận tọa độ
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<List<Double>>("selected_coordinates")
        ?.observeAsState()
        ?.value?.let { coords ->
            if (coords.size == 2) {
                latitude = coords[0]
                longitude = coords[1]
            }
            navController.currentBackStackEntry?.savedStateHandle?.remove<List<Double>>("selected_coordinates")
        }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Đăng tin", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = {
                        when (currentStep) {
                            0 -> navController.popBackStack()
                            else -> currentStep--
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (currentStep == 0) {
                                if (address.isBlank() || price.isBlank() || area.isBlank()) {
                                    Toast.makeText(context, "Vui lòng nhập đầy đủ Địa chỉ, Giá phòng và Diện tích.", Toast.LENGTH_SHORT).show()
                                } else {
                                    currentStep++
                                }
                            } else if (currentStep == 1 && mainImage.value.isEmpty()) {
                                Toast.makeText(context, "Bạn cần thêm ảnh chính để tiếp tục.", Toast.LENGTH_SHORT).show()
                            } else if (currentStep < 2) {
                                currentStep++
                            }  else {
                                val room = Room(
                                    title = "Tin đăng mới",
                                    description = description,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    area = area.toDoubleOrNull() ?: 0.0,
                                    location = address,
                                    latitude = latitude,
                                    longitude = longitude,
                                    roomType = roomType,
                                    roomCategory = roomCategory,
                                    amenities = selectedAmenities.toList(),
                                    mainImage = mainImage.value,
                                    images = imageList.toList(),
                                    isAvailable = true,
                                    owner_id = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                    created_at = System.currentTimeMillis()
                                )

                                scope.launch {
                                    roomViewModel.addRoom(room) { success ->
                                        if (success) {
                                            shouldRefresh.value = true
                                            Toast.makeText(context, "Đăng tin thành công", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, "Thất bại. Vui lòng thử lại", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        },
                        enabled = true
//                        enabled = address.isNotBlank() && price.isNotBlank() && area.isNotBlank()
                    ) {
                        Text(
                            text = if (currentStep < 2) "Tiếp theo" else "Xong",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
            )
        }
    ){ paddingValues ->
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
                0 -> InfoStep(
                    navController,
                    roomType, { roomType = it },
                    roomCategory, { roomCategory = it },
                    address, { address = it },
                    price, { price = it },
                    area, { area = it },
                    description, { description = it },
                    latitude, longitude,
                    selectedAmenities
                )
                1 -> ImageUploadStep(mainImage, imageList)
                2 -> ConfirmStep(
                    roomType, roomCategory, address, price, area,
                    selectedAmenities, mainImage.value, imageList
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

private fun RoomViewModel.addRoomToFirebase(room: Room) {}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InfoStep(
    navController: NavController,
    roomType: String, onRoomTypeChange: (String) -> Unit,
    roomCategory: String, onRoomCategoryChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    price: String, onPriceChange: (String) -> Unit,
    area: String, onAreaChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    latitude: Double,
    longitude: Double,
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



                //chọn vị trí
                TextButton(onClick = {
                    navController.navigate(Routes.SELECT_LOCATION)
                }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Chọn vị trí", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chọn vị trí", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                }



            }

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Nhập địa chỉ") }
            )

            if (latitude != 0.0 && longitude != 0.0) {
                Text(
                    text = "Tọa độ: $latitude, $longitude",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
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

        // Nhập mô tả
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Mô tả",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Nhập mô tả chi tiết") }
            )

        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImageUploadStep(
    mainImage: MutableState<String>,
    imageList: SnapshotStateList<String>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val contentResolver = context.contentResolver

    val launcherMainImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            scope.launch {
                uploadImageToCloudinary(
                    imageUri = it,
                    contentResolver = contentResolver,
                    cloudName = "dnkjhbw9m",
                    uploadPreset = "NhaTro247",
                    onSuccess = { imageUrl, _ ->
                        mainImage.value = imageUrl
                    },
                    onError = { error ->
                        Toast.makeText(context, "Lỗi upload: $error", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    val launcherAdditionalImages = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            if (imageList.size >= 9) {
                Toast.makeText(context, "Bạn chỉ được thêm tối đa 9 ảnh phụ.", Toast.LENGTH_SHORT).show()
                return@let
            }
            scope.launch {
                uploadImageToCloudinary(
                    imageUri = it,
                    contentResolver = contentResolver,
                    cloudName = "dnkjhbw9m",
                    uploadPreset = "NhaTro247",
                    onSuccess = { imageUrl, _ ->
                        imageList.add(imageUrl)
                    },
                    onError = { error ->
                        Toast.makeText(context, "Lỗi upload: $error", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ảnh Chính", style = MaterialTheme.typography.titleMedium)

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (mainImage.value.isEmpty()) {
                Button(onClick = { launcherMainImage.launch("image/*") }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Thêm ảnh chính")
                }
            } else {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .size(180.dp)
                        .padding(4.dp)
                ) {
                    Box {
                        AsyncImage(
                            model = mainImage.value,
                            contentDescription = "Ảnh chính",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { mainImage.value = "" },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color.White.copy(alpha = 0.4f), shape = CircleShape)

                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Xóa ảnh", tint = Color.Red)
                        }
                    }
                }
            }
        }

        Divider()

        Text("Ảnh Phụ (tối đa 9 ảnh)", style = MaterialTheme.typography.titleMedium)

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            imageList.forEachIndexed { index, imageUrl ->
                Card(

                    modifier = Modifier.size(150.dp)
                ) {
                    Box(       modifier = Modifier.size(200.dp)) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Ảnh phụ $index",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { imageList.removeAt(index) },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.White.copy(alpha = 0.4f), shape = CircleShape)

                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Xóa ảnh", tint = Color.Red)
                        }
                    }
                }
            }

            if (imageList.size < 9) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { launcherAdditionalImages.launch("image/*") },
                ) {
                    Box(contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Gray)
                            Text("Thêm ảnh", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

//di để zoom
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfirmStep(
    roomType: String, roomCategory: String, address: String,
    price: String, area: String, selectedAmenities: List<String>,
    mainImage: String, images: List<String>
) {
    val configuration = LocalConfiguration.current
    val maxZoomedHeight = configuration.screenHeightDp.dp * 0.6f
    var isZoomed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Xác nhận thông tin phòng",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Thông tin chi tiết",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp, modifier = Modifier.alpha(0.3f))

                InfoRow(label = "Loại tin", value = roomType)
                InfoRow(label = "Loại phòng", value = roomCategory)
                InfoRow(label = "Địa chỉ", value = address)
                InfoRow(label = "Giá thuê", value = "$price VND")
                InfoRow(label = "Diện tích", value = "$area m²")
                InfoRow(
                    label = "Tiện ích",
                    value = if (selectedAmenities.isNotEmpty()) selectedAmenities.joinToString(", ") else "Không có tiện ích"
                )

            }
        }

        Text(
            "Ảnh chính",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        if (mainImage.isNotEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isZoomed) maxZoomedHeight else 220.dp)
                    .shadow(if (isZoomed) 12.dp else 4.dp, RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isZoomed = true
                                tryAwaitRelease()
                                isZoomed = false
                            }
                        )
                    }
            ) {
                AsyncImage(
                    model = mainImage,
                    contentDescription = "Ảnh chính",
                    contentScale = if (isZoomed) ContentScale.Fit else ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        } else {
            Text("Chưa có ảnh chính", color = MaterialTheme.colorScheme.error)
        }

        Text(
            "Ảnh phụ",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        val isOdd = images.size % 2 != 0
        val lastIndex = images.lastIndex

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            images.forEachIndexed { index, imageUrl ->
                val isLastOddItem = isOdd && index == lastIndex

                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = if (isLastOddItem) {
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    } else {
                        Modifier.size(150.dp)
                    }
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Ảnh phụ $index",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (images.isEmpty()) {
                Text("Không có ảnh khác", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


//ấn để zoom
//@Composable
//fun ConfirmStep(
//    roomType: String, roomCategory: String, address: String,
//    price: String, area: String, selectedAmenities: List<String>,
//    mainImage: String, images: List<String>
//) {
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
//
//    val zoomLevels = listOf(200.dp, screenHeight * 0.4f, screenHeight * 0.7f)
//    var zoomIndex by remember { mutableStateOf(0) }
//
//    Column(
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Text("Xác nhận lại thông tin", fontSize = 16.sp)
//        Text("Loại tin: $roomType")
//        Text("Loại phòng: $roomCategory")
//        Text("Địa chỉ: $address")
//        Text("Giá: $price VND")
//        Text("Diện tích: $area m²")
//        Text("Tiện ích: ${selectedAmenities.joinToString()}")
//
//        Spacer(modifier = Modifier.height(8.dp))
//        Text("Ảnh chính (ấn vào ảnh để zoom):", style = MaterialTheme.typography.titleMedium)
//
//        if (mainImage.isNotEmpty()) {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(zoomLevels[zoomIndex])
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onTap = {
//                                zoomIndex = (zoomIndex + 1) % zoomLevels.size
//                            }
//                        )
//                    }
//            ) {
//                Card(
//                    shape = RoundedCornerShape(8.dp),
//                    elevation = CardDefaults.cardElevation(8.dp),
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    AsyncImage(
//                        model = mainImage,
//                        contentDescription = "Ảnh chính",
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//            }
//        } else {
//            Text("Chưa có ảnh chính")
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//        Text("Số ảnh phụ: ${images.size}")
//    }
//}
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
