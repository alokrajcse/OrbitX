package com.example.orbitx.ViewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String, onNavigateToHome: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    onNavigateToHome()
                }
            }
    }

    fun signUp(username:String, email: String, password: String, onNavigateBackToLogin: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    writeNewUser(auth.currentUser?.uid.toString(),email,username)
                    onNavigateBackToLogin()
                }
            }
    }
}
fun writeNewUser(userId: String,  email: String, username: String) {


    var    database = Firebase.database.reference

    val user = User( email,username,userId)

    database.child("users").child(userId).setValue(user)
}

data class User(val email: String, val username: String? = null, val userId: String? = null)