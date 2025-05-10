//package com.example.nhatro24_7.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.nhatro24_7.data.model.Notification
//import com.example.nhatro24_7.data.repository.NotificationRepository
//import com.example.nhatro24_7.util.NotificationHelper
//import com.google.firebase.database.*
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class NotificationViewModel @Inject constructor(
//    private val notificationRepository: NotificationRepository,
//    private val notificationHelper: NotificationHelper
//) : ViewModel() {
//
//    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
//    val notifications: StateFlow<List<Notification>> get() = _notifications
//
//    private val firebaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("booking_responses")
//
//    init {
//        listenToBookingResponses()
//    }
//
//    private fun listenToBookingResponses() {
//        firebaseRef.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val message = snapshot.child("message").getValue(String::class.java) ?: return
//                val title = "Phản hồi từ chủ trọ"
//                notificationHelper.showNotification(title, message)
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                // Nếu cần lắng nghe thay đổi nội dung phản hồi
//                val message = snapshot.child("message").getValue(String::class.java) ?: return
//                val title = "Phản hồi được cập nhật"
//                notificationHelper.showNotification(title, message)
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {}
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//            override fun onCancelled(error: DatabaseError) {}
//        })
//    }
//
//    fun loadNotifications(userId: String) {
//        viewModelScope.launch {
//            _notifications.value = notificationRepository.getNotifications(userId)
//        }
//    }
//
//    fun addNotification(notification: Notification) {
//        viewModelScope.launch {
//            notificationRepository.addNotification(notification)
//            loadNotifications(notification.userId)
//        }
//    }
//
//    fun markAsRead(notificationId: String) {
//        viewModelScope.launch {
//            notificationRepository.markAsRead(notificationId)
//            // Gọi lại loadNotifications nếu cần thiết
//        }
//    }
//}
