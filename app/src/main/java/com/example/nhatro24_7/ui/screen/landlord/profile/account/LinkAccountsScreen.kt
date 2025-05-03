package com.example.nhatro24_7.ui.screen.landlord.profile.account

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkAccountsScreen(navController: NavController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Trạng thái kiểm tra liên kết
    val isGoogleLinked = remember {
        mutableStateOf(currentUser?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true)
    }
    val isGithubLinked = remember {
        mutableStateOf(currentUser?.providerData?.any { it.providerId == "github.com" } == true)
    }

    // Configure Google Sign-In
    val googleSignInClient: GoogleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("691844467671-9on86a7ohmlnl889o8u3t9theh0n7s9n.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            firebaseAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener { linkTask ->
                    coroutineScope.launch {
                        if (linkTask.isSuccessful) {
                            isGoogleLinked.value = true
                            snackbarHostState.showSnackbar("Liên kết với Google thành công.")
                        } else {
                            val error = linkTask.exception?.localizedMessage ?: "Không thể liên kết với Google."
                            snackbarHostState.showSnackbar("Lỗi: $error")
                        }
                    }
                }
        } catch (e: Exception) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Google Sign-In thất bại: ${e.localizedMessage}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liên kết tài khoản") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                text = if (isGoogleLinked.value) "Google: Đã liên kết" else "Google: Chưa liên kết",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    if (isGoogleLinked.value) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Tài khoản đã liên kết với Google.")
                        }
                    } else {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Liên kết với Google")
            }

            Text(
                text = if (isGithubLinked.value) "GitHub: Đã liên kết" else "GitHub: Chưa liên kết",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    if (isGithubLinked.value) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Tài khoản đã liên kết với GitHub.")
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Tính năng liên kết với GitHub đang được phát triển.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Liên kết với GitHub")
            }
        }
    }
}
