package com.example.orbitx.ChatRepository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database


fun fetchFollowerCount(data: String, onCountReceived: (Int) -> Unit) {
    Firebase.database.getReference("users").child(data).child("followers")
        .get().addOnSuccessListener {
            onCountReceived(it.childrenCount.toInt())
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch follower count", it)
        }
}

fun fetchFollowingCount(data: String, onCountReceived: (Int) -> Unit) {
    Firebase.database.getReference("users").child(data).child("following")
        .get().addOnSuccessListener {
            onCountReceived(it.childrenCount.toInt())
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch following count", it)
        }
}

fun fetchBio(data: String, onBioReceived: (String) -> Unit) {
    Firebase.database.getReference("users").child(data).child("bio")
        .get().addOnSuccessListener { snapshot ->
            val bio = snapshot.getValue(String::class.java) ?: "No bio available"
            onBioReceived(bio)
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch bio", it)
        }
}

fun fetchProfileurl(data: String, onBioReceived: (String) -> Unit) {
    Firebase.database.getReference("users").child(data).child("profilepictureurl")
        .get().addOnSuccessListener { snapshot ->
            val bio = snapshot.getValue(String::class.java) ?: "https://wallpapers.com/images/featured-full/link-pictures-16mi3e7v5hxno9c4.jpg"
            onBioReceived(bio)
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch bio", it)
        }
}
fun fetchusername(data: String, onReceivedname:(String)->Unit){
    Firebase.database.getReference("users").child(data).child("username")
        .get().addOnSuccessListener {
            snapshot->
            val name=snapshot.getValue(String::class.java)?:""
            onReceivedname(name)
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to fetch username", it)
        }

}

fun fetchcurrentuid(onGetUid: (String)-> Unit){

    val id=FirebaseAuth.getInstance().currentUser?.uid.toString()
    onGetUid(id)
}