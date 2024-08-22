package com.example.orbitx.model

data class Post(
    val text: String,
    val imageUrl: String,
    val owneruid: String,
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val comments: List<Comment>,
    val likes: List<Like>,
    val location: Location? = null
)

data class Comment(
    val text: String,
    val owneruid: String,
    val timestamp: Long
)

data class Like(
    val owneruid: String,
    val timestamp: Long
)

data class Location(
    val latitude: Double,
    val longitude: Double
)