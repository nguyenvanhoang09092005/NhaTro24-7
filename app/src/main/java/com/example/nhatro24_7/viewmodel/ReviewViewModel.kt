package com.example.nhatro24_7.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nhatro24_7.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore

class ReviewViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Gửi đánh giá
    fun submitReview(review: Review, onComplete: (Boolean) -> Unit) {
        db.collection("reviews")
            .document(review.id)
            .set(review)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Lấy tất cả đánh giá theo roomId
    fun getReviewsByRoomId(roomId: String, onComplete: (List<Review>) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                val reviews = result.documents.mapNotNull { it.toObject(Review::class.java) }
                onComplete(reviews)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    // Kiểm tra xem user đã đánh giá booking này chưa
    fun hasUserReviewedBooking(userId: String, bookingId: String, onResult: (Boolean) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("userId", userId)
            .whereEqualTo("bookingId", bookingId)
            .get()
            .addOnSuccessListener { result ->
                onResult(!result.isEmpty)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    // Kiểm tra xem người dùng đã từng đánh giá phòng này chưa
    fun hasUserReviewedRoom(userId: String, roomId: String, onResult: (Boolean) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("userId", userId)
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                onResult(!result.isEmpty)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }


}
