package com.example.orbitx.Views

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.orbitx.ChatRepository.MessageCard
import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.ChatRepository.fetchusername
import com.example.orbitx.ChatRepository.getChatRoomId
import com.example.orbitx.Notification.AppState
import com.example.orbitx.Notification.sendTopicNotification2

import com.example.orbitx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@Composable
fun MainChatScreen(navController: NavController, data: String) {



    LaunchedEffect(Unit) {
        AppState.isChatScreenVisible = true
        AppState.currentChatUid = data
    }


    DisposableEffect(Unit) {
        onDispose {
            AppState.isChatScreenVisible = false
            AppState.currentChatUid = "null"
        }
    }
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
fun TopBar(navController: NavController, otherUserId: String) {

    var displayname by remember {
        mutableStateOf("")
    }
    var imageurl by remember { mutableStateOf("") }
    fetchusername(otherUserId){it->displayname=it}
    fetchProfileurl(otherUserId){it-> imageurl=it}
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
        Row(verticalAlignment = Alignment.CenterVertically) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageurl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.avataricon),
                contentDescription = "",
                modifier = Modifier
                    .padding(5.dp)
                    .height(40.dp)
                    .width(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,

            )

            Text(
                text = displayname,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                modifier = Modifier.padding(10.dp),
                color = Color.White
            )
        }

    }
}

@Composable
fun ChatRoom(otherUserId: String, currentUserId: String, navController: NavController) {

    Scaffold(topBar = { TopBar(navController = navController, otherUserId) }) { paddingValues ->
        val db = Firebase.database.reference
        val chatRoomId = getChatRoomId(currentUserId, otherUserId)
        var messages by remember { mutableStateOf(listOf<MessageCard>()) }
        val listState = rememberLazyListState()
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

        Column(modifier = Modifier.padding(paddingValues)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.BottomCenter
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
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
                                            .data(messages[index].imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(R.drawable.loadingimage),
                                        contentDescription = "Image",
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBox(currentUserId: String, otherUserId: String) {
    val db = Firebase.database.reference
    var textboxhint by remember { mutableStateOf("Write message") }
    val storageRef = Firebase.storage.reference
    val chatRoomId = getChatRoomId(currentUserId, otherUserId)
    var message by remember { mutableStateOf("") }
    var messagetitle by remember {
        mutableStateOf("")
    }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    var imageurl by remember {
        mutableStateOf("")
    }

    var sending by remember {
        mutableStateOf(false)
    }
    val context= LocalContext.current

    fetchusername(currentUserId){it-> messagetitle=it}
    fetchProfileurl(currentUserId){it-> imageurl=it}

    val media= rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(), onResult = {
            uri-> if (uri!=null){
        //text1=uri.toString()
                sending=true

        var storageref= com.google.firebase.Firebase.storage.getReference("practice").child("part3")
        val task=storageref.putFile(uri)

        task.addOnSuccessListener {

            storageref.downloadUrl.addOnSuccessListener {

                imageurl=it.toString()
                val sdf = SimpleDateFormat("HH:mm")
                val currentTime = sdf.format(Date())


                val messageData = MessageCard(
                    message = message,
                    senderId = currentUserId,
                    lasttime = currentTime,
                    imageUrl = imageurl)

                db.child("LastTime").child(chatRoomId).setValue(currentTime)
                db.child("ChatRooms").child(chatRoomId).push().setValue(messageData)
                message = ""

                sendTopicNotification2(context,"message$otherUserId", messagetitle, "Sent an Image", currentUserId,otherUserId,"MainChatScreen/$currentUserId")
                println("from chat::"+messagetitle)


                sending=false
                Toast.makeText(context, "IMAGE SENT", Toast.LENGTH_SHORT).show()


            }
        }

    }
    else{
    }
    })
    if (sending){
        Bar()
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
            IconButton(onClick = { media.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
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

                    sendTopicNotification2(context,"message$otherUserId", messagetitle, message, currentUserId,otherUserId,"MainChatScreen/$currentUserId")
                    message = ""
                }
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "", tint = Color.White)
            }
        }
    }

}

@Composable
fun Bar(modifier: Modifier = Modifier) {
    Column() {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            Text(text = "Sending Image", textAlign = TextAlign.Center, modifier = Modifier.padding(5.dp), fontWeight = FontWeight.Bold, color = Color.White)
        }

        Row {

            LinearProgressIndicator(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                color = Color.Green,
                trackColor = Color.LightGray,
            )
        }


    }

}


