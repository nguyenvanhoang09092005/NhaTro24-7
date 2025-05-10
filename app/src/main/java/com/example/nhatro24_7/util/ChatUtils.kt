package com.example.nhatro24_7.util

fun generateChatId(userId1: String, userId2: String): String {
    return if (userId1 < userId2) "$userId1-$userId2" else "$userId2-$userId1"
}