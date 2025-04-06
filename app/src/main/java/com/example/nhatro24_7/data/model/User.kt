package com.example.nhatro24_7.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val role: String = "customer",
    val fullName: String = "",
    val birthDate: String = "",
    val hometown: String = "",
    val currentAddress: String = "",
    val phone: String = "",
    val avatarUrl: String = ""
) {
    fun isAdmin() = role == "admin"
    fun isLandlord() = role == "landlord"
}
