package com.example.orbitx.views

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
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
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
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
import com.example.chatbyme2.Screens.MainChatScreen
import com.example.chatbyme2.ui.ChatHomeScreen
import com.example.orbitx.R
import com.example.orbitx.ViewModel.AuthViewModel
import com.example.orbitx.model.BottomNavigationItem
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orbitx.Views.CreatePostScreen
import com.example.orbitx.Views.Exit
import com.example.orbitx.Views.SearchScreen
import com.example.orbitx.Views.UserProfile
import com.example.orbitx.model.Posts

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFFF85A4F), Color(0xFFE49E99))))
                    .padding(10.dp)
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
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = urbanistMedium,
                        color = Color.Black
                    )
                    IconButton(onClick = { }) {
                        Image(
                            painter = painterResource(id = R.drawable.messagebutton),
                            contentDescription = "",
                            modifier = Modifier.height(30.dp).clip(CircleShape)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            InstagramFeed()
        }
    }
}

@Composable
fun MainScreen(activity: Activity) {
    val navController = rememberNavController()
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (shouldShowBottomBar(currentRoute)) {
                BottomNavigationBar(navController, selectedItemIndex) {
                    selectedItemIndex = it
                }
            }
        }

    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen(navController) }
            composable("search") { SearchScreen(navController) }
            composable("newpost") { CreatePostScreen() }
            composable("profile/{data}", arguments = listOf(navArgument("data") { type = NavType.StringType })) { backStackEntry ->
                val data = backStackEntry.arguments?.getString("data") ?: ""
                UserProfile()
            }
            composable("logout") { Exit(activity = activity, navController = navController) }
            composable("chats") { ChatHomeScreen(navController) }
            composable("MainChatScreen/{data}", arguments = listOf(navArgument("data") { type = NavType.StringType })) { backStackEntry ->
                val data = backStackEntry.arguments?.getString("data") ?: ""
                MainChatScreen(navController, data)
            }
        }
    }
}

fun shouldShowBottomBar(currentRoute: String?): Boolean {
    val routesWithoutBottomBar = listOf("chats", "MainChatScreen/{data}", "logout")
    return !routesWithoutBottomBar.contains(currentRoute)
}

@Composable
fun BottomNavigationBar(navController: NavController, selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        BottomNavigationItem(
            title = "home",
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "search",
            selectedIcon = Icons.Default.Search,
            unselectedIcon = Icons.Outlined.Search,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "newpost",
            selectedIcon = Icons.Default.AddCircle,
            unselectedIcon = Icons.Outlined.AddCircle,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "profile",
            selectedIcon = Icons.Default.Person,
            unselectedIcon = Icons.Outlined.Person,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "logout",
            selectedIcon = Icons.Default.ExitToApp,
            unselectedIcon = Icons.Outlined.ExitToApp,
            hasNews = false
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = Modifier.height(80.dp), containerColor = colorResource(id = R.color.orange)) {
        items.forEachIndexed { index, item ->
            val isSelected = currentRoute == item.title
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    onItemSelected(index)
                    navController.navigate(item.title) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    BadgedBox(badge = {}) {
                        Icon(
                            imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = null
                        )
                    }
                },
                label = {}
            )
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

    LaunchedEffect(Unit) {
        viewModel.fetchPostsFromFirestore { posts ->
            Log.d("InstagramFeed", "Fetched posts: $posts")
            postsList = posts
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
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

