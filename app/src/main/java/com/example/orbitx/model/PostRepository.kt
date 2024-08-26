package com.example.orbitx.model

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class PostRepository(private val db: FirebaseFirestore) {

    var cuid=FirebaseAuth.getInstance().currentUser?.uid.toString()
    var profilepicurl=""

    fun createPost(post: Post): Task<DocumentReference> {
        // Optional: Validate post data
        if (post.text.isEmpty()) {
            throw IllegalArgumentException("Post text cannot be empty")
        }

        return db.collection("Posts2")
            .add(post.toHashMap())
            .addOnSuccessListener { documentReference ->
                // Handle success

                Log.d("PostRepository", "Post added with ID: ${documentReference.id}")
                Firebase.database.getReference("users").child(cuid).child("posts").push().setValue(documentReference.id)
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.w("PostRepository", "Error adding post", e)
            }
    }

    private fun Post.toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "text" to text,
            "imageUrl" to imageUrl,
            "owneruid" to cuid,
            "timestamp" to timestamp,
            "likesCount" to likesCount,
            "commentsCount" to commentsCount,

            "location" to location,
            "hashtag" to hashtag

            

        )
    }
}
