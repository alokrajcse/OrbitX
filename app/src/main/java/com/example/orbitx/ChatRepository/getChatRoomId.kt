package com.example.orbitx.ChatRepository

fun getChatRoomId(user1: String, user2: String): String {
    return if (user1 < user2) "$user1$user2" else "$user2$user1"
}
