package com.example.orbitx.Views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.orbitx.ChatRepository.fetchBio
import com.example.orbitx.ChatRepository.fetchFollowerCount
import com.example.orbitx.ChatRepository.fetchFollowingCount
import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun myProfileScreen(navController: NavController,userProfile: UserProfile2) {

    val context = LocalContext.current


    var profilePictureUrl by remember { mutableStateOf("") }


    var followers by remember { mutableStateOf(0) }
    var following by remember { mutableStateOf(0) }
    var myusername by remember {
        mutableStateOf("")
    }

    var bio by remember {
        mutableStateOf("")
    }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        LaunchedEffect(userId) {
            Firebase.database.getReference("users").child(userId).child("username").get()
                .addOnSuccessListener { snapshot ->
                    val myuser = snapshot.getValue(String::class.java)?:"Loading..."
                    if (myuser != null) {
                        myusername = myuser


                    }
                }
                .addOnFailureListener {


                }

            if (userId != null) {
                fetchFollowerCount(userId) {count-> followers=count}
                fetchFollowingCount(userId) { count -> following = count }
                fetchBio(userId){b->bio=b}
                fetchProfileurl(userId){url->profilePictureUrl=url}
            }

        }
    } else {

    }


    var user=UserProfile2(
        profilePictureUrl=profilePictureUrl,
        username = myusername,
        bio=bio,
        followerCount=followers,
        followingCount = following
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0x85DB4A3E) // Background color for the top bar
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB9DE3527))
                .padding(paddingValues)
                .padding(top = 25.dp, start = 16.dp, end = 16.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp) // Add some spacing
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth() // Fill the available width
                                .height(550.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                ProfileHeader(
                                    imageUrl = user.profilePictureUrl,
                                    size = 210.dp
                                )
                                Spacer(modifier = Modifier.height(7.dp))
                                ProfileContent(userProfile = user, navController = navController)
                            }
                        }

                        IconButton(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "This is my profile on OrbitX: https://orbitxsocial.netlify.app/profile/$userId")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)

                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = Color.Black
                            )
                        }
                    }
                }
                item {
                    Button(
                        onClick = { navController.navigate("newpost") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xB9DE3527),
                            contentColor = Color.White
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .align(Alignment.BottomCenter)
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Post",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Add Post",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileHeader(imageUrl: String, size: Dp, modifier: Modifier = Modifier) {


    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xB9DE3527)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = CircleShape,
        modifier = modifier.size(size)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

@Composable
fun ProfileContent(userProfile: UserProfile2, modifier: Modifier = Modifier,navController: NavController) {



    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Text(
            text = userProfile.username,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = userProfile.postCount.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Posts",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = userProfile.followerCount.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Followers",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = userProfile.followingCount.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Following",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = userProfile.bio,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                navController.navigate("editprofile")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6F61),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
            //shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(16.dp)
                .height(41.dp)
                .width(150.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit Profile",
                modifier = Modifier
                    .size(25.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "Edit Profile",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                /* Handle post click */ },
            shape = RoundedCornerShape(17.dp),
            modifier = Modifier
                .height(40.dp)
                .width(250.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor =  Color(0xB9DE3527),
                contentColor = Color.White)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Arrow",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 2.dp)
            )
            Text(
                text = " Your Posts ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    myProfileScreen()
//        userProfile = UserProfile2(
//            profilePictureUrl = "https://wallpapers.com/images/featured-full/link-pictures-16mi3e7v5hxno9c4.jpg",
//            username = "Shreya_12",
//            bio = " \uD83C\uDF1F Passionate Software Engineer | Cat Lover \uD83D\uDC31 | Lifelong Learner \uD83D\uDCDA | ",
//            isFollowing = true,
//            postCount = 5,
//            followerCount = 30,
//            followingCount = 12
//        )
//    )
//}

data class UserProfile2(
    val profilePictureUrl: String="",
    val username: String="",
    val bio: String="",
    val isFollowing: Boolean=false,
    val postCount: Int=0,
    val followerCount: Int=0,
    val followingCount: Int=0
)