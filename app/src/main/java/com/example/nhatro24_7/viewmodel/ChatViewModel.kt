package com.example.nhatro24_7.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nhatro24_7.data.model.ChatItem
import com.example.nhatro24_7.data.model.Message
import com.example.nhatro24_7.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> get() = _messages

    fun loadMessages(chatId: String) {
        chatRepository.getMessages(chatId).observeForever {
            _messages.value = it
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
}

