package com.example.orbitx.Views

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.orbitx.ChatRepository.fetchBio
import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.ChatRepository.fetchusername
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.storage.storage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profilepicurl by remember {
        mutableStateOf("")
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    var currentuid= Firebase.auth.currentUser?.uid.toString()
    fetchProfileurl(currentuid){it-> profilepicurl=it}
    fetchusername(currentuid){it->username=it}
    fetchBio(currentuid){it->bio=it}
    email=Firebase.auth.currentUser?.email.toString()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { navController.popBackStack() }
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { /* Handle share click */ }
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFF6F61)  // Setting background color
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(120.dp)
                ) {
                    if (profileImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(profileImageUri),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(profilepicurl),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    }

                    // Plus icon for changing the picture
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Profile Picture",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF6F61))
                            .clickable {
                                imagePickerLauncher.launch("image/*")
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                ProfileTextField(label = "Username", value = username, onValueChange = { username = it })
                Spacer(modifier = Modifier.height(16.dp))
                ProfileTextField(label = "Email ID", value = email, onValueChange = { email = it })
                Spacer(modifier = Modifier.height(16.dp))
                ProfileTextField(label = "Phone Number", value = phoneNumber, onValueChange = { phoneNumber = it })
                Spacer(modifier = Modifier.height(16.dp))
                ProfileTextField(label = "Bio", value = bio, onValueChange = { bio = it })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { updateprofiledetails(username,email,phoneNumber,bio,profileImageUri)
                          navController.navigateUp()},
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Update",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun updateprofiledetails(username: String, email: String, phoneNumber: String, bio: String, profileImageUri: Uri?) {

    var db = Firebase.database.getReference("users")
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var db2 = Firebase.storage.getReference("profilepictures")
    if (userId != null) {
        db.child(userId).child("bio").setValue(bio)
        db.child(userId).child("username").setValue(username)

        if (profileImageUri != null) {
            db2.child(userId).putFile(profileImageUri).addOnSuccessListener {
                db2.child(userId).downloadUrl.addOnSuccessListener {
                    db.child(userId).child("profilepictureurl").setValue(it.toString())

                }
            }

        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}