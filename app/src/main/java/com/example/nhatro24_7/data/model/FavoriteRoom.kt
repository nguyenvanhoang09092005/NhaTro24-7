package com.example.nhatro24_7.data.model

//Lịch sử yêu thích
data class FavoriteRoom(
    val userId: String,
    val roomId: String,
    val addedAt: Long = System.currentTimeMillis()
)
