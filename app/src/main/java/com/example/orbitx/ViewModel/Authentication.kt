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
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import java.util.UUID

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
        db.collection("Posts2")
            .get()
            .addOnSuccessListener { snapshot ->
                val postsList = snapshot.documents.mapNotNull { document ->
                    val post = document.toObject<Posts>()
                    post?.postId = document.id
                    post
                }


                val sortedPostsList = postsList.sortedByDescending { it.timestamp }

                onResult(sortedPostsList)
            }
            .addOnFailureListener { e ->
                Log.w("AuthViewModel", "Error fetching posts", e)
            }
    }


    fun updateLikesCount(postId: String, increment: Boolean) {
        val postRef = db.collection("Posts2").document(postId)

        postRef.update("likesCount", if (increment) FieldValue.increment(1) else FieldValue.increment(-1))
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Likes count updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to update likes count", e)
            }
    }

    fun toggleLikeForUser(postId: String, isLiked: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val likeRef = db.collection("Likes").document("${postId}_$userId")
        
        if (isLiked) {
            likeRef.set(mapOf("postId" to postId, "userId" to userId))
            updateLikesCount(postId, true)
        } else {
            likeRef.delete()
            updateLikesCount(postId, false)
        }
    }

    fun checkIfUserLikedPost(postId: String, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(false)
        val likeRef = db.collection("Likes").document("${postId}_$userId")
        likeRef.get().addOnSuccessListener { doc ->
            callback(doc.exists())
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun toggleBookmarkForUser(postId: String, isBookmarked: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val bookmarkRef = db.collection("Bookmarks").document("${postId}_$userId")
        
        if (isBookmarked) {
            bookmarkRef.set(mapOf("postId" to postId, "userId" to userId, "timestamp" to FieldValue.serverTimestamp()))
        } else {
            bookmarkRef.delete()
        }
    }

    fun checkIfUserBookmarkedPost(postId: String, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(false)
        val bookmarkRef = db.collection("Bookmarks").document("${postId}_$userId")
        bookmarkRef.get().addOnSuccessListener { doc ->
            callback(doc.exists())
        }.addOnFailureListener {
            callback(false)
        }
    }
    fun getPostCommentsCount(postId: String, callback: (Int) -> Unit) {
        db.collection("Posts2").document(postId)
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
        val postRef = db.collection("Posts2").document(postId)

        postRef.update("commentsCount", if (increment) FieldValue.increment(1) else FieldValue.increment(-1))
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Comments count updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to update comments count", e)
            }
    }

    fun uploadStoryImage(
        imageUri: Uri, 
        context: android.content.Context, 
        textElements: List<TextElement> = emptyList(),
        stickerElements: List<StickerElement> = emptyList(),
        onResult: (Boolean) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(false)
        val imageFileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child("stories/$userId/$imageFileName")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    addStoryToFirestore(uri.toString(), textElements, stickerElements, onResult)
                }.addOnFailureListener {
                    Log.e("AuthViewModel", "Failed to get download URL", it)
                    onResult(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to upload story image", e)
                onResult(false)
            }
    }

    private fun addStoryToFirestore(
        imageUrl: String, 
        textElements: List<TextElement>,
        stickerElements: List<StickerElement>,
        onResult: (Boolean) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(false)
        val storiesRef = db.collection("Stories")
        val storyId = storiesRef.document().id

        val newStory = Story(
            storyId = storyId,
            userId = userId,
            imageUrl = imageUrl,
            timestamp = FieldValue.serverTimestamp() as? com.google.firebase.Timestamp,
            textElements = textElements,
            stickerElements = stickerElements
        )

        storiesRef.document(storyId)
            .set(newStory)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Story added to Firestore successfully")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to add story to Firestore", e)
                onResult(false)
            }
    }

    fun fetchAllStories(callback: (List<Story>) -> Unit) {
        db.collection("Stories")
            .get()
            .addOnSuccessListener { snapshot ->
                val currentTimeSeconds = System.currentTimeMillis() / 1000
                val oneDaySeconds = 24 * 60 * 60

                val storiesList = snapshot.documents.mapNotNull { it.toObject(Story::class.java) }
                
                // Filter out stories older than 24 hours
                val validStories = storiesList.filter { story ->
                    val storyTime = story.timestamp?.seconds ?: 0L
                    (currentTimeSeconds - storyTime) <= oneDaySeconds
                }

                // Sort: group by userId, but within a user sort by oldest first so they play chronologically
                val sortedList = validStories.sortedWith(compareBy({ it.userId }, { it.timestamp?.seconds ?: 0L }))
                
                callback(sortedList)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to fetch stories", e)
                callback(emptyList())
            }
    }

    fun markStoryAsSeen(storyId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("Stories").document(storyId)
            .update("viewers", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Successfully marked story $storyId as seen by $userId")
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to mark story as seen", e)
            }
    }

    fun deleteStory(storyId: String, onResult: (Boolean) -> Unit) {
        // Technically this only deletes the Firestore document. If we wanted to, we could delete the Storage file too.
        db.collection("Stories").document(storyId)
            .delete()
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Successfully deleted story $storyId")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to delete story $storyId", e)
                onResult(false)
            }
    }

    fun addComment(postId: String, commentText: String) {
        val commentsRef = db.collection("Comments") // Use Comments collection directly

        val commentId = commentsRef.document().id
        val newComment = Comment(
            postId = postId,
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

    fun fetchComments(postId: String, callback: (List<Comment>) -> Unit) {
        db.collection("Comments").whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { snapshot ->
                val commentsList = snapshot.documents.mapNotNull { it.toObject(Comment::class.java) }
                val sortedList = commentsList.sortedBy { it.timestamp?.seconds ?: 0L }
                callback(sortedList)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to fetch comments", e)
                callback(emptyList())
            }
    }

    fun fetchLocation(postId: String, callback: (String) -> Unit) {
        db.collection("Posts2").document(postId)
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
        db.collection("Posts2").document(postId)
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
    val postId: String = "",
    val commentText: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)

data class Story(
    val storyId: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val timestamp: com.google.firebase.Timestamp? = null,
    val viewers: List<String> = emptyList(),
    val textElements: List<TextElement> = emptyList(),
    val stickerElements: List<StickerElement> = emptyList()
)

data class TextElement(
    val id: String = "",
    val text: String = "",
    val colorString: String = "FFFFFFFF", // Default White
    val bgStyle: String = "NONE",
    val fontFamily: String = "DEFAULT",
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f
)

data class StickerElement(
    val id: String = "",
    val stickerId: String = "", // E.g., emoji string or resource identifier
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f
)
