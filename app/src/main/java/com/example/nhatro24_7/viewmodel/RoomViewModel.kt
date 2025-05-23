package com.example.nhatro24_7.viewmodel

import android.system.Os.close
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhatro24_7.data.model.ActivityLog
import com.example.nhatro24_7.data.model.BookingRequest
import com.example.nhatro24_7.data.model.Review
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.model.SavedRoom
import com.example.nhatro24_7.data.model.User
import com.example.nhatro24_7.data.repository.RoomRepository
import com.example.nhatro24_7.ui.screen.customer.search.calculateDistance
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


class RoomViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _rooms = mutableStateListOf<Room>()
    private val db = FirebaseFirestore.getInstance()
    val rooms: List<Room> get() = _rooms
    private val viewedRoomIds = mutableSetOf<String>()
//
//    private val _bookingRequests = MutableStateFlow<List<BookingRequest>>(sampleRequests)
//    val bookingRequests: StateFlow<List<BookingRequest>> = _bookingRequests
    init {
        fetchRooms()
        fetchUsers()
    }

    fun fetchRooms() {
        firestore.collection("rooms")
            .whereEqualTo("isAvailable", true)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("RoomViewModel", "Lỗi khi lấy dữ liệu phòng", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _rooms.clear()
                    for (document in snapshot.documents) {
                        val room = document.toObject(Room::class.java)?.copy(id = document.id)
                        room?.let { _rooms.add(it) }
                    }
                }
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
            "latitude" to room.latitude,
            "longitude" to room.longitude,
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


    fun updateRoom(room: Room, onResult: (Boolean) -> Unit) {
        val roomId = room.id ?: return onResult(false)

        val roomMap = hashMapOf(
            "title" to room.title,
            "description" to room.description,
            "price" to room.price,
            "location" to room.location,
            "latitude" to room.latitude,
            "longitude" to room.longitude,
            "area" to room.area,
            "roomType" to room.roomType,
            "roomCategory" to room.roomCategory,
            "amenities" to room.amenities,
            "mainImage" to room.mainImage,
            "images" to room.images,
            "isAvailable" to room.isAvailable,
            "owner_id" to room.owner_id,
            "created_at" to room.created_at
        )

        db.collection("rooms").document(roomId)
            .update(roomMap as Map<String, Any>)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


    fun deleteRoom(roomId: String, onResult: (Boolean) -> Unit) {
        db.collection("rooms").document(roomId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun generateRoomTitle(roomType: String, location: String, price: Double): String {
        val cleanedLocation = location.split(",").firstOrNull()?.trim() ?: location.trim()

        val formattedPrice = when {
            price >= 1_000_000 -> "${(price / 1_000_000).toInt()} triệu"
            price >= 1_000 -> "${(price / 1_000).toInt()} nghìn"
            else -> "$price đ"
        }

        val titleOptions = listOf(
            "$roomType tại $cleanedLocation, giá $formattedPrice",
            "$roomType giá $formattedPrice - $cleanedLocation",
            "$roomType $formattedPrice/$cleanedLocation"
        )

        val bestTitle = titleOptions.firstOrNull { it.length <= 50 } ?: titleOptions[0].take(47) + "..."
        return bestTitle
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

    fun notifyOwner(ownerId: String, title: String, message: String) {
        val notification = mapOf(
            "title" to title,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(ownerId)
            .collection("notifications")
            .add(notification)
    }


    fun getBookingRequestsFlow(userId: String): Flow<List<BookingRequest>> = callbackFlow {
        val listenerRegistration = db.collection("booking_requests")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(BookingRequest::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(requests)
            }

        awaitClose { listenerRegistration.remove() }
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
            .whereEqualTo("landlordId", landlordId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val requests = result.map { it.toObject(BookingRequest::class.java) }
                onResult(requests)
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

    fun fetchLandlordName(ownerId: String, onResult: (String) -> Unit) {
        db.collection("users").document(ownerId).get()
            .addOnSuccessListener { document ->
                val name = document.getString("fullName") ?: "Không rõ tên"
                onResult(name)
            }
            .addOnFailureListener {
                onResult("Không rõ tên")
            }
    }

    fun getRoomById(roomId: String, onResult: (Room?) -> Unit) {
        db.collection("rooms").document(roomId)
            .get()
            .addOnSuccessListener { document ->
                val room = document.toObject(Room::class.java)?.copy(id = document.id)
                onResult(room)
            }
            .addOnFailureListener { e ->
                Log.e("RoomViewModel", "Lỗi khi lấy dữ liệu phòng theo ID", e)
                onResult(null)
            }
    }

    // lấy danh sách phòng theo chủ trọ
    private val _roomsByLandlord = MutableStateFlow<List<Room>>(emptyList())
    val roomsByLandlord: StateFlow<List<Room>> = _roomsByLandlord

    fun getRoomsByLandlord(uid: String) {
        viewModelScope.launch {
            Firebase.firestore.collection("rooms")
                .whereEqualTo("owner_id", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener

                    val rooms = snapshot.documents.mapNotNull {
                        it.toObject(Room::class.java)?.copy(id = it.id)
                    }

                    _roomsByLandlord.value = rooms
                }
        }
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



    private val _users = mutableStateListOf<User>()
    val users: List<User> get() = _users

    fun fetchUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                _users.clear()
                for (doc in result) {
                    val user = doc.toObject(User::class.java).copy(id = doc.id)
                    _users.add(user)
                }
            }
            .addOnFailureListener {
                Log.e("RoomViewModel", "Lỗi khi lấy danh sách người dùng", it)
            }

    }

// ghi lịch sử xem
//fun logRoomViewActivity(userId: String, roomId: String) {
//    val log = ActivityLog(
//        id = UUID.randomUUID().toString(),
//        userId = userId,
//        roomId = roomId
//    )
//    Firebase.firestore
//        .collection("activity_logs")
//        .document(log.id)
//        .set(log)
//}


    // hủy/ trả phòng
    private val _returnSuccessMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val returnSuccessMap: StateFlow<Map<String, Boolean>> = _returnSuccessMap

    private val _cancelSuccessMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val cancelSuccessMap: StateFlow<Map<String, Boolean>> = _cancelSuccessMap

    fun cancelBooking(bookingId: String, roomId: String) {
        val bookingRef = db.collection("booking_requests").document(bookingId)
        val roomRef = db.collection("rooms").document(roomId)

        bookingRef.update("status", "cancelled")
            .addOnSuccessListener {
                roomRef.update("isAvailable", true)
                    .addOnSuccessListener {
                        _cancelSuccessMap.value = _cancelSuccessMap.value.toMutableMap().apply {
                            put(bookingId, true)
                        }
                    }
            }
    }

    fun returnRoom(bookingId: String, roomId: String) {
        val bookingRef = db.collection("booking_requests").document(bookingId)
        val roomRef = db.collection("rooms").document(roomId)

        bookingRef.update("status", "returned")
            .addOnSuccessListener {
                roomRef.update("isAvailable", true)
                    .addOnSuccessListener {
                        _returnSuccessMap.value = _returnSuccessMap.value.toMutableMap().apply {
                            put(bookingId, true)
                        }
                    }
            }
    }


    fun getBookingRequestById(bookingId: String, onResult: (BookingRequest?) -> Unit) {
        val bookingRef = db.collection("bookingRequests").document(bookingId)
        bookingRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val booking = document.toObject(BookingRequest::class.java)
                    onResult(booking)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getLandlordInfoByBookingRequest(
        bookingRequestId: String,
        onResult: (User?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("booking_requests").document(bookingRequestId)
            .get()
            .addOnSuccessListener { bookingSnap ->
                val landlordId = bookingSnap.getString("landlordId")
                if (landlordId != null) {
                    db.collection("users").document(landlordId)
                        .get()
                        .addOnSuccessListener { userSnap ->
                            val info = User(
                                username = userSnap.getString("fullName") ?: "N/A",
                                landlordBankName = userSnap.getString("bankName") ?: "N/A",
                                landlordBankAccount = userSnap.getString("bankAccount") ?: "N/A"
                            )
                            onResult(info)
                        }
                        .addOnFailureListener {
                            Log.e("QRCodeScreen", "Lỗi truy vấn users", it)
                            onResult(null)
                        }
                } else {
                    Log.e("QRCodeScreen", "Không tìm thấy landlordId trong booking")
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Log.e("QRCodeScreen", "Lỗi truy vấn booking_requests", it)
                onResult(null)
            }
    }

//    // tìm kiếm
//    fun fetchRoomsNearby(
//        latitude: Double,
//        longitude: Double,
//        onResult: (List<Room>) -> Unit,
//        onError: (Exception) -> Unit
//    ) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("rooms")
//            .get()
//            .addOnSuccessListener { result ->
//                val nearbyRooms = result.documents.mapNotNull { it.toObject(Room::class.java) }
//                    .filter {
//                        calculateDistance(latitude, longitude, it.latitude, it.longitude) <= 5.0
//                    }
//                onResult(nearbyRooms)
//            }
//            .addOnFailureListener { exception ->
//                onError(exception)
//            }
//    }

}

