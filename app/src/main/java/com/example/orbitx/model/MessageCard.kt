package com.example.chatbyme2.model

data class MessageCard(
    val message: String = "",
    val senderId: String = "",
    var lasttime: String = "",
    val imageUrl: String = ""
)
