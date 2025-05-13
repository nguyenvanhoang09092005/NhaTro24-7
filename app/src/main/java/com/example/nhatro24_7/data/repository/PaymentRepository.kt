package com.example.nhatro24_7.data.repository

import com.example.nhatro24_7.data.model.Bill
import com.example.nhatro24_7.data.model.Payment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class PaymentRepository {

    private val db = Firebase.firestore.collection("payments")

    fun makePayment(payment: Payment, onResult: (Boolean) -> Unit) {
        val newDoc = db.document()
        val paymentWithId = payment.copy(id = newDoc.id)

        newDoc.set(paymentWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun makePaymentWithBill(payment: Payment, onResult: (Boolean) -> Unit) {
        val paymentRef = Firebase.firestore.collection("payments").document()
        val paymentWithId = payment.copy(id = paymentRef.id)

        paymentRef.set(paymentWithId).addOnSuccessListener {
            val bill = Bill(
                id = Firebase.firestore.collection("bills").document().id,
                paymentId = paymentRef.id,
                userId = payment.userId,
                roomId = payment.roomId,
                amount = payment.amount
            )
            Firebase.firestore.collection("bills").document(bill.id).set(bill)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }.addOnFailureListener { onResult(false) }
    }


    fun getPaymentsByUser(userId: String, onResult: (List<Payment>) -> Unit) {
        db.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val payments = result.toObjects(Payment::class.java)
                onResult(payments)
            }
    }

    fun updatePaymentStatus(paymentId: String, status: String, onResult: (Boolean) -> Unit) {
        db.document(paymentId)
            .update("status", status)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getPaymentsByRoom(roomId: String, onResult: (List<Payment>) -> Unit) {
        db.whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                val payments = result.toObjects(Payment::class.java)
                onResult(payments)
            }
    }

}
