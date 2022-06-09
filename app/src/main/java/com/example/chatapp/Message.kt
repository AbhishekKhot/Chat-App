package com.example.chatapp

data class Message(
    val message: String? = null,
    val senderId: String? = null,
    val timeStamp: Long = 0,
    val currentTime: String? = null,
)