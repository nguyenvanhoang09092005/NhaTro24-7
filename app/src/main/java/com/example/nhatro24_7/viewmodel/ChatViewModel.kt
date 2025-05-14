package com.example.nhatro24_7.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nhatro24_7.data.model.ChatItem
import com.example.nhatro24_7.data.model.Message
import com.example.nhatro24_7.data.model.MessageType
import com.example.nhatro24_7.data.repository.ChatRepository
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.jar.Manifest
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> get() = _messages

    fun loadMessages(
        chatId: String,
        currentUserId: String,
        onNewIncomingMessage: (Message) -> Unit
    ) {
        chatRepository.getMessages(chatId).observeForever { messageList ->
            val previousLast = _messages.value?.lastOrNull()
            val newLast = messageList.lastOrNull()

            // Nếu là tin mới và không phải của chính mình
            if (
                newLast != null &&
                newLast.senderId != currentUserId &&
                newLast != previousLast
            ) {
                onNewIncomingMessage(newLast)
            }

            _messages.value = messageList
        }
    }



    fun sendMessage(chatId: String, message: Message) {
        chatRepository.sendMessage(chatId, message)
    }

    private val _userChats = MutableLiveData<List<Message>>()
    val userChats: LiveData<List<Message>> = _userChats

    fun loadUserChats(currentUserId: String) {
        chatRepository.getUserChats(currentUserId).observeForever {
            _userChats.value = it
        }
    }

    val chatList = MutableLiveData<List<ChatItem>>()

    fun loadChatList(currentUserId: String) {
        chatRepository.getChatListForUser(currentUserId) {
            chatList.postValue(it)
        }
    }

    fun getFileName(context: Context, uri: Uri): String {
        return chatRepository.getFileNameFromUri(context, uri)
    }

    //gửi vị trí
    fun sendLocationMessage(
        context: Context,
        senderId: String,
        receiverId: String,
        chatId: String
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Bạn chưa cấp quyền vị trí", Toast.LENGTH_SHORT).show()
            return
        }

        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val mapsUrl = "https://maps.google.com/?q=$latitude,$longitude"

                // Nếu không dùng API key, có thể để imageUrl = null
                val staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap" +
                        "?center=$latitude,$longitude&zoom=15&size=600x300" +
                        "&markers=color:red%7C$latitude,$longitude&key=YOUR_API_KEY"

                val message = Message(
                    senderId = senderId,
                    receiverId = receiverId,
                    content = "📍 Vị trí hiện tại:\n$mapsUrl",
                    imageUrl = staticMapUrl,
                    location = GeoPoint(latitude, longitude),
                    type = MessageType.LOCATION,
                    timestamp = System.currentTimeMillis()
                )

                chatRepository.sendMessage(chatId, message)

            } else {
                Toast.makeText(context, "Không thể lấy vị trí", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Lỗi khi lấy vị trí", Toast.LENGTH_SHORT).show()
        }
    }

}

