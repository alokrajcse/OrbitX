package com.example.orbitx.ui.JiteshxScreen

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.orbitx.model.Post
import com.example.orbitx.model.PostRepository
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

    enum class PostCreationStatus {
        Idle, Success, Failure, UploadFailure, EmptyFields
    }

    private val _postCreationStatus = MutableStateFlow(PostCreationStatus.Idle)
    val postCreationStatus: StateFlow<PostCreationStatus> = _postCreationStatus

    fun onTextChanged(newText: String) {
        _text.value = newText
    }

    fun onImageSelected(uri: Uri) {
        _imageUri.value = uri
    }

    fun createPost() {
        if (text.value.isNotBlank() && imageUri.value != null) {
            uploadImageToFirebase(imageUri.value!!) { imageUrl ->
                val post = Post(text.value, imageUrl)
                repository.createPost(post)
                    .addOnSuccessListener {
                        _postCreationStatus.value = PostCreationStatus.Success
                        _text.value = ""
                        _imageUri.value = null
                        _imageUrl.value = ""
                        _isPostCreated.value = true
                    }
                    .addOnFailureListener {
                        _postCreationStatus.value = PostCreationStatus.Failure
                    }
            }
        } else {
            _postCreationStatus.value = PostCreationStatus.EmptyFields
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
            .addOnFailureListener { exception ->
                Log.e("UploadError", "Failed to upload image", exception)
                _postCreationStatus.value = PostCreationStatus.UploadFailure
            }
    }
}
