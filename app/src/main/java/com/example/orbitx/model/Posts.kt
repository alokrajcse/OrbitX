package com.example.orbitx.model

data class Posts(
    val owneruid: String = "",
    val imageUrl: String = "",
    val text: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val timestamp: Long = 0L,
    var postId: String = ""
)


