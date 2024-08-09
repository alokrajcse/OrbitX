package com.example.createpost.ui.Screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.createpost.Model.Post
import com.example.createpost.Model.PostRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID


class CreatePostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepository(FirebaseFirestore.getInstance())

    private val _text = MutableStateFlow("")
    private val _imageUri = MutableStateFlow<Uri?>(null)
    private val _imageUrl = MutableStateFlow("")
    private val _isPostCreated = MutableStateFlow(false)

    val text: StateFlow<String> = _text
    val imageUri: StateFlow<Uri?> = _imageUri
    val imageUrl: StateFlow<String> = _imageUrl
    val isPostCreated: StateFlow<Boolean> = _isPostCreated

    fun onTextChanged(newText: String) {
        _text.value = newText
    }

    fun onImageSelected(uri: Uri) {
        _imageUri.value = uri
    }

    fun createPost(context: Context) {
        if (text.value.isNotBlank() && imageUri.value != null) {
            uploadImageToFirebase(imageUri.value!!) { imageUrl ->
                val post = Post(text.value, imageUrl)
                repository.createPost(post)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Post created successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to create post", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onSuccess(downloadUrl.toString())
                }
            }
            .addOnFailureListener {
                Log.e("UploadError", "Failed to upload image", it)
            }
    }
}