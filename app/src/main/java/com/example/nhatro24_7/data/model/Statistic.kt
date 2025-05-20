//package com.example.nhatro24_7.data.model
//
//data class Statistic(
//    val revenue: Double = 0.0,
//    val totalBookings: Int = 0,
////    val revenueByMonth: Map<String, Int>,
////    val viewsByDay: Map<String, Int>,
//    val successfulBookings: Int = 0,
//    val successfulBookingRevenue: Double = 0.0,
//    val totalCancellations: Int = 0,
//    val totalViews: Int = 0,
//    val totalCheckouts: Int = 0,
//    val averageRating: Float = 0f,
////    val paidRoomCount: Int = 0,
//    val topViewedRooms: List<Pair<String, Int>> = emptyList(),
//    val paidRoomCount: Long,
//    val revenueByMonth: Map<String, Long>,
//    val viewsByDay: Map<String, Int>,
//)
package com.example.nhatro24_7.data.model

data class Statistic(
    val revenue: Double = 0.0,
    val totalBookings: Int = 0,
    val totalCancellations: Int = 0,
    val totalCheckouts: Int = 0,
    val totalViews: Int = 0,
    val averageRating: Float = 0f,
    val paidRoomCount: Long = 0L,
    val revenueByMonth: Map<String, Long> = emptyMap(),
    val viewsByDay: Map<String, Int> = emptyMap(),

    val bookingsByMonth: Map<String, Int> = emptyMap(),
    val checkoutsByMonth: Map<String, Int> = emptyMap(),
    val cancellationsByMonth: Map<String, Int> = emptyMap(),
    val paidRoomsByMonth: Map<String, Int> = emptyMap(),
    val totalRooms: Int = 0
)
