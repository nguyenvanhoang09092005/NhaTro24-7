package com.example.nhatro24_7.data.repository

import com.example.nhatro24_7.data.model.Statistic
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StatisticRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getStatisticByLandlord(landlordId: String): Statistic {
        try {
            val bookingsSnapshot = db.collection("booking_requests")
                .whereEqualTo("landlordId", landlordId)
                .get()
                .await()

            val bookings = bookingsSnapshot.documents
            val totalBookings = bookings.count { it.getString("status") == "accepted" }
            val totalCancellations = bookings.count { it.getString("status") == "cancelled" }
            val totalCheckouts = bookings.count { it.getString("status") == "returned" }

            val roomIds = bookings.mapNotNull { it.getString("roomId") }.distinct()
            val roomsSnapshot = db.collection("rooms")
                .whereIn("id", roomIds.take(10))
                .get()
                .await()

            val roomMap = roomsSnapshot.documents.associate {
                it.id to (it.getLong("price") ?: 0L)
            }

            var revenueFromReturnedRooms = 0L
            bookings.filter { it.getString("status") == "returned" }.forEach {
                val roomId = it.getString("roomId")
                revenueFromReturnedRooms += roomMap[roomId] ?: 0L
            }

            // 🔹 Lấy thêm doanh thu từ các khoản thanh toán đã hoàn tất
            val paymentsSnapshot = db.collection("payment")
                .whereEqualTo("landlordId", landlordId)
                .whereEqualTo("status", "paid")
                .get()
                .await()

            val revenueFromPayments = paymentsSnapshot.documents.sumOf {
                it.getDouble("amount")?.toLong() ?: 0L
            }

            val totalRevenue = revenueFromReturnedRooms + revenueFromPayments

            val logsSnapshot = db.collection("activity_logs")
                .whereEqualTo("action", "Xem phòng")
                .get()
                .await()

            val totalViews = logsSnapshot.documents.count {
                roomIds.contains(it.getString("roomId"))
            }

            val reviewsSnapshot = db.collection("reviews")
                .whereIn("roomId", roomIds.take(10))
                .get()
                .await()

            val ratings = reviewsSnapshot.documents.mapNotNull {
                it.getDouble("rating")?.toFloat()
            }

            val averageRating = if (ratings.isNotEmpty()) ratings.average().toFloat() else 0f


            return Statistic(
                revenue = totalRevenue.toDouble(), // chuyển về Double nếu model dùng Double
                totalBookings = totalBookings,
                totalCancellations = totalCancellations,
                totalCheckouts = totalCheckouts,
                totalViews = totalViews,
                averageRating = averageRating
            )
        } catch (e: Exception) {
            throw Exception("Lỗi khi lấy thống kê: ${e.message}", e)
        }
    }

}
