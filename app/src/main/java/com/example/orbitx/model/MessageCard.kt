package com.example.orbitx.ChatRepository

data class MessageCard(
    val message: String = "",
    val senderId: String = "",
    var lasttime: String = "",
    val imageUrl: String = ""
)
