package com.example.nhatro24_7.ui.screen.landlord.profile

import kotlinx.coroutines.tasks.await
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandlordProfileScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var avatarUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

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

    var isEditing by remember { mutableStateOf(false) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            avatarUrl = it.toString()
        }
    }

    LaunchedEffect(Unit) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
            avatarUrl = doc.getString("avatarUrl") ?: ""
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
            landlordZalo = doc.getString("landlordZalo") ?: ""
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
                    IconButton(onClick = { isEditing = !isEditing }) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable(enabled = isEditing) { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(avatarUrl),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Chọn ảnh", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Thông tin cá nhân")
            EditableField("Tên người dùng", username, isEditing) { username = it }
            EditableField("Email", email, false)
            EditableField("Số điện thoại", phone, isEditing, KeyboardType.Phone) { phone = it }
            EditableField("Ngày sinh", birthDate, isEditing) { birthDate = it }
            EditableField("Quê quán", hometown, isEditing) { hometown = it }
            EditableField("Địa chỉ hiện tại", currentAddress, isEditing) { currentAddress = it }

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Thông tin chủ trọ")
            EditableField("Tên chủ trọ", landlordName, isEditing) { landlordName = it }
            EditableField("CMND/CCCD", landlordIdNumber, isEditing, KeyboardType.Number) { landlordIdNumber = it }
            EditableField("Số tài khoản ngân hàng", landlordBankAccount, isEditing, KeyboardType.Number) { landlordBankAccount = it }
            EditableField("Ngân hàng", landlordBankName, isEditing) { landlordBankName = it }
            EditableField("Số Zalo", landlordZalo, isEditing, KeyboardType.Phone) { landlordZalo = it }

            if (isEditing) {
                Button(
                    onClick = {
                        scope.launch {
                            val updatedData = mapOf(
                                "avatarUrl" to avatarUrl,
                                "username" to username,
                                "phone" to phone,
                                "birthDate" to birthDate,
                                "hometown" to hometown,
                                "currentAddress" to currentAddress,
                                "landlordName" to landlordName,
                                "landlordIdNumber" to landlordIdNumber,
                                "landlordBankAccount" to landlordBankAccount,
                                "landlordBankName" to landlordBankName,
                                "landlordZalo" to landlordZalo
                            )
                            FirebaseFirestore.getInstance().collection("users").document(userId)
                                .update(updatedData)
                                .addOnSuccessListener {
                                    scope.launch { snackbarHostState.showSnackbar("Cập nhật thành công!") }
                                    isEditing = false
                                }
                                .addOnFailureListener {
                                    scope.launch { snackbarHostState.showSnackbar("Cập nhật thất bại!") }
                                }
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Lưu thay đổi")
                }
            }
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
    onValueChange: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth()
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
    }
    Spacer(modifier = Modifier.height(8.dp))
}
