package com.example.nhatro24_7.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.nhatro24_7.data.model.ActivityLog
import com.example.nhatro24_7.data.model.BookingRequest
import com.example.nhatro24_7.data.model.Review
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.model.SavedRoom
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RoomViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _rooms = mutableStateListOf<Room>()
    private val db = FirebaseFirestore.getInstance()
    val rooms: List<Room> get() = _rooms
    private val viewedRoomIds = mutableSetOf<String>()
    init {
        fetchRooms()
    }

    fun fetchRooms() {
        firestore.collection("rooms")
            .whereEqualTo("isAvailable", true)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                _rooms.clear()
                for (document in result) {
                    val room = document.toObject(Room::class.java).copy(id = document.id)
                    _rooms.add(room)
                }
            }
            .addOnFailureListener {
                Log.e("RoomViewModel", "Lỗi khi lấy dữ liệu phòng", it)
            }
    }


    fun addRoom(room: Room, onResult: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("rooms").document()
        val roomId = docRef.id

        val roomMap = hashMapOf(
            "id" to roomId,
            "title" to room.title,
            "description" to room.description,
            "price" to room.price,
            "location" to room.location,
            "area" to room.area,
            "roomType" to room.roomType,
            "roomCategory" to room.roomCategory,
            "amenities" to room.amenities,
            "mainImage" to room.mainImage,
            "images" to room.images,
            "viewCount" to room.viewCount,
            "isAvailable" to room.isAvailable,
            "owner_id" to room.owner_id,
            "created_at" to room.created_at
        )

        docRef.set(roomMap)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener { exception ->
                Log.e("RoomViewModel", "Lỗi khi thêm phòng mới: ${exception.message}", exception)
                onResult(false)
            }
    }


    //Lưu phòng
    fun unsaveRoom(savedRoom: SavedRoom, onResult: (Boolean) -> Unit = {}) {
        val docId = "${savedRoom.userId}_${savedRoom.roomId}"
        db.collection("saved_rooms").document(docId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun saveRoom(savedRoom: SavedRoom, onResult: (Boolean) -> Unit = {}) {
        val docId = "${savedRoom.userId}_${savedRoom.roomId}"
        db.collection("saved_rooms").document(docId)
            .set(savedRoom)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getSavedRooms(userId: String, onResult: (List<Room>) -> Unit) {
        db.collection("saved_rooms")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val roomIds = result.mapNotNull { it.getString("roomId") }

                if (roomIds.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                db.collection("rooms")
                    .whereIn("id", roomIds)
                    .get()
                    .addOnSuccessListener { roomResult ->
                        val rooms = roomResult.map { it.toObject(Room::class.java).copy(id = it.id) }
                        onResult(rooms)
                    }
            }
    }


    fun logViewRoom(userId: String, roomId: String) {
        val log = ActivityLog(
            id = UUID.randomUUID().toString(),
            userId = userId,
            action = "Xem phòng",
            roomId = roomId,
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("activity_logs")
            .document(log.id)
            .set(log)
            .addOnSuccessListener {
                Log.d("RoomViewModel", "Đã ghi log xem phòng.")
            }
            .addOnFailureListener {
                Log.e("RoomViewModel", "Lỗi khi ghi log xem phòng", it)
            }
    }

    fun incrementRoomViewCount(roomId: String) {
        val roomRef = firestore.collection("rooms").document(roomId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(roomRef)
            val currentCount = snapshot.getLong("viewCount") ?: 0
            transaction.update(roomRef, "viewCount", currentCount + 1)
        }.addOnSuccessListener {
            Log.d("RoomViewModel", "Đã tăng lượt xem")
        }.addOnFailureListener {
            Log.e("RoomViewModel", "Lỗi khi tăng lượt xem", it)
        }
    }

    fun logAndIncrementView(userId: String, roomId: String) {
        if (viewedRoomIds.contains(roomId)) return

        logViewRoom(userId, roomId)
        incrementRoomViewCount(roomId)
        viewedRoomIds.add(roomId)
    }

    //đặt phòng
    fun hasUserBookedRoom(userId: String, roomId: String, onResult: (Boolean) -> Unit) {
        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                onResult(!result.isEmpty) // true nếu đã đặt phòng
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun sendBookingRequest(
        roomId: String,
        userId: String,
        landlordId: String,
        onResult: (Boolean) -> Unit
    ) {
        val requestId = UUID.randomUUID().toString()
        val request = hashMapOf(
            "id" to requestId,
            "roomId" to roomId,
            "userId" to userId,
            "landlordId" to landlordId,
            "status" to "pending",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("booking_requests").document(requestId)
            .set(request)
            .addOnSuccessListener {
                // Gửi thông báo cho chủ trọ
                sendUserNotification(
                    userId = landlordId,
                    message = "Bạn có yêu cầu đặt phòng mới từ người dùng."
                )
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }


    // đánh giá
    fun submitReview(review: Review, onResult: (Boolean) -> Unit) {
        db.collection("reviews").document(review.id)
            .set(review)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getReviewsByRoomId(roomId: String, onResult: (List<Review>) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                val reviews = result.map { it.toObject(Review::class.java) }
                onResult(reviews)
            }
    }

    //xem thông tin chủ trọ
    fun fetchRoomsByOwner(ownerId: String, onResult: (List<Room>) -> Unit) {
        db.collection("rooms")
            .whereEqualTo("owner_id", ownerId)
            .get()
            .addOnSuccessListener { result ->
                val rooms = result.map { it.toObject(Room::class.java).copy(id = it.id) }
                onResult(rooms)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }


    fun markRoomAsUnavailable(roomId: String, onResult: (Boolean) -> Unit = {}) {
        db.collection("rooms").document(roomId)
            .update("isAvailable", false)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    //gửi để chủ trọ
    fun getBookingRequestsForLandlord(landlordId: String, onResult: (List<BookingRequest>) -> Unit) {
        db.collection("booking_requests")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val requests = result.map { it.toObject(BookingRequest::class.java) }
                onResult(requests) // KHÔNG LỌC landlordId để test
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }


    // kiểm tra đã gửi y/c ch
    fun hasPendingBookingRequest(userId: String, roomId: String, onResult: (Boolean) -> Unit) {
        db.collection("booking_requests")
            .whereEqualTo("userId", userId)
            .whereEqualTo("roomId", roomId)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { result ->
                onResult(!result.isEmpty)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun updateBookingStatus(requestId: String, status: String, onResult: (Boolean) -> Unit) {
        db.collection("booking_requests")
            .document(requestId)
            .update("status", status)
            .addOnSuccessListener {
                if (status == "accepted" || status == "rejected") {
                    db.collection("booking_requests").document(requestId).get()
                        .addOnSuccessListener { doc ->
                            val userId = doc.getString("userId") ?: return@addOnSuccessListener
                            val msg = if (status == "accepted") {
                                "Yêu cầu đặt phòng của bạn đã được chấp nhận!"
                            } else {
                                "Yêu cầu đặt phòng của bạn đã bị từ chối!"
                            }
                            sendUserNotification(userId, msg)
                        }
                }
                onResult(true)
            }
            .addOnFailureListener { onResult(false) }
    }


    fun getLatestBookingStatus(userId: String, onResult: (String) -> Unit) {
        db.collection("booking_requests")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val status = result.firstOrNull()?.getString("status") ?: "pending"
                onResult(status)
            }
            .addOnFailureListener { onResult("pending") }
    }

    fun sendUserNotification(userId: String, message: String) {
        val notification = hashMapOf(
            "userId" to userId,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        FirebaseFirestore.getInstance()
            .collection("notifications")
            .add(notification)
    }


}
