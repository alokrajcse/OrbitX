package com.example.orbitx.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.orbitx.ChatRepository.ChatViewModel
import com.example.orbitx.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatHomeScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.orbit),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(modifier = Modifier.fillMaxSize()) {
            FirebaseUI(navController)
        }
    }
}
@Composable
fun FirebaseUI(navController: NavHostController, viewModel: ChatViewModel = viewModel()) {
    val chatcardlist by viewModel.chatcardlist.observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "", tint = Color.White)
            }
            Text(
                text = "Chats",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                modifier = Modifier.padding(10.dp),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))


        LazyColumn {
            items(chatcardlist ?: emptyList()) { chatCard ->
                ChatCardItem(chatCard = chatCard, navController = navController)
            }
        }
    }
}

@Composable
fun ChatCardItem(chatCard: ChatCard, navController: NavHostController) {
    Card(
        onClick = { navController.navigate("MainChatScreen/${chatCard.userId}") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        colors = CardDefaults.cardColors(colorResource(id = R.color.white)),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(chatCard.profilePictureUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.avataricon),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .height(52.dp)
                    .width(52.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = chatCard.username,
                        fontSize = 22.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = if (chatCard.onlinestatus) "Online" else "Offline",
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(2.dp),
                        color = if (chatCard.onlinestatus) Color.Magenta else Color.Gray
                    )
                }
            }

            val currentTimeMillis = chatCard.lastTime
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(currentTimeMillis)
            val formattedTime = sdf.format(date)
            Text(
                text = if (formattedTime=="05:30") "" else formattedTime,
                textAlign = TextAlign.End
            )
        }
    }
}
data class ChatCard(
    val userId: String = "",
    val profilePictureUrl: String = "",
    val username: String = "",
    val onlinestatus: Boolean = false,
    val lastTime: Long = 0L
)