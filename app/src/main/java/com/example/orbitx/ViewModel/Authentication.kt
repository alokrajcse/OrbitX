package com.example.orbitx.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orbitx.ChatRepository.User
import com.example.orbitx.ChatRepository.fetchcurrentuid
import com.example.orbitx.model.Post
import com.example.orbitx.model.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class AuthViewModel : ViewModel() {
    private val _userProfileData = MutableLiveData<List<User>>()
    val userProfileData: LiveData<List<User>> get() = _userProfileData
    private val db = FirebaseFirestore.getInstance()

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

    fun fetchPostsFromFirestore(onResult: (List<Posts>) -> Unit) {
        db.collection("Posts")
            .get()
            .addOnSuccessListener { snapshot ->
                val postsList = snapshot.documents.mapNotNull { document ->
                    val post = document.toObject<Posts>()
                    post?.postId = document.id
                    post
                }
                onResult(postsList)
            }
            .addOnFailureListener { e ->
                Log.w("AuthViewModel", "Error fetching posts", e)
            }
    }

    fun updateLikesCount(postId: String, increment: Boolean) {
        val postRef = db.collection("Posts").document(postId)

        postRef.update("likesCount", if (increment) FieldValue.increment(1) else FieldValue.increment(-1))
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Likes count updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to update likes count", e)
            }
    }
    fun getPostCommentsCount(postId: String, callback: (Int) -> Unit) {
        db.collection("Posts").document(postId)
            .get()
            .addOnSuccessListener { document ->
                val commentsCount = document.getLong("commentsCount")?.toInt() ?: 0
                callback(commentsCount)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to get comments count", e)
                callback(0)
            }
    }

    fun updateCommentsCount(postId: String, increment: Boolean) {
        val postRef = db.collection("Posts").document(postId)

        postRef.update("commentsCount", if (increment) FieldValue.increment(1) else FieldValue.increment(-1))
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Comments count updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to update comments count", e)
            }
    }

    fun addComment(postId: String, commentText: String) {
        val commentsRef = db.collection("Comments") // Use Comments collection directly

        val commentId = commentsRef.document().id
        val newComment = Comment(
            commentText = commentText,
            timestamp = FieldValue.serverTimestamp() as? com.google.firebase.Timestamp
        )

        commentsRef.document(commentId)
            .set(newComment)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Comment added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to add comment", e)
            }
    }

    fun fetchLocation(postId: String, callback: (String) -> Unit) {
        db.collection("Posts").document(postId)
            .get()
            .addOnSuccessListener { document ->
                val location = document.getString("location") ?: ""
                callback(location)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to get location", e)
                callback("")
            }
    }


    fun fetchHashtag(postId: String, callback: (String) -> Unit) {
        db.collection("Posts").document(postId)
            .get()
            .addOnSuccessListener { document ->
                val hashtag = document.getString("hashtag") ?: ""
                callback(hashtag)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to get hashtag", e)
                callback("")
            }
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String, onNavigateToHome: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser?.let {
                        fetchcurrentuid { currentUserId ->
                            if (currentUserId.isNotEmpty()) {
                                FirebaseMessaging.getInstance().subscribeToTopic("message$currentUserId")
                                    .addOnSuccessListener {
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

    fun fetchusername(data: String, onReceivedname: (String) -> Unit) {
        Firebase.database.getReference("users").child(data).child("username")
            .get()

            .addOnSuccessListener { snapshot ->
                println("username fetch success")
                val name = snapshot.getValue(String::class.java) ?: "Old User"
                onReceivedname(name)
            }

            .addOnFailureListener {
                println("username fetch failed")
                Log.e("Firebase", "Failed to fetch username", it)
            }

    }

    fun fetchProfileurl(data: String, onUrlReceived: (String) -> Unit) {
        Firebase.database.getReference("users").child(data).child("profilepictureurl")
            .get()
            .addOnSuccessListener { snapshot ->
                val url = snapshot.getValue(String::class.java)
                    ?: "https://wallpapers.com/images/featured-full/link-pictures-16mi3e7v5hxno9c4.jpg"
                onUrlReceived(url)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to fetch profile picture URL", it)
            }
    }
}

fun writeNewUser(userId: String, email: String, username: String) {
    val database = Firebase.database.reference
    val user = User(email, username, userId)
    database.child("users").child(userId).setValue(user)
}

data class Comment(
    val commentText: String,
    val timestamp: com.google.firebase.Timestamp? = null
)
