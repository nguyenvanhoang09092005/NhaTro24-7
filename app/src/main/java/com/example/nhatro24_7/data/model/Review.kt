package com.example.nhatro24_7.data.model

data class Review(
    val id: String = "",
    val userId: String,
    val roomId: String,
    val rating: Float,
    val comment: String,
    val submittedAt: Long = System.currentTimeMillis()
)
