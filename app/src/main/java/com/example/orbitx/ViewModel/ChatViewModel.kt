package com.example.orbitx.ChatRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatViewModel : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        ChatRepository.getUsers().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (childSnapshot in snapshot.children) {
                    val email = childSnapshot.child("email").getValue(String::class.java) ?: ""
                    val userId = childSnapshot.child("userId").getValue(String::class.java) ?: ""
                    val username = childSnapshot.child("username").getValue(String::class.java) ?: ""

                    userList.add(User(email, username, userId))
                }
                _users.value = userList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun getLastTime(roomId: String): LiveData<String> = ChatRepository.getLastTime(roomId)
}

