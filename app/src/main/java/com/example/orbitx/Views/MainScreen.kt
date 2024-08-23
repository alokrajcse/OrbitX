package com.example.orbitx.Views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import com.example.orbitx.R
import com.example.orbitx.ViewModel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.orbitx.ChatRepository.User
import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.ChatRepository.fetchcurrentuid
import com.example.orbitx.ChatRepository.fetchusername
import com.example.orbitx.Navigation.BottomNavigationBar
import com.example.orbitx.model.Posts
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun HomeScreen(navController: NavController) {
//    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))
//    var isRefreshing by remember { mutableStateOf(false) }
//    var postsList by remember { mutableStateOf<List<Posts>>(emptyList()) }
//
//    val viewModel: AuthViewModel = viewModel()
//    val userProfileData by viewModel.userProfileData.observeAsState(emptyList())
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchPostsFromFirestore { posts ->
//            postsList = posts
//            Log.d("HomeScreen", "Posts fetched: ${posts.size}")
//        }
//
//    }
//
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        topBar = {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        Brush.verticalGradient(
//                            listOf(
//                                Color(0xFFF85A4F),
//                                Color(0xFFE49E99)
//                            )
//                        )
//                    )
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(5.dp)
//                ) {
//                    Text(
//                        text = "OrbitX",
//                        modifier = Modifier
//                            .padding(10.dp)
//                            .weight(1f),
//                        fontSize = 25.sp,
//                        fontWeight = FontWeight.Bold,
//                        fontFamily = urbanistMedium,
//                        color = Color.Black
//                    )
//
//                    IconButton(onClick = { navController.navigate("chats") }) {
//                        Image(
//                            painter = painterResource(id = R.drawable.messagebutton),
//                            contentDescription = "",
//                            modifier = Modifier.height(30.dp)
//                        )
//                    }
//                }
//            }
//        }
//    ) {
//        Box(modifier = Modifier.padding(top = 60.dp)) {
//            OrbitXFeed(
//                userProfileData = userProfileData,
//                postsList = postsList,
//                onRefresh = {
//                    isRefreshing = true
//                    viewModel.fetchPostsFromFirestore { posts ->
//                        postsList = posts
//                        isRefreshing = false
//                    }
//                }
//            )
//        }
//    }
//}

@Composable
fun HomeScreen(navController: NavController) {
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))
    var isRefreshing by remember { mutableStateOf(false) }
    var postsList by remember { mutableStateOf<List<Posts>>(emptyList()) }

    val viewModel: AuthViewModel = viewModel()
    val userProfileData by viewModel.userProfileData.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchPostsFromFirestore { posts ->
            postsList = posts
            Log.d("HomeScreen", "Posts fetched: ${posts.size}")
        }
        Log.d("HomeScreen", "UserProfileData: ${userProfileData.size}")
        userProfileData.forEach { user ->
            Log.d("HomeScreen", "User: ${user.username}, ${user.profilepictureurl}")
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFF85A4F),
                                Color(0xFFE49E99)
                            )
                        )
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    Text(
                        text = "OrbitX",
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = urbanistMedium,
                        color = Color.Black
                    )

                    IconButton(onClick = { navController.navigate("chats") }) {
                        Image(
                            painter = painterResource(id = R.drawable.messagebutton),
                            contentDescription = "",
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(top = 60.dp)) {
            OrbitXFeed(
                userProfileData = userProfileData,
                postsList = postsList,
                onRefresh = {
                    isRefreshing = true
                    viewModel.fetchPostsFromFirestore { posts ->
                        postsList = posts
                        isRefreshing = false
                        Log.d("HomeScreen", "Posts refreshed: ${posts.size}")
                    }
                }
            )
        }
    }
}


@Composable
fun MainScreen(activity: Activity) {
    val navController = rememberNavController()
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (com.example.orbitx.Navigation.shouldShowBottomBar(currentRoute)) {
                BottomNavigationBar(navController, selectedItemIndex) {
                    selectedItemIndex = it
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController)
            }
            composable("search") {
                SearchScreen(navController)
            }
            composable("newpost") {
                CreatePostScreen(navController, modifier = Modifier.padding(innerPadding))
            }
            composable("profile") {
                myProfileScreen(navController, userProfile = UserProfile2(
                    profilePictureUrl = "https://wallpapers.com/images/featured-full/link-pictures-16mi3e7v5hxno9c4.jpg",
                    username = "Shreya_12",
                    bio = " \uD83C\uDF1F Passionate Software Engineer | Cat Lover \uD83D\uDC31 | Lifelong Learner \uD83D\uDCDA | ",
                    isFollowing = true,
                    postCount = 5,
                    followerCount = 30,
                    followingCount = 12
                ))
            }
            composable("editprofile") {
                EditProfileScreen(navController)
            }
            composable("logout") {
                Exit(activity = activity, navController = navController)
            }
            composable("chats") {
                ChatHomeScreen(navController)
            }
            composable("MainChatScreen/{data}", arguments = listOf(navArgument("data") { type = NavType.StringType })) { backStackEntry ->
                val data = backStackEntry.arguments?.getString("data") ?: ""
                MainChatScreen(navController, data)
            }
            composable("otheruserprofile/{data}", arguments = listOf(navArgument("data") { type = NavType.StringType })) { backStackEntry ->
                val data = backStackEntry.arguments?.getString("data") ?: ""
                otherUserProfileSection(data=data,userProfile = UserProfile(
                    profilePictureUrl = "https://cdn-icons-png.flaticon.com/128/4322/4322991.png",

                    ), navController = navController
                )
            }
        }
    }
}

@Composable
fun OrbitXFeed(
    userProfileData: List<User>,
    postsList: List<Posts>,
    onRefresh: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val context= LocalContext.current

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            isRefreshing = true
            onRefresh()
            isRefreshing = false
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            items(postsList) { post ->
                val user = userProfileData.find { it.userId == post.owneruid }


                OrbitXPost(
                    profileImageUrl = user?.profilepictureurl ?: "",
                    username = user?.username ?: "Unknown User",
                    location = "Location",
                    owneruserid = post.owneruid,
                    postuid = post.postId,

                    imageUrl = post.imageUrl,
                    text = post.text,
                    initialIsLiked = false,
                    likesCount = post.likesCount,
                    commentsCount = post.commentsCount,
                    timestamp = post.timestamp,
                    onComment = { commentText ->
                        println("Comment posted: $commentText")
                    },
                    onShare = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Checkout this post on OrbitX: https://orbitxsocial.netlify.app/posts/${post.postId}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                    onLikeChange = { isLiked ->

                    }
                )
            }
        }
    }
}

@Composable
fun OrbitXPost(
    profileImageUrl: String,
    username: String,
    postuid: String,
    location: String,
    owneruserid: String,
    imageUrl: String,
    text: String,
    initialIsLiked: Boolean,
    likesCount: Int,
    commentsCount: Int,
    timestamp: Long,
    onComment: (String) -> Unit,
    onShare: () -> Unit,
    onLikeChange: (Boolean) -> Unit,
    viewModel: AuthViewModel= viewModel(),
) {
    var isLiked by remember { mutableStateOf(initialIsLiked) }
    var likeCounter by remember { mutableStateOf(likesCount) }
    var isCommentDialogOpen by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val formattedTimestamp = remember(timestamp) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        dateFormat.format(Date(timestamp))
    }
    var usrname by remember { mutableStateOf("") }
    var profilepicurl by remember { mutableStateOf("") }

    viewModel.fetchusername(owneruserid){it-> usrname=it}
    viewModel.fetchProfileurl(owneruserid){it-> profilepicurl=it}





    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = usrname,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = location,
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
                onLikeChange(isLiked)
            }) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(onClick = { isCommentDialogOpen = true }) {
                Icon(
                    painter = painterResource(R.drawable.speech_bubble),
                    contentDescription = "Comment",
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = {
                onShare()
            }) {
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
            text = text,
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

    if (isCommentDialogOpen) {
        AlertDialog(
            onDismissRequest = { isCommentDialogOpen = false },
            title = {
                Text(text = "Add a Comment")
            },
            text = {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text(text = "Write your comment...") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onComment(commentText)
                        isCommentDialogOpen = false
                        commentText = ""
                    }
                ) {
                    Text("Post")
                }
            },
            dismissButton = {
                Button(onClick = { isCommentDialogOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}