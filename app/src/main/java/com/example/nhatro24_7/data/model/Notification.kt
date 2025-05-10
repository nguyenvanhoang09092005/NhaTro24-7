package com.example.nhatro24_7.data.model

data class Notification(
    val id: String = "",
    val userId: String = "",           // ID của người nhận thông báo
    val title: String = "",            // Tiêu đề thông báo (thêm mới)
    val message: String = "",          // Nội dung thông báo
    val type: NotificationType = NotificationType.GENERAL, // Loại thông báo (thêm mới)
    val isRead: Boolean = false,       // Đã đọc hay chưa (thêm mới)
    val imageUrl: String? = null,      // Link ảnh kèm (nếu có, thêm mới)
    val timestamp: Long = System.currentTimeMillis() // Thời gian tạo, mặc định current time
)
enum class NotificationType {
    GENERAL,      // Thông báo chung
    BOOKING,      // Đặt phòng, đặt cọc
    PAYMENT,      // Thanh toán
    PROMOTION,    // Khuyến mãi, giảm giá
    SYSTEM        // Hệ thống, bảo trì, cập nhật app
}