package com.example.chatbyme2.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.chatbyme2.ChatRepository.getChatRoomId

import com.example.chatbyme2.model.MessageCard
import com.example.orbitx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@Composable
fun MainChatScreen(navController: NavController, data: String) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    if (currentUserId != null) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.moon2),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {

            ChatRoom(data, currentUserId, navController)
        }
    } else {

        Text("User not logged in", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
    }
}

@Composable
fun TopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "",
                tint = Color.White
            )
        }
        Text(
            text = "Chat",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            modifier = Modifier.padding(10.dp),
            color = Color.White
        )
    }
}

@Composable
fun ChatRoom(otherUserId: String, currentUserId: String, navController: NavController) {
    val db = Firebase.database.reference
    val chatRoomId = getChatRoomId(currentUserId, otherUserId)
    var messages by remember { mutableStateOf(listOf<MessageCard>()) }
    val listState = rememberLazyListState()

    LaunchedEffect(chatRoomId) {
        db.child("ChatRooms").child(chatRoomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMessages = mutableListOf<MessageCard>()
                snapshot.children.forEach { child ->
                    val msg = child.getValue(MessageCard::class.java)
                    if (msg != null) {
                        newMessages.add(msg)
                    }
                }
                messages = newMessages
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column {
        TopBar(navController = navController)

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.BottomCenter){
            LazyColumn(
                Modifier

                    .fillMaxWidth(),
                state = listState
            ) {
                items(messages.size) { index ->
                    val isCurrentUser = messages[index].senderId == currentUserId
                    val backgroundColor = if (isCurrentUser) Color.LightGray else Color.White
                    val arrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = arrangement
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .widthIn(max = 250.dp)
                        ) {
                            if (messages[index].imageUrl.isNotEmpty()) {

                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(messages.get(index).imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(R.drawable.loadingimage),
                                    contentDescription = "stringResource(R.string.description",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.clip(RectangleShape)
                                )
                            } else {
                                Text(
                                    text = messages[index].message,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        ChatBox(currentUserId, otherUserId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBox(currentUserId: String, otherUserId: String) {
    val db = Firebase.database.reference
    var textboxhint by remember { mutableStateOf("Write message") }
    val storageRef = Firebase.storage.reference
    val chatRoomId = getChatRoomId(currentUserId, otherUserId)
    var message by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Card(
        colors = CardDefaults.cardColors(Color.Black),
        modifier = Modifier.padding(5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = textboxhint, color = Color.White)
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = Color.White,
                    containerColor = Color.Black,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(onClick = { launcher.launch("image/*")
                textboxhint="Tap send button to send image"
                val sdf = SimpleDateFormat("HH:mm")
                val currentTime = sdf.format(Date())
                val storagePath = "chat_images/${chatRoomId}/${UUID.randomUUID()}.jpg"
                val imageRef = storageRef.child(storagePath)

                imageUri?.let { uri ->
                    val uploadTask = imageRef.putFile(uri)
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            val messageData = MessageCard(
                                message = message,
                                senderId = currentUserId,
                                lasttime = currentTime,
                                imageUrl = downloadUri.toString()
                            )
                            db.child("LastTime").child(chatRoomId).setValue(currentTime)
                            db.child("ChatRooms").child(chatRoomId).push().setValue(messageData)
                            message = ""
                            imageUri = null
                        }
                    }
                }
            }) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "", tint = Color.White)
            }
            IconButton(onClick = {
                if (imageUri != null) {

                    textboxhint="Tap send button to send image"
                    val sdf = SimpleDateFormat("HH:mm")
                    val currentTime = sdf.format(Date())
                    val storagePath = "chat_images/${chatRoomId}/${UUID.randomUUID()}.jpg"
                    val imageRef = storageRef.child(storagePath)

                    imageUri?.let { uri ->
                        val uploadTask = imageRef.putFile(uri)
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let { throw it }
                            }
                            imageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                val messageData = MessageCard(
                                    message = message,
                                    senderId = currentUserId,
                                    lasttime = currentTime,
                                    imageUrl = downloadUri.toString()
                                )
                                db.child("LastTime").child(chatRoomId).setValue(currentTime)
                                db.child("ChatRooms").child(chatRoomId).push().setValue(messageData)
                                message = ""
                                imageUri = null
                                textboxhint="write message"
                            }
                        }
                    }
                } else if (message.isNotEmpty()) {
                    val sdf = SimpleDateFormat("HH:mm")
                    val currentTime = sdf.format(Date())
                    val messageData = MessageCard(message, currentUserId, currentTime, "")
                    db.child("LastTime").child(chatRoomId).setValue(currentTime)
                    db.child("ChatRooms").child(chatRoomId).push().setValue(messageData)
                    message = ""
                }
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "", tint = Color.White)
            }
        }
    }
}


