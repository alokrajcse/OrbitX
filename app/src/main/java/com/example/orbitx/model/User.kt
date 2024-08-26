package com.example.orbitx.ChatRepository

data class User(
    val email: String="",
    val username: String="",
    val userId: String="",
    val isFollowing: Boolean=false,
    val profilepictureurl: String="https://wallpapers.com/images/featured-full/link-pictures-16mi3e7v5hxno9c4.jpg"
)
