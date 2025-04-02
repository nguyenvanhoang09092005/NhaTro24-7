package com.example.nhatro24_7.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val role: String = "customer"
) {
    fun isAdmin() = role == "admin"
    fun isLandlord() = role == "landlord"
}
