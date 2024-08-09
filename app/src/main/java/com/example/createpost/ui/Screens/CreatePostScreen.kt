package com.example.createpost.ui.Screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.createpost.Model.Post
import com.example.createpost.R
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.createpost.Model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun CreatePostScreen(viewModel: CreatePostViewModel = viewModel(), modifier: Modifier) {
    val text by viewModel.text.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val context = LocalContext.current
    val db = Firebase.firestore
    val postRef = db.collection("Users").document("vVDzSYUgxnw62SVdBF8m") // replace with the actual post ID
    var authorAvatarUrl by remember { mutableStateOf("") }
    var post by remember { mutableStateOf(User(
        authorName = "",
        authorAvatarUrl = "",
        timestamp = System.currentTimeMillis() / 1000,
    )) }

    LaunchedEffect(Unit) {
        launch {
            val postSnapshot = postRef.get().await()
            if (postSnapshot.exists()) {
                authorAvatarUrl = postSnapshot.get("authorAvatarUrl") as String
                post = User(
                    authorName = postSnapshot.get("authorName") as String,
                    authorAvatarUrl = postSnapshot.get("authorAvatarUrl") as String,
                    timestamp = post.timestamp
                )
            }
        }
    }


        // Activity result launcher to handle the image picking
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }
    Surface(color = Color.White,
        modifier = Modifier.padding(top=40.dp)
            .fillMaxSize()) {
        Column () {
            TopBar(
                onBackPressed = { /* Handle back press action here */ },
                onPostClicked = { viewModel.createPost(context) }
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(authorAvatarUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.ic_placeholder)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(
                    Modifier
                        .weight(1f)
                ) {
                    Text(
                        post.authorName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        ,
                        color = Color.Black
                    )
                    val today = remember {
                        Date()
                    }
                    Text(
                        post.timestamp?.let { dateLabel(timestamp = it, today = today) } ?: "",
                        color = Color.DarkGray

                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.menu),
                        tint = Color.Black
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            var focusedContainerColor by remember { mutableStateOf(Color(237, 222, 221))}

            TextField(value = text, onValueChange ={viewModel.onTextChanged(it)},
            placeholder = {Text(text = "What's on your mind?",style = TextStyle(
                fontSize = 25.sp) , // Change font size here
                color = Color.Gray
            )},
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White,
                    focusedContainerColor = focusedContainerColor),
                modifier = Modifier
                    .background(Color.Yellow)
                    .fillMaxWidth()
                    .height(300.dp))
            Spacer(Modifier.height(1.dp))
            ImagePicker(
                imageUri = imageUri,
                onImageSelected = { imagePickerLauncher.launch("image/*") }
            )
            Spacer(Modifier.height(1.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ){var showColorPicker by remember { mutableStateOf(false) }
                ExtendedFloatingActionButton(
                    onClick = { showColorPicker = true },
                    icon = {Box(Modifier.size(24.dp)) {
                        val myImage: Painter = painterResource(id = R.drawable.color)
                        Image(painter = myImage, contentDescription = "Edit")
                    }},
                    text = { Text(text = "Background Colour",style = TextStyle(
                        fontSize = 20.sp, // Change font size here
                        color = Color.Black
                    )) },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .fillMaxSize(),
                    containerColor = Color.White,
                )

                // Show color picker dialog
                if (showColorPicker) {
                    ColorPickerDialog(
                        onColorSelected = { color ->
                            focusedContainerColor = color
                            showColorPicker = false
                        },
                        onDismissRequest = { showColorPicker = false }
                    )

                }
            }
            Spacer(Modifier.height(1.dp))
            Location()
            Spacer(Modifier.height(1.dp))
            Hastag()
            Spacer(Modifier.height(1.dp))
            Activity()
            Spacer(Modifier.height(1.dp))
            Texts()
            Spacer(Modifier.height(1.dp))
            Link()

        }
    }
}

@Composable
fun Link() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        ExtendedFloatingActionButton(
            onClick = { },
            icon = {Box(Modifier.size(24.dp)) {
                val myImage: Painter = painterResource(id = R.drawable.link)
                Image(painter = myImage, contentDescription = "Edit")
            }},
            text = {
                Text(
                    text = "Link", style = TextStyle(
                        fontSize = 20.sp,// Change font size here
                        color = Color.Black
                    )
                )
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color.White,
        )
    }
}

@Composable
fun Texts() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        ExtendedFloatingActionButton(
            onClick = { },
            icon = {Box(Modifier.size(24.dp)){
                val myImage: Painter = painterResource(id = R.drawable.text)
                Image(painter = myImage, contentDescription = "Edit")
            }},
            text = {
                Text(
                    text = "Text", style = TextStyle(
                        fontSize = 20.sp,// Change font size here
                        color = Color.Black
                    )
                )
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color.White,
        )
    }
}

@Composable
fun Activity() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        ExtendedFloatingActionButton(
            onClick = { },
            icon = {Box(Modifier.size(24.dp)){
                val myImage: Painter = painterResource(id = R.drawable.activity)
                Image(painter = myImage, contentDescription = "Edit")
            }},
            text = {
                Text(
                    text = "Activity", style = TextStyle(
                        fontSize = 20.sp,// Change font size here
                        color = Color.Black
                    )
                )
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color.White,
        )
    }
}

@Composable
fun Hastag() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        ExtendedFloatingActionButton(
            onClick = { },
            icon = {Box(Modifier.size(24.dp)){
                val myImage: Painter = painterResource(id = R.drawable.hashtag)
                Image(painter = myImage, contentDescription = "Edit")
            }},
            text = {
                Text(
                    text = "Hashtag", style = TextStyle(
                        fontSize = 20.sp,// Change font size here
                        color = Color.Black
                    )
                )
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color.White,
        )
    }
}

@Composable
fun Location() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        ExtendedFloatingActionButton(
            onClick = { },
            icon = {
                Box(Modifier.size(24.dp)) { // adjust the size here
                    val myImage: Painter = painterResource(id = R.drawable.location)
                    Image(painter = myImage, contentDescription = "Edit")
                }
            },
            text = {
                Text(
                    text = "Check In", style = TextStyle(
                        fontSize = 20.sp,// Change font size here
                        color = Color.Black
                    )
                )
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color.White,
        )
    }
}

@Composable
fun ColorPickerDialog(onColorSelected: (Color) -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select a Color") },
        text = {
            ColorPickerGrid(onColorSelected = onColorSelected)
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ColorPickerGrid(onColorSelected: (Color) -> Unit) {
    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
        Color.Magenta, Color.Cyan, Color.Black, Color.Gray
    )
    // Creating a grid of colors
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(count = colors.size) { index ->
            val color = colors[index]
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color = color)
                    .clickable { onColorSelected(color) }
                    .padding(4.dp)
            )
        }
    }
}
@Composable
fun TopBar(onBackPressed: () -> Unit, onPostClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(231, 107, 99))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackPressed) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            text = "Create Post",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            color = Color(237, 222, 221)
        )
        Button(onClick = onPostClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Color(237, 222, 221)) ){
            Text("Post")
        }
    }
}

fun dateLabel(timestamp: Long, today: Date): String {
    Log.d("Timestamp", "Formatting timestamp: $timestamp") // Add this line
    val timestampDate = Date(timestamp * 1000)
    val calendar = Calendar.getInstance()
    calendar.time = today

    // Check if the timestamp is today
    if (isSameDay(timestampDate, today)) {
        return "Today"
    }

    // Check if the timestamp is yesterday
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    if (isSameDay(timestampDate, calendar.time)) {
        return "Yesterday"
    }

    // Check if the timestamp is tomorrow
    calendar.add(Calendar.DAY_OF_YEAR, 2)
    if (isSameDay(timestampDate, calendar.time)) {
        return "Tomorrow"
    }

    // If none of the above, return a formatted date string
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return dateFormat.format(timestampDate)
}
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val calendar1 = Calendar.getInstance()
    calendar1.time = date1
    val calendar2 = Calendar.getInstance()
    calendar2.time = date2
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}


@Composable
fun ImagePicker(imageUri: Uri?, onImageSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Adjust height as needed
                    .clip(RoundedCornerShape(8.dp)) // Optional: for rounded corners
            )
        }

        ExtendedFloatingActionButton(
            onClick = { onImageSelected() },
            icon = {Box(Modifier.size(24.dp)){
                val myImage: Painter = painterResource(id = R.drawable.image_gallery)
                Image(painter = myImage, contentDescription = "Edit")
            }},
            text = { Text(text = "Photo",style = TextStyle(
                fontSize = 20.sp,// Change font size here
                color = Color.Black
            )
            ) },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color.White
        )
    }
}


