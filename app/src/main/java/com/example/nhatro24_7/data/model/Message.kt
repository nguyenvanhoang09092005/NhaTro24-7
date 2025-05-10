package com.example.nhatro24_7.data.model

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val senderName: String? = null,
    val senderAvatarUrl: String? = null,
//    val messageId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)