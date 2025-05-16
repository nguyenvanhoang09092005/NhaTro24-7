package com.example.nhatro24_7.data.repository

import com.example.nhatro24_7.data.model.Statistic
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StatisticRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getStatisticByLandlord(landlordId: String): Statistic {
        try {
            // Lấy tất cả yêu cầu đặt phòng
            val bookingsSnapshot = db.collection("booking_requests")
                .whereEqualTo("landlordId", landlordId)
                .get()
                .await()

            val bookings = bookingsSnapshot.documents
            val totalBookings = bookings.count { it.getString("status") == "accepted" }
            val totalCancellations = bookings.count { it.getString("status") == "cancelled" }
            val totalCheckouts = bookings.count { it.getString("status") == "returned" }

            // Lấy các phòng tương ứng với các yêu cầu đặt phòng
            val roomIds = bookings.mapNotNull { it.getString("roomId") }.distinct()
            val roomsSnapshot = db.collection("rooms")
                .whereIn("id", roomIds.take(10))
                .get()
                .await()

            val roomMap = roomsSnapshot.documents.associate {
                it.id to (it.getLong("price") ?: 0L)
            }

            // Doanh thu từ phòng đã trả lại
            var revenueFromReturnedRooms = 0L
            bookings.filter { it.getString("status") == "returned" }.forEach {
                val roomId = it.getString("roomId")
                revenueFromReturnedRooms += roomMap[roomId] ?: 0L
            }

            // Doanh thu từ thanh toán
            val paymentsSnapshot = db.collection("payment")
                .whereEqualTo("landlordId", landlordId)
                .whereEqualTo("status", "paid")
                .get()
                .await()

            val revenueFromPayments = paymentsSnapshot.documents.sumOf {
                it.getDouble("amount")?.toLong() ?: 0L
            }

            val totalRevenue = revenueFromReturnedRooms + revenueFromPayments

            // Doanh thu theo tháng
            val revenueByMonth = mutableMapOf<String, Long>()
            paymentsSnapshot.documents.forEach { doc ->
                val month = doc.getString("month") ?: ""
                val amount = doc.getDouble("amount")?.toLong() ?: 0L
                revenueByMonth[month] = (revenueByMonth[month] ?: 0L) + amount
            }

            // Lượt xem theo ngày
            val logsSnapshot = db.collection("activity_logs")
                .whereEqualTo("action", "Xem phòng")
                .get()
                .await()

            val viewsByDay = mutableMapOf<String, Int>()
            logsSnapshot.documents.forEach { log ->
                val date = log.getString("date") ?: ""
                viewsByDay[date] = (viewsByDay[date] ?: 0) + 1
            }

            // Đánh giá trung bình của các phòng
            val reviewsSnapshot = db.collection("reviews")
                .whereIn("roomId", roomIds.take(10))
                .get()
                .await()

            val ratings = reviewsSnapshot.documents.mapNotNull {
                it.getDouble("rating")?.toFloat()
            }

            val averageRating = if (ratings.isNotEmpty()) ratings.average().toFloat() else 0f

            // Trả về đối tượng Statistic đầy đủ
            return Statistic(
                revenue = totalRevenue.toDouble(),
                totalBookings = totalBookings,
                totalCancellations = totalCancellations,
                totalCheckouts = totalCheckouts,
                totalViews = logsSnapshot.size(),
                averageRating = averageRating,
                paidRoomCount = revenueFromPayments,
                revenueByMonth = revenueByMonth,
                viewsByDay = viewsByDay
            )
        } catch (e: Exception) {
            throw Exception("Lỗi khi lấy thống kê: ${e.message}", e)
        }
    }
}
