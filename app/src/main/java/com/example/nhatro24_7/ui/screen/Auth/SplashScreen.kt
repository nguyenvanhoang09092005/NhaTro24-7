package com.example.nhatro24_7.ui.screen.Auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.nhatro24_7.R
import com.example.nhatro24_7.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    var userRole by remember { mutableStateOf<String?>(null) }
    var rawProgress by remember { mutableStateOf(0f) }

    val progress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 100),
        label = "progressAnimation"
    )

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )

    LaunchedEffect(true) {
        startAnimation = true
        while (rawProgress < 1f) {
            rawProgress += 0.01f
            delay(15)
        }
        rawProgress = 0.99f

        delay(30)
        viewModel.checkIfLoggedIn { role ->
            userRole = role
        }
    }

    LaunchedEffect(userRole) {
        userRole?.let { role ->
            delay(500)
            if (role != "guest") {
                onNavigateToHome(role)
            } else {
                onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF56CCF2), Color(0xFF2F80ED)) // Gradient xanh dễ nhìn
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .alpha(alphaAnim.value)
                .padding(horizontal = 24.dp)
        ) {
            val user by viewModel.currentUser.collectAsState()

            val avatarPainter = if (!user?.avatarUrl.isNullOrEmpty()) {
                rememberAsyncImagePainter(user!!.avatarUrl)
            } else {
                painterResource(id = R.drawable.logo)
            }

            Image(
                painter = avatarPainter,
                contentDescription = "Ảnh đại diện người dùng",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.White.copy(alpha = 0.8f), CircleShape)
            )


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Nhà Trọ 24/7",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Tìm trọ nhanh chóng, thuận tiện",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 16.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

