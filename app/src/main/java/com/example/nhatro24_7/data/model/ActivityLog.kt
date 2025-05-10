package com.example.nhatro24_7.data.model

//Lịch sử đã xem
data class ActivityLog(
    val id: String = "",
    val userId: String = "",
    val action: String = "",            //  "Xem phòng"
    val roomId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
