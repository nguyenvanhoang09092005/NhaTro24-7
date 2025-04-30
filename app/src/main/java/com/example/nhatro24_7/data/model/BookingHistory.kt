package com.example.nhatro24_7.data.model

//Lịch sử đặt phòng
data class BookingHistory(
    val id: String = "",
    val userId: String,
    val roomId: String,
    val checkInDate: Long,
    val checkOutDate: Long,
    val status: BookingStatus = BookingStatus.PENDING, // ENUM: PENDING, CONFIRMED, CANCELED
    val bookedAt: Long = System.currentTimeMillis()
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}
