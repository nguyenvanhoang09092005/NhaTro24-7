package com.example.nhatro24_7.data.model

//Lịch sử huỷ phòng
data class CancelHistory(
    val bookingId: String,
    val userId: String,
    val roomId: String,
    val canceledAt: Long = System.currentTimeMillis(),
    val reason: String = ""
)
