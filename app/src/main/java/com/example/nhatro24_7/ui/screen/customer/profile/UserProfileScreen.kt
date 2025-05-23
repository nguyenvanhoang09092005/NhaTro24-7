package com.example.nhatro24_7.ui.screen.customer.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.nhatro24_7.ui.screen.landlord.profile.EditableField
import com.example.nhatro24_7.ui.screen.landlord.profile.SectionTitle
import com.example.nhatro24_7.util.deleteImageFromCloudinary
import com.example.nhatro24_7.util.uploadImageToCloudinary
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(viewModel: AuthViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val contentResolver = context.contentResolver

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userId = firebaseUser?.uid ?: return

    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    var avatarUrl by remember { mutableStateOf("") }
    var avatarPublicId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var hometown by remember { mutableStateOf("") }
    var currentAddress by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
            avatarUrl = doc.getString("avatarUrl") ?: ""
            avatarPublicId = doc.getString("avatarPublicId") ?: ""
            username = doc.getString("username") ?: ""
            email = doc.getString("email") ?: firebaseUser.email.orEmpty()
            phone = doc.getString("phone") ?: ""
            birthDate = doc.getString("birthDate") ?: ""
            hometown = doc.getString("hometown") ?: ""
            currentAddress = doc.getString("currentAddress") ?: ""
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Không thể tải dữ liệu người dùng.")
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            scope.launch {
                uploadImageToCloudinary(
                    imageUri = it,
                    contentResolver = contentResolver,
                    cloudName = "dnkjhbw9m",
                    uploadPreset = "NhaTro247",
                    onSuccess = { imageUrl, publicId ->
                        // Xóa ảnh cũ nếu có
                        if (avatarPublicId.isNotBlank()) {
                            scope.launch {
                                deleteImageFromCloudinary(
                                    publicId = avatarPublicId,
                                    cloudName = "dnkjhbw9m",
                                    apiKey = "791292363868727",
                                    apiSecret = "_5aBOAaLNCUabVPcyZMxwH-j1yY",
                                    onSuccess = {},
                                    onError = { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Không thể xoá ảnh cũ: $error")
                                        }
                                    }
                                )
                            }
                        }

                        avatarUrl = imageUrl
                        avatarPublicId = publicId
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(userId)
                            .update(mapOf(
                                "avatarUrl" to imageUrl,
                                "avatarPublicId" to publicId
                            ))
                        scope.launch {
                            snackbarHostState.showSnackbar("Cập nhật ảnh đại diện thành công!")
                        }
                    },
                    onError = { error ->
                        scope.launch {
                            snackbarHostState.showSnackbar("Lỗi upload ảnh: $error")
                        }
                    }
                )
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ") },
                actions = {
                    IconButton(
                        onClick = {
                            if (isEditing) {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        val updatedData = mapOf(
                                            "username" to username,
                                            "phone" to phone,
                                            "birthDate" to birthDate,
                                            "hometown" to hometown,
                                            "currentAddress" to currentAddress
                                        )
                                        FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(userId)
                                            .update(updatedData)
                                            .await()
                                        snackbarHostState.showSnackbar("Cập nhật thành công")
                                        isEditing = false
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Lỗi: ${e.localizedMessage}")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                isEditing = true
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Lưu" else "Chỉnh sửa"
                        )
                    }
                }
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                val imageToDisplay = if (avatarUrl.isNotBlank()) avatarUrl else null

                if (imageToDisplay != null) {
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(imageToDisplay)
                            .crossfade(true)
                            .build()
                    )

                    Image(
                        painter = painter,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Chọn ảnh",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Chọn ảnh",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Thông tin cá nhân")
            Spacer(modifier = Modifier.height(16.dp))

            EditableField("Tên người dùng", username, isEditing) { username = it }
            EditableField("Email", email, false) {}
            EditableField("Số điện thoại", phone, isEditing, KeyboardType.Phone) { phone = it }
            EditableField("Ngày sinh", birthDate, isEditing) { birthDate = it }
            EditableField("Quê quán", hometown, isEditing) { hometown = it }
            EditableField("Nơi ở hiện tại", currentAddress, isEditing) { currentAddress = it }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
