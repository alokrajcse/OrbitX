package com.example.orbitx.ChatRepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orbitx.Views.ChatCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

class ChatViewModel : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    private val _chatcardlist = MutableLiveData<List<ChatCard>>()
    val users: LiveData<List<User>> get() = _users
    val chatcardlist: LiveData<List<ChatCard>> get() = _chatcardlist

    init {
        _chatcardlist.value = mutableListOf()
        fetchUsers()
    }


    private fun updateOrAddChatCard(chatCard: ChatCard) {
        val updatedList = _chatcardlist.value.orEmpty().toMutableList()
        val existingCardIndex = updatedList.indexOfFirst { it.userId == chatCard.userId }

        if (existingCardIndex != -1) {

            updatedList[existingCardIndex] = chatCard
        } else {

            updatedList.add(chatCard)
        }

        val sortedList = updatedList.sortedByDescending { it.lastTime }
        _chatcardlist.value = sortedList
        println("ChatCard List Updated: $sortedList")
    }


    private fun fetchUsers() {
        ChatRepository.getUsers().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (childSnapshot in snapshot.children) {
                    val email = childSnapshot.child("email").getValue(String::class.java) ?: ""
                    val userId = childSnapshot.child("userId").getValue(String::class.java) ?: ""
                    val username = childSnapshot.child("username").getValue(String::class.java) ?: ""
                    val onlinestatus = childSnapshot.child("online").getValue(Boolean::class.java) ?: false


                    val roomId = getChatRoomId(userId, FirebaseAuth.getInstance().currentUser?.uid.toString())
                    getLastTime(roomId).observeForever { lastTimeString ->
                        val lastTime = lastTimeString?.toLongOrNull() ?: 0L
                        fetchProfileurl(userId) { profilePictureUrl ->
                            val chatCard = ChatCard(
                                userId = userId,
                                username = username,
                                profilePictureUrl = profilePictureUrl,
                                onlinestatus = onlinestatus,
                                lastTime = lastTime
                            )
                            updateOrAddChatCard(chatCard)
                        }
                    }

                    userList.add(User(email, username, userId))
                }
                _users.value = userList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    fun getLastTime(roomId: String): LiveData<String> = ChatRepository.getLastTime(roomId)


    fun fetchProfileurl(data: String, onBioReceived: (String) -> Unit) {
        Firebase.database.getReference("users").child(data).child("profilepictureurl")
            .get().addOnSuccessListener { snapshot ->
                val bio = snapshot.getValue(String::class.java) ?: "https://wallpapers.com/images/featured-full/link-pictures-16mi3e7v5hxno9c4.jpg"
                onBioReceived(bio)
            }.addOnFailureListener {
                Log.e("Firebase", "Failed to fetch bio", it)
            }
    }
}
