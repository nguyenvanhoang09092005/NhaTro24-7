package com.example.nhatro24_7.data.model

data class Room(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val location: String = "",
    val area: Double = 0.0,
    val roomType: String = "",
    val roomCategory: String = "",
    val amenities: List<String> = emptyList(),
    val mainImage: String = "",
    val images: List<String> = emptyList(),
    val owner_id: Int = 0,
    val created_at: Long = 0L
)
