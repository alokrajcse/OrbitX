package com.example.orbitx.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.orbitx.ChatRepository.fetchcurrentuid
import com.example.orbitx.model.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume


class AuthViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val realtimeDb = Firebase.database.reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun fetchPostsFromFirestore(onResult: (List<Posts>) -> Unit) {
        db.collection("Posts")
            .get()
            .addOnSuccessListener { snapshot ->
                val postsList = snapshot.documents.mapNotNull { it.toObject<Posts>() }
                val updatedPostsList = mutableListOf<Posts>()

                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val tasks = postsList.map { post ->
                            async {
                                val (username, profilePictureUrl) = fetchUserData(post.owneruid)
                                Log.d("AuthViewModel", "Updating post with ID: username: $username, profilePictureUrl: $profilePictureUrl")

                                post.username = username
                                post.profilepictureurl = profilePictureUrl
                                updatedPostsList.add(post)
                            }
                        }
                        tasks.awaitAll()
                    }
                    onResult(updatedPostsList)
                }
            }
            .addOnFailureListener { e ->
                Log.w("AuthViewModel", "Error fetching posts", e)
            }
    }

    private suspend fun fetchUserData(userId: String): Pair<String, String> {
        return suspendCancellableCoroutine { continuation ->
            realtimeDb.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").getValue(String::class.java) ?: ""
                    val profilePictureUrl = snapshot.child("profilepictureurl").getValue(String::class.java) ?: ""
                    continuation.resume(Pair(username, profilePictureUrl))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("AuthViewModel", "Error fetching user data", error.toException())
                    continuation.resume(Pair("", ""))
                }
            })
        }
    }
    fun signIn(email: String, password: String, onNavigateToHome: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        fetchcurrentuid { currentUserId ->
                            // Ensure the currentUserId is not empty before subscribing
                            if (currentUserId.isNotEmpty()) {
                                FirebaseMessaging.getInstance().subscribeToTopic("message$currentUserId").addOnSuccessListener {
                                    onNavigateToHome()

                                }
                            }
                        }
                    }

                }
            }
    }
    fun signUp(
        username: String,
        email: String,
        password: String,
        onNavigateBackToLogin: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    writeNewUser(auth.currentUser?.uid.toString(), email, username)
                    onNavigateBackToLogin()
                }
            }
    }
}
fun writeNewUser(userId: String,  email: String, username: String) {
    var database = Firebase.database.reference
    val user = User( email,username,userId)
    database.child("users").child(userId).setValue(user)
}

data class User(val email: String, val username: String? = null, val userId: String? = null)