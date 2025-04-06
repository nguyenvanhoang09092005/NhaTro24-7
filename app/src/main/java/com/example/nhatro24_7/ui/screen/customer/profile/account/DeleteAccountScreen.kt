package com.example.nhatro24_7.ui.screen.customer.profile.account

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(navController: NavController) {
    val context: Context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    var isDeleting by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xóa tài khoản") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(
                    text = "Cảnh báo: Thao tác này sẽ xóa toàn bộ tài khoản và dữ liệu liên quan.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xác nhận xóa tài khoản", color = Color.White)
                }
            }

            if (isDeleting) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Xác nhận xóa tài khoản") },
                    text = { Text("Bạn có chắc chắn muốn xóa tài khoản không? Thao tác này không thể hoàn tác.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                isDeleting = true
                                coroutineScope.launch {
                                    try {
                                        user?.reload()

                                        if (user != null) {
                                            user.delete().addOnCompleteListener { deleteTask ->
                                                coroutineScope.launch {
                                                    isDeleting = false
                                                    if (deleteTask.isSuccessful) {
                                                        snackbarHostState.showSnackbar("Tài khoản đã được xóa.")
                                                        delay(1500)
                                                        navController.navigate("splash") {
                                                            popUpTo(0)
                                                        }
                                                    } else {
                                                        val exception = deleteTask.exception
                                                        if (exception is FirebaseAuthRecentLoginRequiredException) {
                                                            reauthenticateAndDelete(user, context, snackbarHostState, navController)
                                                        } else {
                                                            snackbarHostState.showSnackbar("Lỗi: ${exception?.localizedMessage}")
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            isDeleting = false
                                            snackbarHostState.showSnackbar("Không tìm thấy người dùng.")
                                        }
                                    } catch (e: Exception) {
                                        isDeleting = false
                                        snackbarHostState.showSnackbar("Lỗi không xác định: ${e.localizedMessage}")
                                    }
                                }
                            }
                        ) {
                            Text("Xóa", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    }
}

// Hàm xác thực lại (chỉ xử lý với Google - có thể mở rộng Email/Password)
private fun reauthenticateAndDelete(
    user: FirebaseUser,
    context: Context,
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {
    val coroutineScope = CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
    val account = GoogleSignIn.getLastSignedInAccount(context)
    val idToken = account?.idToken

    if (idToken != null) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        user.reauthenticate(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    user.delete().addOnCompleteListener { deleteTask ->
                        coroutineScope.launch {
                            if (deleteTask.isSuccessful) {
                                snackbarHostState.showSnackbar("Tài khoản đã được xóa.")
                                delay(1500)
                                navController.navigate("splash") {
                                    popUpTo(0)
                                }
                            } else {
                                snackbarHostState.showSnackbar("Lỗi khi xóa tài khoản: ${deleteTask.exception?.localizedMessage}")
                            }
                        }
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Xác thực lại thất bại: ${authTask.exception?.localizedMessage}")
                    }
                }
            }
    } else {
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Không thể xác thực lại: Không tìm thấy tài khoản Google.")
        }
    }
}
