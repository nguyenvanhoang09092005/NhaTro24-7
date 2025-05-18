package com.example.nhatro24_7.data.repository

import com.example.nhatro24_7.data.model.Room
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoomRepository {

    private val db = FirebaseFirestore.getInstance()
    private val bookingRequests = db.collection("booking_requests")
    private val rooms = db.collection("rooms")

    suspend fun sendBookingRequest(roomId: String, userId: String, landlordId: String): Boolean {
        return try {
            val booking = hashMapOf(
                "roomId" to roomId,
                "userId" to userId,
                "landlordId" to landlordId,
                "status" to "pending",
                "timestamp" to System.currentTimeMillis()
            )
            bookingRequests.add(booking).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun hasPendingBookingRequest(userId: String, roomId: String): Boolean {
        return try {
            val result = bookingRequests
                .whereEqualTo("userId", userId)
                .whereEqualTo("roomId", roomId)
                .whereEqualTo("status", "pending")
                .get()
                .await()
            !result.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateBookingStatus(requestId: String, status: String): Boolean {
        return try {
            bookingRequests.document(requestId).update("status", status).await()

            // Nếu chấp nhận thì cập nhật isAvailable = false cho phòng
            if (status == "accepted") {
                val doc = bookingRequests.document(requestId).get().await()
                val roomId = doc.getString("roomId") ?: return false
                rooms.document(roomId).update("isAvailable", false).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun returnRoom(roomId: String): Boolean {
        return try {
            rooms.document(roomId).update("isAvailable", true).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun cancelBookingRequest(requestId: String): Boolean {
        return try {
            bookingRequests.document(requestId).update("status", "cancelled").await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateBookingStatusToPaid(
        bookingId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings")
            .document(bookingId)
            .update("status", "paid")
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
