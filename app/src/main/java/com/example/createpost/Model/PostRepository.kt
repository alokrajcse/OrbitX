package com.example.createpost.Model

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class PostRepository(private val db: FirebaseFirestore) {
    fun createPost(post: Post): Task<DocumentReference> {
        return db.collection("Posts")
            .add(post.toHashMap())

    }


    private fun Post.toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "text" to text,
            "imageUrl" to imageUrl
        )
    }
}