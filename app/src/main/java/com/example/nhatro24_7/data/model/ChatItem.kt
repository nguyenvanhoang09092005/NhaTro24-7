package com.example.nhatro24_7.data.model

data class ChatItem(
    val chatId: String,
    val otherUserId: String,
    val otherUsername: String,
    val otherAvatarUrl: String,
    val lastMessage: String,
    val timestamp: Long
)
