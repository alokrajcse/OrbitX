package com.example.orbitx.Views

import com.example.orbitx.R
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.roundToInt
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.orbitx.ViewModel.AuthViewModel
import com.example.orbitx.ViewModel.Story
import com.example.orbitx.ChatRepository.User
import com.google.firebase.auth.FirebaseAuth

@Composable
fun StoryViewerScreen(
    stories: List<Story>,
    users: List<User>,
    initialIndex: Int = 0,
    viewModel: AuthViewModel = viewModel(),
    onClose: () -> Unit
) {
    if (stories.isEmpty()) {
        onClose()
        return
    }

    var currentIndex by remember { mutableStateOf(initialIndex.coerceIn(0, stories.lastIndex)) }
    var isPaused by remember { mutableStateOf(false) }

    val currentStory = stories[currentIndex]
    val currentUser = users.find { it.userId == currentStory.userId }
    val currentUserStoriesCount = stories.count { it.userId == currentStory.userId }
    val currentUserStartIndex = stories.indexOfFirst { it.userId == currentStory.userId }
    val localIndex = currentIndex - currentUserStartIndex
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val animatableProgress = remember { Animatable(0f) }

    LaunchedEffect(currentIndex, isPaused) {
        if (!currentStory.viewers.contains(currentUserId)) {
            viewModel.markStoryAsSeen(currentStory.storyId)
        }
        
        if (!isPaused) {
            animatableProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = ((1f - animatableProgress.value) * 5000).toInt(),
                    easing = LinearEasing
                )
            )
            // Animation finished -> next story
            if (currentIndex < stories.lastIndex) {
                currentIndex++
                animatableProgress.snapTo(0f)
            } else {
                onClose() // Reached the end
            }
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(currentIndex) {
                    detectTapGestures(
                        onPress = {
                            isPaused = true
                            val released = tryAwaitRelease()
                            isPaused = false

                            if (released) {
                                val tapPositionX = it.x
                                val screenWidth = size.width

                                if (tapPositionX < screenWidth * 0.3f) {
                                    // Tap left: Previous story
                                    if (currentIndex > 0) {
                                        currentIndex--
                                        animatableProgress.snapTo(0f)
                                    } else {
                                        // Start over if tapping back on first story
                                        animatableProgress.snapTo(0f)
                                    }
                                } else {
                                    // Tap right: Next story
                                    if (currentIndex < stories.lastIndex) {
                                        currentIndex++
                                        animatableProgress.snapTo(0f)
                                    } else {
                                        onClose()
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            // Image Layer
            AsyncImage(
                model = currentStory.imageUrl,
                contentDescription = "Story Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            // Render Stickers
            currentStory.stickerElements.forEach { sticker ->
                Text(
                    text = sticker.stickerId,
                    fontSize = 60.sp,
                    modifier = Modifier
                        .offset { IntOffset(sticker.offsetX.roundToInt(), sticker.offsetY.roundToInt()) }
                        .graphicsLayer(
                            scaleX = sticker.scale,
                            scaleY = sticker.scale,
                            rotationZ = sticker.rotation
                        )
                )
            }

            // Render Texts
            currentStory.textElements.forEach { textEl ->
                val parsedColor = try {
                    val hex = if (textEl.colorString.startsWith("#")) textEl.colorString else "#${textEl.colorString}"
                    Color(android.graphics.Color.parseColor(hex))
                } catch (e: Exception) {
                    try {
                        Color(java.lang.Long.parseLong(textEl.colorString, 16).toInt())
                    } catch (e2: Exception) {
                        Color.White
                    }
                }
                
                val font = when (textEl.fontFamily) {
                    "SERIF" -> FontFamily.Serif
                    "MONOSPACE" -> FontFamily.Monospace
                    "CURSIVE" -> FontFamily.Cursive
                    else -> FontFamily.Default
                }
                
                val bgModifier = when (textEl.bgStyle) {
                    "SOLID" -> Modifier.background(parsedColor, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                    "SEMI" -> Modifier.background(parsedColor.copy(alpha=0.5f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                    else -> Modifier
                }
                
                val textColor = if (textEl.bgStyle == "SOLID" || textEl.bgStyle == "SEMI") {
                    if (parsedColor == Color.White || parsedColor == Color.Yellow) Color.Black else Color.White
                } else {
                    parsedColor
                }
                
                Text(
                    text = textEl.text,
                    color = textColor,
                    fontFamily = font,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset { IntOffset(textEl.offsetX.roundToInt(), textEl.offsetY.roundToInt()) }
                        .graphicsLayer(
                            scaleX = textEl.scale,
                            scaleY = textEl.scale,
                            rotationZ = textEl.rotation
                        )
                        .then(bgModifier)
                )
            }

            // Overlay UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp)
            ) {
                // Story Progress Bars (Grouped to current user)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 0 until currentUserStoriesCount) {
                        val progress = when {
                            i < localIndex -> 1f
                            i == localIndex -> animatableProgress.value
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }

                // User Info Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    // Profile Picture
                    AsyncImage(
                        model = if (currentUser?.profilepictureurl.isNullOrEmpty()) R.drawable.avataricon else currentUser?.profilepictureurl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = currentUser?.username ?: "User ${currentStory.userId.take(5)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Delete Button (if owned)
                    if (currentStory.userId == currentUserId) {
                        IconButton(onClick = {
                            isPaused = true
                            viewModel.deleteStory(currentStory.storyId) { success ->
                                if (success) onClose() else isPaused = false
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Story",
                                tint = Color.White
                            )
                        }
                    }

                    // Close button
                    IconButton(onClick = { onClose() }) {
                        Text(
                            text = "X",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // Bottom Interactions (if NOT owned)
            if (currentStory.userId != currentUserId) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Send Message "Field"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Send Message",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Heart Icon
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Story",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
