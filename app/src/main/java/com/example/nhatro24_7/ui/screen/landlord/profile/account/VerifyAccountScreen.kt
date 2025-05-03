package com.example.nhatro24_7.ui.screen.landlord.profile.account

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyAccountScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xác minh tài khoản") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nếu bạn chưa xác minh email, vui lòng kiểm tra hộp thư hoặc nhấn nút bên dưới để gửi lại email xác minh.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    if (user != null && !user.isEmailVerified) {
                        coroutineScope.launch {
                            user.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    coroutineScope.launch {
                                        if (task.isSuccessful) {
                                            snackbarHostState.showSnackbar("Email xác minh đã được gửi.")
                                        } else {
                                            val error = task.exception?.localizedMessage ?: "Không thể gửi email xác minh."
                                            snackbarHostState.showSnackbar("Lỗi: $error")
                                        }
                                    }
                                }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Tài khoản đã được xác minh hoặc không tìm thấy người dùng.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gửi lại email xác minh")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        user?.reload()?.addOnCompleteListener {
                            coroutineScope.launch {
                                if (user.isEmailVerified) {
                                    snackbarHostState.showSnackbar("Tài khoản đã được xác minh.")
                                } else {
                                    snackbarHostState.showSnackbar("Tài khoản vẫn chưa được xác minh.")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kiểm tra trạng thái xác minh")
            }
        }
    }
}
