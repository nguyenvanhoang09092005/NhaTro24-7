package com.example.nhatro24_7.data.repository

import android.content.Context
import android.net.Uri
import com.example.nhatro24_7.data.model.Message
import com.example.nhatro24_7.data.model.ChatItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val chatsCollection = firestore.collection("chats")

    fun sendMessage(chatId: String, message: Message) {
        val chatDocRef = chatsCollection.document(chatId)

        chatDocRef.set(
            mapOf("participants" to listOf(message.senderId, message.receiverId)),
            SetOptions.merge()
        )

        chatDocRef.collection("messages")
            .add(message)
    }

    fun getMessages(chatId: String): LiveData<List<Message>> {
        val messages = MutableLiveData<List<Message>>()
        chatsCollection.document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) return@addSnapshotListener
                val messageList = snapshot?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                }
                messages.value = messageList ?: emptyList()
            }
        return messages
    }

    fun getUserChats(currentUserId: String): LiveData<List<Message>> {
        val messages = MutableLiveData<List<Message>>()
        chatsCollection
            .get()
            .addOnSuccessListener { snapshot ->
                val allMessages = mutableListOf<Message>()
                snapshot.documents.forEach { chatDoc ->
                    chatDoc.reference.collection("messages")
                        .whereIn("senderId", listOf(currentUserId))
                        .get()
                        .addOnSuccessListener { msgSnapshot ->
                            allMessages.addAll(msgSnapshot.toObjects(Message::class.java))
                            messages.value = allMessages
                        }
                    chatDoc.reference.collection("messages")
                        .whereIn("receiverId", listOf(currentUserId))
                        .get()
                        .addOnSuccessListener { msgSnapshot ->
                            allMessages.addAll(msgSnapshot.toObjects(Message::class.java))
                            messages.value = allMessages
                        }
                }
            }
        return messages
    }

    fun getChatListForUser(currentUserId: String, callback: (List<ChatItem>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val chatsCollection = db.collection("chats")
        chatsCollection.get().addOnSuccessListener { chatDocs ->
            val chatItems = mutableListOf<ChatItem>()
            val totalChats = chatDocs.size()
            var processedChats = 0

            if (totalChats == 0) {
                callback(emptyList())
                return@addOnSuccessListener
            }

            fun checkAndCallCallback() {
                if (processedChats == totalChats) {
                    callback(chatItems.sortedByDescending { it.timestamp })
                }
            }

            for (doc in chatDocs) {
                val chatId = doc.id
                if (!chatId.contains(currentUserId)) {
                    processedChats++
                    checkAndCallCallback()
                    continue
                }

                val otherUserId = chatId.split("-").firstOrNull { it != currentUserId }
                if (otherUserId == null) {
                    processedChats++
                    checkAndCallCallback()
                    continue
                }


                doc.reference.collection("messages")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { messageSnapshot ->
                        val lastMsg = messageSnapshot.documents.firstOrNull()?.toObject(Message::class.java)
                        if (lastMsg != null) {
                            db.collection("users").document(otherUserId).get()
                                .addOnSuccessListener { userSnap ->
                                    val username = userSnap.getString("username") ?: ""
                                    val avatar = userSnap.getString("avatarUrl") ?: ""

                                    chatItems.add(
                                        ChatItem(
                                            chatId = chatId,
                                            otherUserId = otherUserId,
                                            otherUsername = username,
                                            otherAvatarUrl = avatar,
                                            lastMessage = lastMsg.content,

                                            timestamp = lastMsg.timestamp
                                        )
                                    )
                                    processedChats++
                                    checkAndCallCallback()
                                }
                                .addOnFailureListener {
                                    processedChats++
                                    checkAndCallCallback()
                                }
                        } else {
                            processedChats++
                            checkAndCallCallback()
                        }
                    }
                    .addOnFailureListener {
                        processedChats++
                        checkAndCallCallback()
                    }
            }
        }
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var name = "Tập tin"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }
}