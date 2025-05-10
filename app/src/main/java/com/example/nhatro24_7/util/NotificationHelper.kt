//package com.example.nhatro24_7.util
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.example.nhatro24_7.R
//import kotlin.random.Random
//
//class NotificationHelper(private val context: Context) {
//
//    companion object {
//        private const val CHANNEL_ID = "booking_response_channel"
//        private const val CHANNEL_NAME = "Thông báo phản hồi"
//        private const val CHANNEL_DESC = "Thông báo từ chủ trọ về yêu cầu đặt phòng"
//    }
//
//    init {
//        createNotificationChannel()
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = CHANNEL_DESC
//            }
//            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(channel)
//        }
//    }
//
//    fun showNotification(title: String, message: String) {
//        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_notification) // Đảm bảo bạn có icon này trong drawable
//            .setContentTitle(title)
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//            .build()
//
//        with(NotificationManagerCompat.from(context)) {
//            notify(Random.nextInt(), notification)
//        }
//    }
//}
