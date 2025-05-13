package com.example.nhatro24_7.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nhatro24_7.data.model.Payment
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.repository.PaymentRepository
import com.example.nhatro24_7.util.QRCodeGenerator

class PaymentViewModel(private val repository: PaymentRepository = PaymentRepository()) : ViewModel() {

    var selectedMethod by mutableStateOf("Chuyển khoản")
    var isProcessing by mutableStateOf(false)
    var paymentSuccess by mutableStateOf(false)
    var paymentStatus = "Pending"

    // Hàm tạo mã QR từ thông tin thanh toán
    fun createQRCode(payment: Payment): Bitmap {
        val qrCodeContent = payment.generateQRCode()
        return QRCodeGenerator.generate(qrCodeContent)
    }


    // Giả sử chúng ta thực hiện thanh toán qua phương thức này
    fun makePayment(payment: Payment) {
        // Cập nhật trạng thái thanh toán
        paymentStatus = "Completed"
        // Bạn có thể thực hiện các logic thanh toán thực tế ở đây (gửi yêu cầu API, cập nhật Firebase...)
    }

    fun makePayment(roomId: String, userId: String, amount: Double) {
        isProcessing = true
        val payment = Payment(
            roomId = roomId,
            userId = userId,
            amount = amount,
            paymentMethod = selectedMethod
        )

        repository.makePayment(payment) { success ->
            isProcessing = false
            paymentSuccess = success
        }
    }

    fun updatePaymentStatus(paymentId: String, status: String, onResult: (Boolean) -> Unit) {
        isProcessing = true
        repository.updatePaymentStatus(paymentId, status) { success ->
            isProcessing = false
            paymentSuccess = success
            onResult(success)
        }
    }

    fun makePaymentWithAutoInfo(
        room: Room,
        userId: String,
    ) {
        isProcessing = true

        val payment = Payment(
            roomId = room.id,
            userId = userId,
            amount = room.price,
            paymentMethod = selectedMethod,
//            landlordId = room.landlordId,
            landlordBankAccount = room.landlordBankAccount,
            landlordBankName = room.landlordBankName
        )

        repository.makePaymentWithBill(payment) { success ->
            isProcessing = false
            paymentSuccess = success
        }
    }


}
