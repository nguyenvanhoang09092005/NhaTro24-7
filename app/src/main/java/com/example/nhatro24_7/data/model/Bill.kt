package com.example.nhatro24_7.data.model

data class Bill(
    val id: String = "",
    val paymentId: String = "",
    val userId: String = "",
    val roomId: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Đã thanh toán"
)
