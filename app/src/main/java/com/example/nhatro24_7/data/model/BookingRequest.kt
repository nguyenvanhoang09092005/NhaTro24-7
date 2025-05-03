package com.example.nhatro24_7.data.model

data class BookingRequest(
    val id: String = "",
    val roomId: String = "",
    val userId: String = "",
    val landlordId: String = "",
    val status: String = "pending",  // accepted / rejected
    val timestamp: Long = 0L
)
