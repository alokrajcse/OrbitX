package com.example.orbitx.Views

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.orbitx.ChatRepository.ChatRepository
import com.example.orbitx.ChatRepository.User
import com.example.orbitx.ChatRepository.fetchBio
import com.example.orbitx.ChatRepository.fetchFollowerCount
import com.example.orbitx.ChatRepository.fetchFollowingCount
import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.Notification.topicRepository
import com.example.orbitx.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun otherUserProfileSection(
    userProfile: UserProfile,
    data: String,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Profile") }, colors = TopAppBarDefaults.topAppBarColors(
                colorResource(id = R.color.orange)), navigationIcon = { IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back")

                
            }})
        }
    ) { paddingValues ->

        // State to hold the fetched username
        var username by remember { mutableStateOf<String?>(null) }

        var isFollowing by remember { mutableStateOf(userProfile.isFollowing) }

        var followerCount by remember { mutableStateOf(userProfile.followerCount) }
        var followingCount by remember { mutableStateOf(userProfile.followingCount) }

        var bio by remember { mutableStateOf(userProfile.bio) }
        var profilePictureUrl by remember { mutableStateOf("") }




        LaunchedEffect(data) {
            getUsername(data) { fetchedUsername ->
                username = fetchedUsername
            }

            fetchFollowerCount(data) { count -> followerCount = count }
            fetchFollowingCount(data) { count -> followingCount = count }

            fetchBio(data){b->bio=b}
            fetchProfileurl(data){it->profilePictureUrl=it}

            val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
            Firebase.database.getReference("users").child(currentUserUid).child("following").child(data)
                .get().addOnSuccessListener { snapshot ->
                    isFollowing = snapshot.exists()
                }



        }

        Box (modifier = Modifier.padding(paddingValues)){

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Profile picture and Username/Bio
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        UserProfilePicture(
                            imageUrl = profilePictureUrl,
                            size = 100.dp
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .alignByBaseline()
                        ) {
                            Text(
                                text = username ?: "Loading...",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = bio,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Follower count
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat(
                            label = "Posts",
                            value = userProfile.postCount.toString()
                        )
                        ProfileStat(
                            label = "Followers",
                            value = followerCount.toString()
                        )
                        ProfileStat(
                            label = "Following",
                            value = followingCount.toString()
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FollowButton(
                            isFollowing = isFollowing,
                            onClick = {
                                val userUid = FirebaseAuth.getInstance().currentUser!!.uid
                                val ref = Firebase.database.getReference("users").child(userUid).child("following").child(data)
                                val ref2 = Firebase.database.getReference("users").child(data).child("followers").child(userUid)

                                ref.get().addOnSuccessListener { snapshot ->
                                    val current = snapshot.getValue(Boolean::class.java) ?: false

                                    if (current) {
                                        ref2.removeValue().addOnSuccessListener {
                                            ref.removeValue().addOnSuccessListener {
                                                isFollowing = false
                                                followerCount--
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    topicRepository().unsubscribe("messagefrom"+data+"to"+userUid)
                                                }
                                            }
                                        }
                                    } else {
                                        ref.setValue(true).addOnSuccessListener {
                                            ref2.setValue(true).addOnSuccessListener {
                                                isFollowing = true
                                                followerCount++
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    topicRepository().subscribe("messagefrom"+data+"to"+userUid)
                                                }
                                            }
                                        }
                                    }
                                }.addOnFailureListener {
                                    // Handle any error cases if needed
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { navController.navigate("MainChatScreen/$data") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.orange))
                        ) {
                            Text(text = "Message", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }

    }


}
@Composable
private fun UserProfilePicture(
    imageUrl: String,
    size: Dp,
    modifier: Modifier = Modifier
)
{
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build()
        ),
        contentDescription = "Profile picture",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    )
}

@Composable
private fun FollowButton(
    isFollowing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFollowing) Color.DarkGray else colorResource(id = R.color.orange)
        )
    ) {
        Text(
            text = if (isFollowing) "Unfollow" else "Follow",
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun ProfileStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class UserProfile(
    val profilePictureUrl: String="",
    val username: String="",
    val bio: String="This is my bio",
    val isFollowing: Boolean=false,
    val postCount: Int=0,
    val followerCount: Int=0,
    val followingCount: Int=0,

    )

fun getUsername(data: String, callback: (String?) -> Unit) {
    ChatRepository.getUsers().child(data).addValueEventListener(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Retrieve the user data as a User object
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                callback(user.username)
            } else {
                callback(null) // User not found
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle possible errors
            Log.w("Firebase", "Failed to read value.", error.toException())
            callback(null)
        }
    })
}



