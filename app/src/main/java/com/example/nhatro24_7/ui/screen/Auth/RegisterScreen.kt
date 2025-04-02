package com.example.nhatro24_7.ui.screen.Auth

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nhatro24_7.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image

@Composable
fun RegisterScreen(onLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()


    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("691844467671-9on86a7ohmlnl889o8u3t9theh0n7s9n.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Đăng ký Google
    fun handleGoogleSignIn(account: GoogleSignInAccount) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "role" to "customer"
                    )
                    userId?.let {
                        db.collection("users").document(it).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Đăng ký Google thành công!", Toast.LENGTH_SHORT).show()
                                onLoginClick()
                            }
                            .addOnFailureListener {
                                errorMessage = "Lỗi khi lưu dữ liệu: ${it.message}"
                            }
                    }
                } else {
                    errorMessage = task.exception?.message ?: "Đăng ký thất bại."
                }
            }
    }

    // Đăng ký bằng Google
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            account?.let {
                handleGoogleSignIn(it)
            } ?: run {
                errorMessage = "Đăng nhập Google thất bại"
            }
        } else {
            errorMessage = "Đăng nhập Google thất bại"
        }
    }


    fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }


    fun registerUser() {
        if (email.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Vui lòng điền đầy đủ thông tin."
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Mật khẩu nhập lại không khớp."
            return
        }

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "role" to "customer"
                    )
                    userId?.let {
                        db.collection("users").document(it).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                onLoginClick()
                            }
                            .addOnFailureListener {
                                errorMessage = "Lỗi khi lưu dữ liệu: ${it.message}"
                            }
                    }
                } else {
                    errorMessage = task.exception?.message ?: "Đăng ký thất bại."
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .shadow(15.dp, RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Đăng ký", fontSize = 26.sp, color = Color(0xFF0277BD))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tạo tài khoản mới", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email hoặc Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên người dùng") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Nhập lại mật khẩu") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { registerUser() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(10.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
            ) {
                Text("Đăng Ký", fontSize = 18.sp, color = Color.White)
            }
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
                    text = "Hoặc đăng ký bằng",
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
                Image(
                    painter = painterResource(R.drawable.gg),
                    contentDescription = "Google",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(onClick = { signInWithGoogle() })
                )
                Image(painter = painterResource(R.drawable.fb), contentDescription = "Facebook", modifier = Modifier.size(40.dp))
                Image(painter = painterResource(R.drawable.github), contentDescription = "Github", modifier = Modifier.size(40.dp))

            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Đã có tài khoản? Đăng nhập ngay", fontSize = 14.sp, color = Color(0xFF03A9F4), modifier = Modifier.clickable { onLoginClick() })
        }
    }
}
