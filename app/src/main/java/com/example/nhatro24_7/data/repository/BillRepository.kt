package com.example.nhatro24_7.data.repository

import com.example.nhatro24_7.data.model.Bill
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BillRepository {

    private val db = Firebase.firestore.collection("bills")

    fun addBill(bill: Bill, onResult: (Boolean) -> Unit) {
        val newDoc = db.document(bill.id)
        newDoc.set(bill)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getBillsByUser(userId: String, onResult: (List<Bill>) -> Unit) {
        db.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val bills = result.toObjects(Bill::class.java)
                onResult(bills)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getBillsByRoom(roomId: String, onResult: (List<Bill>) -> Unit) {
        db.whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                val bills = result.toObjects(Bill::class.java)
                onResult(bills)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun updateBillStatus(billId: String, status: String, onResult: (Boolean) -> Unit) {
        db.document(billId)
            .update("status", status)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
