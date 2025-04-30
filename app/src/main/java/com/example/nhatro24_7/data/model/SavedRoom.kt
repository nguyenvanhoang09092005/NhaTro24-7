package com.example.nhatro24_7.data.model

data class SavedRoom(
    val userId: String,
    val roomId: String,
    val savedAt: Long = System.currentTimeMillis()
)
