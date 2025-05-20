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

            val paymentsSnapshot = db.collection("payment")
                .whereEqualTo("landlordId", landlordId)
                .whereEqualTo("status", "paid")
                .get()
                .await()

            val revenueFromPayments = paymentsSnapshot.documents.sumOf {
                it.getDouble("amount")?.toLong() ?: 0L
            }

            val totalRevenue = revenueFromReturnedRooms + revenueFromPayments

            // Khởi tạo map số lượng theo tháng
            val bookingsByMonth = mutableMapOf<String, Int>()
            val checkoutsByMonth = mutableMapOf<String, Int>()
            val cancellationsByMonth = mutableMapOf<String, Int>()
            val paidRoomsByMonth = mutableMapOf<String, Int>()

            // Đếm số lượng đặt, trả, huỷ theo tháng trong booking_requests
            bookings.forEach { doc ->
                val month = doc.getString("month") ?: return@forEach
                when (doc.getString("status")) {
                    "accepted" -> bookingsByMonth[month] = (bookingsByMonth[month] ?: 0) + 1
                    "returned" -> checkoutsByMonth[month] = (checkoutsByMonth[month] ?: 0) + 1
                    "cancelled" -> cancellationsByMonth[month] = (cancellationsByMonth[month] ?: 0) + 1
                }
            }

            // Đếm số lượng thanh toán theo tháng trong payment
            paymentsSnapshot.documents.forEach { doc ->
                val month = doc.getString("month") ?: return@forEach
                paidRoomsByMonth[month] = (paidRoomsByMonth[month] ?: 0) + 1
            }

            // Lấy logs xem phòng có action = "Xem phòng"
            val logsSnapshot = db.collection("activity_logs")
                .whereEqualTo("action", "Xem phòng")
                .get()
                .await()

            val viewsByDay = mutableMapOf<String, Int>()

            logsSnapshot.documents.filter { doc ->
                val roomId = doc.getString("roomId")
                roomId != null && roomIds.contains(roomId)
            }.forEach { log ->
                val date = log.getString("date") ?: ""
                viewsByDay[date] = (viewsByDay[date] ?: 0) + 1
            }

            val filteredViewsCount = logsSnapshot.documents.count { doc ->
                val roomId = doc.getString("roomId")
                roomId != null && roomIds.contains(roomId)
            }

            // Lấy đánh giá phòng
            val reviewsSnapshot = db.collection("reviews")
                .whereIn("roomId", roomIds.take(10))
                .get()
                .await()

            val ratings = reviewsSnapshot.documents.mapNotNull {
                it.getDouble("rating")?.toFloat()
            }

            val averageRating = if (ratings.isNotEmpty()) ratings.average().toFloat() else 0f

            return Statistic(
                revenue = totalRevenue.toDouble(),
                totalBookings = totalBookings,
                totalCancellations = totalCancellations,
                totalCheckouts = totalCheckouts,
                totalViews = filteredViewsCount,
                averageRating = averageRating,
                paidRoomCount = revenueFromPayments,
                viewsByDay = viewsByDay,
                bookingsByMonth = bookingsByMonth,
                checkoutsByMonth = checkoutsByMonth,
                cancellationsByMonth = cancellationsByMonth,
                paidRoomsByMonth = paidRoomsByMonth
            )
        } catch (e: Exception) {
            throw Exception("Lỗi khi lấy thống kê: ${e.message}", e)
        }
    }
}
