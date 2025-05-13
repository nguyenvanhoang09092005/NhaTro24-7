package com.example.nhatro24_7.ui.screen.customer.payment

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.nhatro24_7.util.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeScreen(
    navController: NavHostController,
    amount: Long,
    transferContent: String,
    onBack: () -> Unit
) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(amount, transferContent) {
        val content = "$amount\n$transferContent"
        qrBitmap = QRCodeGenerator.generate(content)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mã QR Thanh Toán") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (qrBitmap != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Quét mã QR để thanh toán", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(20.dp))

                // Hiển thị mã QR dưới dạng hình ảnh
                Image(bitmap = qrBitmap!!.asImageBitmap(), contentDescription = null)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Quay lại", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            // Hiển thị CircularProgressIndicator khi đang tạo mã QR
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
