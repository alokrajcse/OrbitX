package com.example.orbitx.model

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class PostRepository(private val db: FirebaseFirestore) {
    fun createPost(post: Post): Task<DocumentReference> {
        // Optional: Validate post data
        if (post.text.isEmpty()) {
            throw IllegalArgumentException("Post text cannot be empty")
        }

        return db.collection("Posts")
            .add(post.toHashMap())
            .addOnSuccessListener { documentReference ->
                // Handle success
                Log.d("PostRepository", "Post added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.w("PostRepository", "Error adding post", e)
            }
    }

    private fun Post.toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "text" to text,
            "imageUrl" to imageUrl
        )
    }
}
