package com.example.nhatro24_7.data.repository

import com.example.nhatro24_7.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StatisticRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getStatisticByLandlord(landlordId: String): Statistic {
        val payments = db.collection("payments")
            .whereEqualTo("landlordId", landlordId)
            .whereEqualTo("status", "Completed")
            .get().await()

        val totalRevenue = payments.sumOf { it.getDouble("amount") ?: 0.0 }

        val bookings = db.collection("booking_requests")
            .whereEqualTo("landlordId", landlordId)
            .get().await()

        val totalBookings = bookings.size()

        val cancellations = db.collection("cancel_histories")
            .whereEqualTo("landlordId", landlordId)
            .get().await()

        val totalCancellations = cancellations.size()

        val rooms = db.collection("rooms")
            .whereEqualTo("owner_id", landlordId)
            .get().await()

        var totalViews = 0
        for (doc in rooms) {
            totalViews += doc.getLong("viewCount")?.toInt() ?: 0
        }

        val reviews = db.collection("reviews")
            .whereEqualTo("landlordId", landlordId)
            .get().await()

        val totalRatings = reviews.mapNotNull { it.getDouble("rating")?.toFloat() }
        val avgRating = if (totalRatings.isNotEmpty()) {
            totalRatings.average().toFloat()
        } else 0f

        return Statistic(
            revenue = totalRevenue,
            totalBookings = totalBookings,
            totalCancellations = totalCancellations,
            totalViews = totalViews,
            averageRating = avgRating
        )
    }
}
