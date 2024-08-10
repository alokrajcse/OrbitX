package com.example.orbitx.Views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.orbitx.ChatRepository.ChatRepository
import com.example.orbitx.ChatRepository.User

@Composable
fun otherUserProfileSection(
    userProfile: UserProfile,
    data: String,
    modifier: Modifier = Modifier
) {
    // State to hold the fetched username
    var username by remember { mutableStateOf<String?>(null) }


    // Fetch the username
    LaunchedEffect(data) {
        getUsername(data) { fetchedUsername ->
            username = fetchedUsername
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Profile picture and Username/Bio
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserProfilePicture(
                imageUrl = userProfile.profilePictureUrl,
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
                    text = userProfile.bio,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        FollowButton(
            isFollowing = userProfile.isFollowing,
            onClick = { /* handle follow/unfollow action */ },
            modifier = Modifier.padding(top = 16.dp)
        )

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
                value = userProfile.followerCount.toString()
            )
            ProfileStat(
                label = "Following",
                value = userProfile.followingCount.toString()
            )
        }
    }
}

@Composable
private fun UserProfilePicture(
    imageUrl: String,
    size: Dp,
    modifier: Modifier = Modifier
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
            containerColor = if (isFollowing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
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
    val profilePictureUrl: String,
    val username: String,
    val bio: String,
    val isFollowing: Boolean,
    val postCount: Int,
    val followerCount: Int,
    val followingCount: Int
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
