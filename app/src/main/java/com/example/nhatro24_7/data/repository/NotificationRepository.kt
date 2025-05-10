//package com.example.nhatro24_7.data.repository
//
//import com.example.nhatro24_7.data.model.Notification
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//
//class NotificationRepository {
//    private val db = FirebaseFirestore.getInstance()
//    private val notificationsRef = db.collection("notifications")
//
//    suspend fun addNotification(notification: Notification) {
//        notificationsRef.document(notification.id).set(notification).await()
//    }
//
//    suspend fun getNotifications(): List<Notification> {
//        val snapshot = notificationsRef.get().await()
//        return snapshot.toObjects(Notification::class.java)
//    }
//
//    suspend fun markAsRead(notificationId: String) { /* cập nhật isRead = true */ }
//
//}
