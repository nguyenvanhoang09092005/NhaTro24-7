package com.example.nhatro24_7.data.model

data class Payment(
    val id: String = "",
    val roomId: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending",
    val landlordId: String = "",
    val landlordBankAccount: String = "",
    val landlordBankName: String = "",
    val qrCodeContent: String? = null
)
{
    fun generateQRCode(): String {
        return """
            STK: $landlordBankAccount
            Ngân hàng: $landlordBankName
            Chủ TK: $landlordId
            Số tiền: $amount
            Phương thức: $paymentMethod
            Thời gian: $timestamp
        """.trimIndent()
    }
}