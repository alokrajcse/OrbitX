package com.example.orbitx.Views

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import com.example.orbitx.R
import com.example.orbitx.ViewModel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orbitx.Navigation.BottomNavigationBar
import com.example.orbitx.model.Posts
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))


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
        Box(modifier = Modifier.padding(top=60.dp)) {

            InstagramFeed()
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
                CreatePostScreen(navController,modifier = Modifier.padding(innerPadding))
            }
            composable("profile") {
                myProfileScreen(navController,userProfile = UserProfile2(
                    profilePictureUrl = "https://wallpapers.com/images/featured-full/link-pictures-16mi3e7v5hxno9c4.jpg",
                    username = "Shreya_12",
                    bio = " \uD83C\uDF1F Passionate Software Engineer | Cat Lover \uD83D\uDC31 | Lifelong Learner \uD83D\uDCDA | ",
                    isFollowing = true,
                    postCount = 5,
                    followerCount = 30,
                    followingCount = 12
                )
                )
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

                )
                )
            }
        }
    }
}



@Composable
fun InstagramPost(
    profileImageResId: Int,
    username: String,
    location: String,
    imageUrl: String,
    text: String,
    initialIsLiked: Boolean,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onLikeChange: (Boolean) -> Unit
) {
    var isLiked by remember { mutableStateOf(initialIsLiked) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(profileImageResId),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
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
                .padding(10.dp)
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
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                isLiked = !isLiked
                onLikeChange(isLiked)
            }) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = onComment) {
                Icon(
                    painter = painterResource(R.drawable.speech_bubble),
                    contentDescription = "Comment",
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${if (isLiked) 311 else 310} Likes",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = text,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun InstagramFeed(viewModel: AuthViewModel = viewModel()) {
    var postsList by remember { mutableStateOf<List<Posts>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchPostsFromFirestore { posts ->
            postsList = posts
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            isRefreshing = true
            viewModel.fetchPostsFromFirestore { posts ->
                postsList = posts
                isRefreshing = false
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            items(postsList) { post ->
                InstagramPost(
                    profileImageResId = R.drawable.avataricon,
                    username = "username",
                    location = "Indonesia",
                    imageUrl = post.imageUrl,
                    text = post.text,
                    initialIsLiked = false,
                    onComment = { /* Handle comment action */ },
                    onShare = { /* Handle share action */ },
                    onLikeChange = { isLiked ->
                    }
                )
            }
        }
    }
}