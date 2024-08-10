package com.example.orbitx.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orbitx.model.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    fun fetchPostsFromFirestore(onResult: (List<Posts>) -> Unit) {
        db.collection("Posts")
            .get()
            .addOnSuccessListener { snapshot ->
                val postsList = snapshot.documents.mapNotNull { it.toObject<Posts>() }
                onResult(postsList)
            }
            .addOnFailureListener { e ->
                Log.w("AuthViewModel", "Error fetching posts", e)
            }
    }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    fun signIn(email: String, password: String, onNavigateToHome: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    onNavigateToHome()
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
