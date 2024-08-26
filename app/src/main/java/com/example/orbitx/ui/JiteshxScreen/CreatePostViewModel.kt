package com.example.orbitx.ui.JiteshxScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.orbitx.model.Post
import com.example.orbitx.model.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID


class CreatePostViewModel : ViewModel() {
    private val _isPostCreated = MutableStateFlow(false)
    val isPostCreated: StateFlow<Boolean> = _isPostCreated.asStateFlow()
    private val repository: PostRepository = PostRepository(FirebaseFirestore.getInstance())
    private val _text = MutableStateFlow("")
    private val _imageUri = MutableStateFlow<Uri?>(null)
    private val _imageUrl = MutableStateFlow("")


    val text: StateFlow<String> = _text
    val imageUri: StateFlow<Uri?> = _imageUri
    val imageUrl: StateFlow<String> = _imageUrl



    fun onTextChanged(newText: String) {
        _text.value = newText
    }

    fun onImageSelected(uri: Uri) {
        _imageUri.value = uri
    }

    fun createPost(context: Context, navController: NavController, location: String, hashtag: String) {
        _isPostCreated.value = true
        if (text.value.isBlank()) {
            Toast.makeText(context, "Please enter text", Toast.LENGTH_SHORT).show()
            _isPostCreated.value = false
            return
        }
        if (imageUri.value == null) {
            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
            _isPostCreated.value = false
            return
        }


        uploadImageToFirebase(imageUri.value!!) { imageUrl ->
            println("get text value::${text.value}")
            val post = Post(
                text = text.value,
                imageUrl = imageUrl,
                owneruid = FirebaseAuth.getInstance().currentUser!!.uid, // Get the current user's UID
                timestamp = System.currentTimeMillis(), // Get the current timestamp
                likesCount = 0, // Initialize likes count to 0
                commentsCount = 0, // Initialize comments count to 0
                comments = emptyList(), // Initialize comments list to empty
                likes = emptyList(), // Initialize likes list to empty
                location = location, // Set the location
                hashtag = hashtag // Set the hashtag
            )
            repository.createPost(post)
                .addOnSuccessListener {

                    navController.navigateUp()
                    Toast.makeText(context, "Post created successfully", Toast.LENGTH_SHORT).show()


                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to create post", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    _isPostCreated.value = false
                }
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
