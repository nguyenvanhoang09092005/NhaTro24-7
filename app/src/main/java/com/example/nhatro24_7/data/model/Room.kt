package com.example.nhatro24_7.data.model

data class Room(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val area: Double = 0.0,
    val roomType: String = "",
    val roomCategory: String = "",
    val amenities: List<String> = emptyList(),
    val mainImage: String = "",
    val images: List<String> = emptyList(),
    val viewCount: Int = 0,
    val isAvailable: Boolean = true,
    val owner_id: String = "",

    // Thông tin chủ nhà (dùng cho thanh toán)
    val landlordName: String = "",
    val landlordBankAccount: String = "",
    val landlordBankName: String = "",
    val created_at: Long = 0L

)
