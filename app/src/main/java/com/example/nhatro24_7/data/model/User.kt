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
    val avatarUrl: String = "",
    val landlordName: String = "",
    val landlordIdNumber: String = "",  // CMND/CCCD
    val landlordBankAccount: String = "",
    val landlordBankName: String = "",
    val landlordZalo: String = "",
    val chatIds: List<String> = emptyList()
)