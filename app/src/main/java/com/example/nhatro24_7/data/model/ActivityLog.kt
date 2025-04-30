package com.example.nhatro24_7.data.model

//Lịch sử hoạt động
data class ActivityLog(
    val id: String = "",
    val userId: String = "",
    val action: String = "",            //  "Xem phòng", "Đặt phòng", "Đánh giá"
    val roomId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
