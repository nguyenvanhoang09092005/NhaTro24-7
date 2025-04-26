package com.example.nhatro24_7.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.nhatro24_7.data.model.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class RoomViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _rooms = mutableStateListOf<Room>()
    private val db = FirebaseFirestore.getInstance()
    val rooms: List<Room> get() = _rooms

    init {
        fetchRooms()
    }

    private fun fetchRooms() {
        firestore.collection("rooms")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                _rooms.clear()
                for (document in result) {
                    val room = document.toObject(Room::class.java).copy(id = document.id)
                    _rooms.add(room)
                }
            }
            .addOnFailureListener {
                Log.e("RoomViewModel", "Lỗi khi lấy dữ liệu phòng", it)
            }
    }


    suspend fun addRoom(room: Room): Boolean {
        return try {
            val docRef = db.collection("rooms").document()
            val roomWithId = room.copy(id = docRef.id)
            docRef.set(roomWithId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
