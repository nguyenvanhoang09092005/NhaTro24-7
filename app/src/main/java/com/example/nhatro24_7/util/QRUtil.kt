//package com.example.nhatro24_7.util
//
//import android.graphics.Bitmap
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.graphics.ImageBitmap
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.qrcode.QRCodeWriter
//import android.graphics.Color
//
//
//fun generateQrCode(content: String): Bitmap {
//    val writer = QRCodeWriter()
//    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
//    val width = bitMatrix.width
//    val height = bitMatrix.height
//    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//
//    for (x in 0 until width) {
//        for (y in 0 until height) {
//            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
//        }
//    }
//
//    return bitmap
//}
