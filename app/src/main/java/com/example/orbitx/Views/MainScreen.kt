package com.example.orbitx.Views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined. FavoriteBorder
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import android.util.Base64
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import com.example.orbitx.R
import com.example.orbitx.ViewModel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.orbitx.ChatRepository.User
import com.example.orbitx.Navigation.BottomNavigationBar
import com.example.orbitx.model.Posts
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.orbitx.ViewModel.Story
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HeartAnimationOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Double tap like",
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF4F3F8), // Matches the purple-ish white background
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    IconButton(onClick = { /* Camera action */ }) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "Camera",
                            modifier = Modifier.size(28.dp),
                            tint = Color(0xFF2D3748)
                        )
                    }

                    Text(
                        text = "Discover",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )

                    IconButton(
                        onClick = { navController.navigate("chats") }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = "Messages",
                            modifier = Modifier.size(28.dp),
                            tint = Color(0xFF2D3748)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            OrbitXFeed(
                navController = navController,
                userProfileData = userProfileData,
                postsList = postsList,
                onRefresh = {
                    viewModel.fetchPostsFromFirestore { posts ->
                        postsList = posts
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
    val viewModel: AuthViewModel = viewModel()
    val userProfileData by viewModel.userProfileData.observeAsState(emptyList())
    
    var activeStories by remember { mutableStateOf(emptyList<Story>()) }
    var selectedStoryIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchAllStories { fetchedStories ->
            activeStories = fetchedStories
        }
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
            composable("storyEditor/{uri}", arguments = listOf(navArgument("uri") { type = NavType.StringType })) { backStackEntry ->
                val encodedUriStr = backStackEntry.arguments?.getString("uri") ?: ""
                val decodedUriStr = String(Base64.decode(encodedUriStr, Base64.URL_SAFE or Base64.NO_WRAP))
                val decodedUri = Uri.parse(decodedUriStr)
                
                StoryEditorScreen(
                    imageUri = decodedUri,
                    viewModel = viewModel,
                    onDismiss = { navController.popBackStack() },
                    onUploadComplete = {
                        navController.popBackStack()
                        Toast.makeText(activity, "Story uploaded successfully!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        selectedStoryIndex?.let { index ->
            StoryViewerScreen(
                stories = activeStories,
                users = userProfileData,
                initialIndex = index,
                onClose = { selectedStoryIndex = null }
            )
        }
    }
}
@Composable
fun StoriesRow(navController: NavController, users: List<User>, activeStories: List<Story>, viewModel: AuthViewModel = viewModel(), onStoryClick: (Int) -> Unit) {
    // Show users who have active stories first, then fallback to dummy profiles
    val usersWithStories = users.filter { user -> activeStories.any { it.userId == user.userId } }
    val dummyUsers = users.filter { it.profilepictureurl.isNotEmpty() && !usersWithStories.contains(it) }
    val stories = (usersWithStories + dummyUsers).take(15)

    var isUploading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val encodedUri = Base64.encodeToString(uri.toString().toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
            navController.navigate("storyEditor/$encodedUri")
        }
    }

    val currentUserActiveStories = activeStories.filter { it.userId == currentUserId }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .border(
                                2.dp, 
                                if (currentUserActiveStories.isNotEmpty()) Brush.linearGradient(listOf(Color(0xFFCBD5E0), Color(0xFFCBD5E0))) else SolidColor(Color(0xFFE2E8F0)), 
                                RoundedCornerShape(20.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentUserActiveStories.isNotEmpty()) {
                                    val index = activeStories.indexOfFirst { it.userId == currentUserId }
                                    if (index != -1) {
                                        onStoryClick(index)
                                    }
                                } else {
                                    if (!isUploading) galleryLauncher.launch("image/*")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentUserActiveStories.isNotEmpty()) {
                            AsyncImage(
                                model = currentUserActiveStories.last().imageUrl,
                                contentDescription = "Your Story",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp))
                            )
                        } else if (isUploading) {
                            CircularProgressIndicator(color = Color(0xFF8A72B8), modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            val myUser = users.find { it.userId == currentUserId }
                            AsyncImage(
                                model = if (myUser?.profilepictureurl.isNullOrEmpty()) R.drawable.avataricon else myUser?.profilepictureurl,
                                contentDescription = "Your Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp))
                            )
                        }
                    }

                    if (currentUserActiveStories.isEmpty() && !isUploading) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = 6.dp, y = 6.dp)
                                .background(Color.White, CircleShape)
                                .padding(2.dp)
                                .background(Color(0xFF2196F3), CircleShape), // Blue plus
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your Story", fontSize = 12.sp, color = Color(0xFFA0AEC0))
            }
        }
        items(stories) { user ->
            // Determine ring state
            val userStories = activeStories.filter { it.userId == user.userId }
            val hasUnseenStories = userStories.any { !it.viewers.contains(currentUserId) }
            val ringBrush = if (hasUnseenStories) {
                Brush.linearGradient(listOf(Color(0xFFD82B7E), Color(0xFFF58529))) // Instagram-ish gradient
            } else {
                SolidColor(Color(0xFFCBD5E0)) // Muted Grey
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = user.profilepictureurl.ifEmpty { R.drawable.avataricon },
                    contentDescription = user.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, ringBrush, RoundedCornerShape(20.dp))
                        .padding(3.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            val index = activeStories.indexOfFirst { it.userId == user.userId }
                            if (index != -1) {
                                onStoryClick(index)
                            }
                        },
                    placeholder = painterResource(R.drawable.avataricon)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.username.take(10), // Truncate long names
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A72B8) // Purple text
                )
            }
        }
    }
}

@Composable
fun OrbitXFeed(
    navController: NavController,
    userProfileData: List<User>,
    postsList: List<Posts>,
    onRefresh: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context=LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    var activeStories by remember { mutableStateOf(emptyList<Story>()) }
    var selectedStoryIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchAllStories { fetchedStories ->
            activeStories = fetchedStories
        }
    }

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
                .background(Color(0xFFF4F3F8)), // Very soft purple background matching top bar
            contentPadding = PaddingValues(bottom = 80.dp, top = 0.dp)
        ) {
            item {
                StoriesRow(navController, userProfileData, activeStories) { index ->
                    selectedStoryIndex = index
                }
            }
            items(postsList) { post ->
                val user = userProfileData.find { it.userId == post.owneruid }

                OrbitXPost(
                    profileImageUrl = user?.profilepictureurl ?: "",
                    username = user?.username ?: "Unknown User",
                    location = post.location,
                    hashtag = post.hashtag,
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
                    onLikeChange = { liked ->
                        viewModel.toggleLikeForUser(post.postId, liked)
                    },
                    onBookmarkChange = { bookmarked ->
                        viewModel.toggleBookmarkForUser(post.postId, bookmarked)
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
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
    onBookmarkChange: (Boolean) -> Unit,
    hashtag: String,
    viewModel: AuthViewModel = viewModel(),
) {
    val initialCommentsCount: Int = 0
    var commentsCountState by remember { mutableStateOf(initialCommentsCount) }
    var isLiked by remember { mutableStateOf(initialIsLiked) }
    var likeCounter by remember { mutableStateOf(likesCount) }
    var isBookmarked by remember { mutableStateOf(false) }

    LaunchedEffect(postuid) {
        viewModel.checkIfUserLikedPost(postuid) { liked ->
            isLiked = liked
        }
        viewModel.checkIfUserBookmarkedPost(postuid) { bookmarked ->
            isBookmarked = bookmarked
        }
    }
    var isCommentDialogOpen by remember { mutableStateOf(false) }
    var isImageExpanded by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var showHeartAnimation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val formattedTimestamp = remember(timestamp) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        dateFormat.format(Date(timestamp))
    }
    var usrname by remember { mutableStateOf("") }
    var profilepicurl by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("") }
    var hash by remember { mutableStateOf("") }

    viewModel.fetchusername(owneruserid) { it -> usrname = it }
    viewModel.fetchProfileurl(owneruserid) { it -> profilepicurl = it }
    viewModel.fetchLocation(postuid) { it -> loc = it }
    viewModel.fetchHashtag(postuid) { it -> hash = it }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat look
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp) // Clean consistent padding around content
        ) {
            // User Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(if (profilepicurl.isNotEmpty()) profilepicurl else profileImageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.avataricon),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (usrname.isNotEmpty()) usrname else username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    val displayLoc = if (loc.isNotEmpty()) loc else location
                    if (displayLoc.isNotEmpty()) {
                        Text(
                            text = displayLoc,
                            fontSize = 13.sp,
                            color = Color(0xFFA0AEC0)
                        )
                    }
                }

                IconButton(
                    onClick = { /* Add options menu if needed */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options", tint = Color(0xFFA0AEC0))
                }
            }

            // Post Content Row (Image + Icons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Post Image Context
                Box(
                    modifier = Modifier
                        .weight(1f) // Takes available space
                        .clip(RoundedCornerShape(20.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (!isLiked) {
                                        isLiked = true
                                        likeCounter += 1
                                        onLikeChange(true)
                                    }
                                    showHeartAnimation = true
                                    coroutineScope.launch {
                                        delay(800)
                                        showHeartAnimation = false
                                    }
                                },
                                onTap = {
                                    isImageExpanded = true
                                }
                            )
                        }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Post image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // 1:1 Square aspect ratio
                            .background(Color(0xFFF7FAFC))
                    )
                    
                    HeartAnimationOverlay(
                        visible = showHeartAnimation,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Vertical Actions column on the right
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(top = 24.dp, end = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color(0xFFE55D5D) else Color(0xFF8A72B8),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isLiked = !isLiked
                                likeCounter += if (isLiked) 1 else -1
                                onLikeChange(isLiked)
                            }
                    )

                    Icon(
                        painter = painterResource(R.drawable.speech_bubble), // Replace back with custom icon if desired
                        contentDescription = "Comment",
                        tint = Color(0xFF8A72B8),
                        modifier = Modifier
                            .size(22.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isCommentDialogOpen = true
                            }
                    )

                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = "Share",
                        tint = Color(0xFF8A72B8),
                        modifier = Modifier
                            .size(22.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onShare()
                            }
                    )

                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color(0xFF8A72B8),
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isBookmarked = !isBookmarked
                                onBookmarkChange(isBookmarked)
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post Text
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF4A5568)
                )
            }
            
            // Interaction row underneath text
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dummy mini avatars row
                val avatarList = listOf(
                    "https://randomuser.me/api/portraits/women/44.jpg",
                    "https://randomuser.me/api/portraits/men/46.jpg",
                    "https://randomuser.me/api/portraits/women/65.jpg"
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    avatarList.forEach { avatarModel ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatarModel)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = buildAnnotatedString {
                        if (likeCounter > 0) {
                            append("liked by ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF8A72B8))) {
                                append(if (usrname.isNotEmpty()) usrname else username) // Using poster name for demo logic
                            }
                            if (likeCounter > 1) {
                                append(" and ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF8A72B8))) {
                                    append("${likeCounter - 1} others")
                                }
                            }
                        } else {
                            append("Be the first to like this")
                        }
                    },
                    fontSize = 12.sp,
                    color = Color(0xFFA0AEC0) // Grayscale text color
                )
            }
        }
    }
    if (isCommentDialogOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        var commentsList by remember { mutableStateOf<List<com.example.orbitx.ViewModel.Comment>>(emptyList()) }
        var isLoadingComments by remember { mutableStateOf(false) }

        LaunchedEffect(isCommentDialogOpen) {
            if (isCommentDialogOpen) {
                isLoadingComments = true
                viewModel.fetchComments(postuid) { comments ->
                    commentsList = comments
                    isLoadingComments = false
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = { isCommentDialogOpen = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.6f) // Take up 60% of screen height
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Comments",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally),
                    color = Color(0xFF2D3748)
                )

                if (isLoadingComments) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF8A72B8))
                    }
                } else if (commentsList.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No comments yet.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(commentsList) { comment ->
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Anonymous user", // Placeholder as Comment model lacks username currently
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = comment.commentText, 
                                        fontSize = 14.sp, 
                                        color = Color(0xFF4A5568)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text(text = "Add a comment...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF8A72B8),
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    onComment(commentText)
                                    viewModel.addComment(postuid, commentText)
                                    viewModel.updateCommentsCount(postuid, increment = true)
                                    viewModel.getPostCommentsCount(postuid) { count ->
                                        commentsCountState = count
                                    }
                                    viewModel.fetchComments(postuid) { updatedComments ->
                                        commentsList = updatedComments
                                    }
                                    commentText = ""
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = "Post comment",
                                tint = if (commentText.isNotBlank()) Color(0xFF8A72B8) else Color.Gray
                            )
                        }
                    }
                )
            }
        }
    }

    if (isImageExpanded) {
        var scale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        Dialog(
            onDismissRequest = { isImageExpanded = false },
            properties = DialogProperties(usePlatformDefaultWidth = false) // Allow full screen
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { isImageExpanded = false }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Expanded Post image",
                    contentScale = ContentScale.Fit, // Fit to screen width/height
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = maxOf(1f, minOf(scale * zoom, 5f))
                                val maxX = (size.width * (scale - 1)) / 2
                                val minX = -maxX
                                offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x * scale))
                                val maxY = (size.height * (scale - 1)) / 2
                                val minY = -maxY
                                offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y * scale))
                            }
                        }
                )
                
                // Add a close button overlay
                IconButton(
                    onClick = { isImageExpanded = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close full screen",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
