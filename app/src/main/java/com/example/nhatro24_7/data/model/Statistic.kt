package com.example.nhatro24_7.data.model

data class Statistic(
    val revenue: Double = 0.0,
    val totalBookings: Int = 0,

    val successfulBookings: Int = 0,
    val successfulBookingRevenue: Double = 0.0,
    val totalCancellations: Int = 0,
    val totalViews: Int = 0,
    val totalCheckouts: Int = 0,
    val averageRating: Float = 0f,
    val paidRoomCount: Int = 0,
    val topViewedRooms: List<Pair<String, Int>> = emptyList()

)
