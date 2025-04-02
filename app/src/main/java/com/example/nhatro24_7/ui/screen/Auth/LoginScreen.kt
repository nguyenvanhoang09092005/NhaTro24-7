package com.example.nhatro24_7.ui.screen.Auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nhatro24_7.R
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    isLoading: Boolean,
    onNavigateToHome: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) } 
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("691844467671-9on86a7ohmlnl889o8u3t9theh0n7s9n.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    //  đăng nhập Google
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Đăng xuất khỏi tài khoản Google cũ trước khi đăng nhập lại
            googleSignInClient.signOut().addOnCompleteListener {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                account?.let {
                    viewModel.signInWithGoogle(it) { success, role ->
                        if (success) {
                            onNavigateToHome(role.toString())
                        } else {
                            loginError = "Đăng nhập Google thất bại"
                        }
                    }
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB3E5FC), Color(0xFF81D4FA))
                )
            )
            .clickable { focusManager.clearFocus() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .shadow(15.dp, RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Đăng nhập", fontSize = 26.sp, color = Color(0xFF0277BD))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Chào mừng bạn trở lại", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            if (loginError != null) {
                Text(text = loginError!!, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.signIn(email.trim(), password) { success, role ->
                        if (success) {
                            onNavigateToHome(role) // Điều hướng theo vai trò
                        } else {
                            loginError = "Email hoặc mật khẩu không chính xác"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
            ) {
                Text("Đăng Nhập", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Chưa có tài khoản? Đăng ký ngay",
                fontSize = 14.sp,
                color = Color(0xFF03A9F4),
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = Color.Gray
                )
                Text(
                    text = "Hoặc đăng nhập bằng",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(painter = painterResource(R.drawable.gg), contentDescription = "Google", modifier = Modifier.size(40.dp).clickable {
                    launcher.launch(googleSignInClient.signInIntent) 
                })
               Image(painter = painterResource(R.drawable.fb), contentDescription = "Facebook", modifier = Modifier.size(40.dp))
                Image(painter = painterResource(R.drawable.github), contentDescription = "Github", modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Quên mật khẩu?", fontSize = 14.sp, color = Color(0xFF03A9F4), modifier = Modifier.clickable { /* TODO: Handle forgot password */ })
        }
    }


}
