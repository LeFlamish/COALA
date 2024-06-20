package com.example.smobileeapp

import com.example.smobileeapp.data.Chat

data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
)