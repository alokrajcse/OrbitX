package com.example.orbitx.ChatRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ChatRepository {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val lastTimeReference = FirebaseDatabase.getInstance().getReference("LastTime")

    fun getUsers() = databaseReference

    fun getLastTime(roomId: String): LiveData<String> {
        val liveData = MutableLiveData<String>()

        lastTimeReference.child(roomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                liveData.value = snapshot.getValue(String::class.java) ?: ""
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                liveData.value = "Error"
            }
        })

        return liveData
    }
}
