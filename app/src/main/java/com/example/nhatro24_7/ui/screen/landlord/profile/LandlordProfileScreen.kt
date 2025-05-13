package com.example.nhatro24_7.ui.screen.landlord.profile

import android.R.attr.editable
import kotlinx.coroutines.tasks.await
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.nhatro24_7.data.model.User
import com.example.nhatro24_7.util.deleteImageFromCloudinary
import com.example.nhatro24_7.util.uploadImageToCloudinary
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandlordProfileScreen(viewModel: AuthViewModel, navController: NavController)
{
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var avatarUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var hometown by remember { mutableStateOf("") }
    var currentAddress by remember { mutableStateOf("") }
    var landlordName by remember { mutableStateOf("") }
    var landlordIdNumber by remember { mutableStateOf("") }
    var landlordBankAccount by remember { mutableStateOf("") }
    var landlordBankName by remember { mutableStateOf("") }
    var landlordZalo by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val userState = remember { mutableStateOf(User()) }
    var isEditing by remember { mutableStateOf(false) }
    var avatarPublicId by remember { mutableStateOf("") }

    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            scope.launch {
                uploadImageToCloudinary(
                    imageUri = it,
                    contentResolver = context.contentResolver,
                    cloudName = "dnkjhbw9m",
                    uploadPreset = "NhaTro247",
                    onSuccess = { imageUrl, publicId ->
                        // Xoá ảnh cũ nếu có
//                        if (avatarPublicId.isNotBlank()) {
//                            deleteImageFromCloudinary(
//                                publicId = avatarPublicId,
//                                cloudName = "dnkjhbw9m",
//                                apiKey = "791292363868727",
//                                apiSecret = "_5aBOAaLNCUabVPcyZMxwH-j1yY",
//                                onSuccess = {},
//                                onError = { error ->
//                                    scope.launch {
//                                        snackbarHostState.showSnackbar("Không thể xoá ảnh cũ: $error")
//                                    }
//                                }
//                            )
//                        }

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


    LaunchedEffect(Unit) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
            avatarUrl = doc.getString("avatarUrl") ?: ""
            avatarPublicId = doc.getString("avatarPublicId") ?: ""
            username = doc.getString("username") ?: ""
            email = doc.getString("email") ?: ""
            phone = doc.getString("phone") ?: ""
            birthDate = doc.getString("birthDate") ?: ""
            hometown = doc.getString("hometown") ?: ""
            currentAddress = doc.getString("currentAddress") ?: ""
            landlordName = doc.getString("landlordName") ?: ""
            landlordIdNumber = doc.getString("landlordIdNumber") ?: ""
            landlordBankAccount = doc.getString("landlordBankAccount") ?: ""
            landlordBankName = doc.getString("landlordBankName") ?: ""

        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Không thể tải dữ liệu người dùng.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ chủ trọ") },
                actions = {
                    IconButton(
                        onClick = {
                            if (isEditing) {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        val updatedData = mapOf(
                                            "username" to username,
                                            "landlordIdNumber" to landlordIdNumber,
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(enabled = isEditing) { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Avatar",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Thông tin cá nhân")
            EditableField("Tên người dùng", username, isEditing) { username = it }
            EditableField("Email", email, false)
            EditableField("Số điện thoại", phone, isEditing, KeyboardType.Phone) { phone = it }
            EditableField("CMND/CCCD", landlordIdNumber, isEditing, KeyboardType.Number) { landlordIdNumber = it }
            EditableField("Số tài khoản ngân hàng", landlordBankAccount,false)
            EditableField("Ngân hàng", landlordBankName, false)
            EditableField("Ngày sinh", birthDate, isEditing) { birthDate = it }
            EditableField("Quê quán", hometown, isEditing) { hometown = it }
            EditableField("Địa chỉ hiện tại", currentAddress, isEditing) { currentAddress = it }

        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun EditableField(
    label: String,
    value: String,
    isEditing: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    editable: Boolean = true,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                enabled = editable,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                placeholder = { Text("Nhập $label") }
            )
        } else {
            Text(
                text = value.ifEmpty { "Chưa có thông tin" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
