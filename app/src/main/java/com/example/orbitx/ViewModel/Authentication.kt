package com.example.orbitx.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orbitx.ChatRepository.User
import com.example.orbitx.ChatRepository.fetchcurrentuid
import com.example.orbitx.model.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class AuthViewModel : ViewModel() {
    private val _userProfileData = MutableLiveData<List<User>>()
    val userProfileData: LiveData<List<User>> get() = _userProfileData
    init {
        fetchUserProfileData()
    }
    private fun fetchUserProfileData() {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileList = mutableListOf<User>()

                for (childSnapshot in snapshot.children) {
                    val email = childSnapshot.child("email").getValue(String::class.java) ?: ""
                    val username = childSnapshot.child("username").getValue(String::class.java) ?: ""
                    val userId = childSnapshot.child("userId").getValue(String::class.java) ?: ""
                    val isFollowing = childSnapshot.child("isFollowing").getValue(Boolean::class.java) ?: false
                    val profilePictureUrl = childSnapshot.child("profilepictureurl").getValue(String::class.java) ?: ""

                    profileList.add(
                        User(
                            email = email,
                            username = username,
                            userId = userId,
                            isFollowing = isFollowing,
                            profilepictureurl = profilePictureUrl
                        )
                    )
                }
                _userProfileData.value = profileList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AuthViewModel", "Failed to fetch user data: ${error.message}")
            }
        })
    }


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
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        fetchcurrentuid { currentUserId ->
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
    fun fetchusername(data: String, onReceivedname:(String)->Unit){
        com.google.firebase.Firebase.database.getReference("users").child(data).child("username")
            .get().addOnSuccessListener {
                    snapshot->
                val name=snapshot.getValue(String::class.java)?:"Old User"
                onReceivedname(name)
            }.addOnFailureListener {
                Log.e("Firebase", "Failed to fetch username", it)
            }

    }
    fun fetchProfileurl(data: String, onUrlReceived: (String) -> Unit) {
        com.google.firebase.Firebase.database.getReference("users").child(data).child("profilepictureurl")
            .get().addOnSuccessListener { snapshot ->
                val url = snapshot.getValue(String::class.java) ?: "https://wallpapers.com/images/featured-full/link-pictures-16mi3e7v5hxno9c4.jpg"
                onUrlReceived(url)
            }.addOnFailureListener {
                Log.e("Firebase", "Failed to fetch bio", it)
            }
    }
}
fun writeNewUser(userId: String,  email: String, username: String) {
    var database = Firebase.database.reference
    val user = User( email,username,userId)
    database.child("users").child(userId).setValue(user)
}

