package com.example.orbitx.model

data class Posts(
    val imageUrl: String = "",
    val text: String = "",
    val commentsCount: Int = 0,
    val likesCount: Int = 0,
    val owneruid: String = "",
    val timestamp: Long = 0L,
    var username: String = "",
    var profilepictureurl: String = "",

)
data class User(
    val username: String = "",
    val profilepictureurl: String = ""
)