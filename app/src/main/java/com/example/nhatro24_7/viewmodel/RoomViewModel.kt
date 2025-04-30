package com.example.nhatro24_7.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.model.SavedRoom
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

    fun addRoom(room: Room, onResult: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("rooms").document()

        val roomWithId = room.copy(id = docRef.id)

        docRef.set(roomWithId)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    //Lưu phòng
    fun unsaveRoom(savedRoom: SavedRoom, onResult: (Boolean) -> Unit = {}) {
        val docId = "${savedRoom.userId}_${savedRoom.roomId}"
        db.collection("saved_rooms").document(docId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun saveRoom(savedRoom: SavedRoom, onResult: (Boolean) -> Unit = {}) {
        val docId = "${savedRoom.userId}_${savedRoom.roomId}"
        db.collection("saved_rooms").document(docId)
            .set(savedRoom)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getSavedRooms(userId: String, onResult: (List<Room>) -> Unit) {
        db.collection("saved_rooms")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val roomIds = result.mapNotNull { it.getString("roomId") }
                db.collection("rooms")
                    .whereIn("id", roomIds)
                    .get()
                    .addOnSuccessListener { roomResult ->
                        val rooms = roomResult.map { it.toObject(Room::class.java).copy(id = it.id) }
                        onResult(rooms)
                    }
            }
    }


}
