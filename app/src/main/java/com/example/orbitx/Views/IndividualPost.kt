package com.example.orbitx.Views

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.ChatRepository.fetchusername
import com.example.orbitx.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualPost(data: String,navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var title by remember { mutableStateOf("") }
    var owneruserid by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var profilepicurl by remember { mutableStateOf("") }
    var isLiked by remember { mutableStateOf(false) }
    var likeCounter by remember { mutableStateOf(0) }
    var commentsCount by remember { mutableStateOf(0) }
    var formattedTimestamp by remember { mutableStateOf("") }


    LaunchedEffect(data) {
        db.collection("Posts").document(data).get().addOnSuccessListener {
            title = it.get("text").toString() ?: ""
            owneruserid = it.get("owneruid").toString() ?: ""
            imageUrl = it.get("imageUrl").toString() ?: ""

            fetchusername(owneruserid) { fetchedUsername ->
                username = fetchedUsername
            }
            fetchProfileurl(owneruserid) { fetchedProfilePicUrl ->
                profilepicurl = fetchedProfilePicUrl
            }
        }
    }



    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Profile") }, colors = TopAppBarDefaults.topAppBarColors(
            colorResource(id = R.color.orange)
        ), navigationIcon = { IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back")


        }})
    })
    { paddingValues ->

        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
        )
        {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profilepicurl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.avataricon),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "location",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = "Post image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                IconButton(onClick = {
                    isLiked = !isLiked
                    likeCounter += if (isLiked) 1 else -1
                }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(R.drawable.speech_bubble),
                        contentDescription = "Comment",
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Text(
                text = "$likeCounter Likes",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 12.dp),
            )
            Text(
                text = "$commentsCount Comments",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
            )
            Text(
                text = title,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 12.dp),
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formattedTimestamp,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
            )
        }
        }
    }
}
